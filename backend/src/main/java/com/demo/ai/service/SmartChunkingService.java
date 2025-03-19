package com.demo.ai.service;

import com.demo.ai.model.DevelopmentEntity;
import com.demo.ai.model.DocumentChunkEntity;
import com.demo.ai.model.DocumentEntity;
import com.demo.ai.model.ProjectEntity;
import com.demo.ai.model.bean.ChunkData;
import com.demo.ai.model.bean.DocumentChunkDTO;
import com.demo.ai.model.bean.DocumentWithChunksDTO;
import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.DocumentChunkRepository;
import com.demo.ai.repository.DocumentRepository;
import com.demo.ai.util.EmbeddingUtils;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartChunkingService {

	@Value("${github.access.token}")
	private String accessToken;

	private final ProjectService projectService;
	private final AIService aiService;

	private final LlamaIndexService llamaIndexService;
	private final DocumentRepository documentRepository;
	private final DevelopmentRepository developmentRepository;
	private final DocumentChunkRepository documentChunkRepository;

	private final Tika tika = new Tika();

	private LanguageDetectorME languageDetector;

	private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(Map.entry("eng", "english"), Map.entry("ita", "italian"), Map.entry("fra", "french"),
			Map.entry("deu", "german"), Map.entry("spa", "spanish"), Map.entry("nld", "dutch"), Map.entry("por", "portuguese"), Map.entry("rus", "russian"),
			Map.entry("zho", "chinese"), Map.entry("jpn", "japanese"), Map.entry("kor", "korean"));

	public SmartChunkingService(ProjectService projectService, DocumentRepository documentRepository, AIService aiService, LlamaIndexService llamaIndexService,
                                   DocumentChunkRepository documentChunkRepository, DevelopmentRepository developmentRepository) {

		this.projectService = projectService;
		this.documentRepository = documentRepository;
		this.aiService = aiService;
		this.llamaIndexService = llamaIndexService;
		this.documentChunkRepository = documentChunkRepository;
		this.developmentRepository = developmentRepository;

	}

	public void createChunks() {
		//invocare lamaindex per effettuare l'embedding dei documenti
	}

	public String searchRelevantChunksForDevelopmentId(Long developmentId) {

		//invocare lamaindex per effettuare la ricerca dei documenti e il salvataggio sul db

		return "Scansione completata, selezionati i documenti più rilevanti.";

	}

	public List<DocumentWithChunksDTO> getChunksGroupedByDocument(Long developmentId) {

		DevelopmentEntity development = developmentRepository.getReferenceById(developmentId);

		String query = "Recupera i documenti attinenti al seguente quesito: \"" +development.getDescription() + "\"";

		List<DocumentChunkDTO> flatChunks = llamaIndexService.filterDocumentsWithReAG(query);

		// 🔹 Raggruppiamo i chunk per documentId e documentName
		Map<Long, List<DocumentChunkDTO>> groupedChunks = flatChunks.stream().collect(Collectors.groupingBy(DocumentChunkDTO::getDocumentId));

		// 🔹 Convertiamo la mappa in una lista di DTO
		return groupedChunks.entrySet().stream().map(entry -> {

			Long documentId = entry.getKey();
			String documentName = entry.getValue().get(0).getDocumentName();
			List<String> chunkTitles = entry.getValue().stream().map(DocumentChunkDTO::getChunkTitle).collect(Collectors.toList());
			List<String> chunkTextes = entry.getValue().stream().map(DocumentChunkDTO::getChunkText).collect(Collectors.toList());

			return new DocumentWithChunksDTO(documentId, documentName, chunkTitles, chunkTextes);

		}).collect(Collectors.toList());

	}





}
