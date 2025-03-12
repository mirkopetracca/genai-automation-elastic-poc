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


public class SmartChunkingServiceBkp {
/*
	@Value("${github.access.token}")
	private String accessToken;

	private final ProjectService projectService;
	private final AIService aiService;
	private final DocumentRepository documentRepository;
	private final DevelopmentRepository developmentRepository;
	private final DocumentChunkRepository documentChunkRepository;

	private final Tika tika = new Tika();

	private LanguageDetectorME languageDetector;

	private static final Map<String, String> LANGUAGE_MAP = Map.ofEntries(Map.entry("eng", "english"), Map.entry("ita", "italian"), Map.entry("fra", "french"),
			Map.entry("deu", "german"), Map.entry("spa", "spanish"), Map.entry("nld", "dutch"), Map.entry("por", "portuguese"), Map.entry("rus", "russian"),
			Map.entry("zho", "chinese"), Map.entry("jpn", "japanese"), Map.entry("kor", "korean"));

	public SmartChunkingServiceBkp(ProjectService projectService, DocumentRepository documentRepository, AIService aiService,
                                   DocumentChunkRepository documentChunkRepository, DevelopmentRepository developmentRepository) {

		this.projectService = projectService;
		this.documentRepository = documentRepository;
		this.aiService = aiService;
		this.documentChunkRepository = documentChunkRepository;
		this.developmentRepository = developmentRepository;

	}

	public void createChunks() {

		try {

			List<ProjectEntity> projects = projectService.getAllProjects();

			for (ProjectEntity project : projects) {

				String documentsPath = project.getDocumentsPath();
				String repoUrl = project.getRepositoryUrl();

				GitHub github = GitHub.connectUsingOAuth(accessToken);
				String repoName = repoUrl.replace("https://github.com/", "");
				GHRepository repository = github.getRepository(repoName);

				scanDirectory(repository, documentsPath);

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public String searchRelevantChunksForDevelopmentId(Long developmentId) {

		DevelopmentEntity development = developmentRepository.findById(developmentId)
				.orElseThrow(() -> new IllegalArgumentException("Development ID non trovato"));

		// String devTitle = development.getTitle();
		String devDescription = development.getDescription();

		List<Float> queryEmbedding = aiService.generateEmbedding(devDescription);

		List<DocumentChunkEntity> relevantChunks = documentChunkRepository.searchHybrid(EmbeddingUtils.convertFloatListToArray(queryEmbedding), devDescription);

		List<DocumentChunkEntity> filteredChunks = filterRelevantChunks(relevantChunks, devDescription);

		associateChunksToDevelopment(development, filteredChunks);

		return "Scansione completata, selezionati i documenti più rilevanti.";

	}

	public List<DocumentWithChunksDTO> getChunksGroupedByDocument(Long developmentId) {

		List<DocumentChunkDTO> flatChunks = documentChunkRepository.findChunksByDevelopmentId(developmentId);

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

	private void scanDirectory(GHRepository repository, String path) throws IOException {

		List<GHContent> contents = repository.getDirectoryContent(path);

		for (GHContent file : contents) {

			if (file.isFile()) {

				processFile(file);

			} else if (file.isDirectory()) {

				scanDirectory(repository, file.getPath());

			}

		}

	}

	private void processFile(GHContent file) {

		try (InputStream inputStream = file.read()) {

			String fileName = file.getName();
			String extractedText = tika.parseToString(inputStream);

			Optional<DocumentEntity> documents = documentRepository.findByFileName(fileName);

			if (!documents.isPresent()) {

				System.out.println("Processing " + fileName);

				DocumentEntity document = createNewDocument(fileName, extractedText);

				String chunkedResponse = executeSmartChunking(extractedText);

				List<ChunkData> chunks = parseChunkedResponse(chunkedResponse);

				saveChunksWithEmbeddings(document, chunks);

			}

		} catch (IOException | TikaException e) {

			System.err.println("Errore nella lettura del file: " + file.getName() + " - " + e.getMessage());

		}

	}

	private String executeSmartChunking(String extractedText) {

		String prompt = "Dividi il seguente testo in sezioni coerenti e significative. "
				+ "Per ogni sezione, restituisci un titolo riassuntivo e il contenuto separati da `@@`. "
				+ "Mantieni le sezioni separate con `###`. Ogni sezione deve avere un concetto chiaro " + "e non superare le 500 parole.\n\n" + "Testo:\n"
				+ extractedText;

		return aiService.callOpenAI(prompt, 0.2);

	}

	private DocumentEntity createNewDocument(String fileName, String extractedText) {

		DocumentEntity document = new DocumentEntity();
		document.setFileName(fileName);
		document.setExtractedText(extractedText);
		documentRepository.save(document);
		return document;

	}

	private static List<ChunkData> parseChunkedResponse(String jsonResponse) {

		List<ChunkData> chunks = new ArrayList<>();

		String[] sections = jsonResponse.split("###");

		for (String section : sections) {

			String[] parts = section.split("@@");

			if (parts.length == 2) {

				chunks.add(new ChunkData(parts[0].trim(), parts[1].trim()));

			}

		}

		return chunks;

	}

	private void saveChunksWithEmbeddings(DocumentEntity document, List<ChunkData> chunks) {

		for (ChunkData chunkData : chunks) {

			List<Float> embedding = aiService.generateEmbedding(chunkData.getContent());
			String detectedLanguage = detectLanguage(chunkData.getContent());

			DocumentChunkEntity chunk = new DocumentChunkEntity();
			chunk.setDocument(document);
			chunk.setTitle(chunkData.getTitle());
			chunk.setChunkText(chunkData.getContent());
			chunk.setEmbedding(ArrayUtils.toPrimitive(embedding.toArray(new Float[0]), 0.0F));
			chunk.setLanguage(detectedLanguage);
			documentChunkRepository.save(chunk);

		}

	}

	private String detectLanguage(String content) {

		try (InputStream langModelStream = getClass().getResourceAsStream("/models/langdetect.bin")) {

			this.languageDetector = new LanguageDetectorME(new LanguageDetectorModel(langModelStream));
			return getLanguageName(languageDetector.predictLanguage(content).getLang());

		} catch (Exception e) {

			return "simple";

		}

	}

	private static String getLanguageName(String iso639_3) {

		return LANGUAGE_MAP.getOrDefault(iso639_3, "simple");

	}

	private List<DocumentChunkEntity> filterRelevantChunks(List<DocumentChunkEntity> chunks, String query) {

		StringBuilder chunksText = new StringBuilder();

		for (DocumentChunkEntity chunk : chunks) {

			chunksText.append("**Id:** ").append(chunk.getId()).append("\n");
			chunksText.append("**Titolo:** ").append(chunk.getTitle()).append("\n");
			chunksText.append(chunk.getChunkText()).append("\n\n---\n\n");

		}

		String prompt = "La query dell'utente è: \"" + query + "\"\n\n"
				+ "Di seguito ci sono alcuni estratti di documenti. Per ogni estratto, indica se è rilevante indicando soltanto \"ID,(SI/NO)\" senza aggiungere altro:\n\n"
				+ chunksText;

		String response = aiService.callOpenAI(prompt, 0.2);

		return parseRelevantChunks(response, chunks);

	}

	private List<DocumentChunkEntity> parseRelevantChunks(String response, List<DocumentChunkEntity> chunks) {

		List<Long> relevantIds = new ArrayList<>();

		for (String line : response.split("\n")) {

			String[] parts = line.split(",");

			if (parts.length == 2 && parts[1].trim().equalsIgnoreCase("SI")) {

				try {

					relevantIds.add(Long.parseLong(parts[0].trim())); // 🔹 Estraiamo l'ID

				} catch (NumberFormatException e) {

					System.err.println("Errore nel parsing dell'ID: " + line);

				}

			}

		}

		return chunks.stream().filter(chunk -> relevantIds.contains(chunk.getId())).collect(Collectors.toList());

	}

	private void associateChunksToDevelopment(DevelopmentEntity development, List<DocumentChunkEntity> filteredChunks) {

		Set<Long> chunkIds = filteredChunks.stream().map(DocumentChunkEntity::getId).collect(Collectors.toSet());

		List<DocumentChunkEntity> realChunks = documentChunkRepository.findAllById(chunkIds);

		for (DocumentChunkEntity chunk : realChunks) {

			chunk.getDevelopments().add(development);

		}

		development.getDocumentChunks().clear();
		developmentRepository.save(development);

		development.getDocumentChunks().addAll(realChunks);

		documentChunkRepository.saveAll(realChunks);
		developmentRepository.save(development);

	}
*/
}
