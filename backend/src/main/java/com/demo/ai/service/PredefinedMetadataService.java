package com.demo.ai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.demo.ai.model.DevelopmentEntity;
import com.demo.ai.model.DevelopmentMetadataEntity;
import com.demo.ai.model.DocumentEntity;
import com.demo.ai.model.bean.DocumentChunkDTO;
import com.demo.ai.repository.DevelopmentMetadataRepository;
import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.DocumentChunkRepository;
import com.demo.ai.repository.DocumentRepository;

@Service
public class PredefinedMetadataService {

	private final DevelopmentMetadataRepository metadataRepository;
	private final DevelopmentRepository developmentRepository;
	private final DocumentChunkRepository documentChunkRepository;
	private final DocumentRepository documentRepository;
	private final AIService aiService;

	public PredefinedMetadataService(DevelopmentMetadataRepository metadataRepository, DevelopmentRepository developmentRepository,
			DocumentRepository documentRepository, AIService aiService, DocumentChunkRepository documentChunkRepository) {

		this.metadataRepository = metadataRepository;
		this.developmentRepository = developmentRepository;
		this.documentRepository = documentRepository;
		this.aiService = aiService;
		this.documentChunkRepository = documentChunkRepository;

	}

	public ResponseEntity<String> generateMetadata(Long developmentId) {

		Optional<DevelopmentEntity> developmentOpt = developmentRepository.findById(developmentId);

		if (developmentOpt.isEmpty()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Development ID non trovato");

		}

		DevelopmentEntity development = developmentOpt.get();

		metadataRepository.deleteByDevelopmentId(developmentId);

		List<DocumentChunkDTO> relevantChunks = documentChunkRepository.findChunksByDevelopmentId(developmentId);

		StringBuilder context = new StringBuilder("Di seguito i documenti più pertinenti:\n");

		for (DocumentChunkDTO doc : relevantChunks) {

			context.append("- ").append(doc.getChunkText()).append("\n");

		}

		String functionalRequirements = aiService.callOpenAI(context + "\nEstrarre i requisiti funzionali per: " + development.getDescription());
		String useCases = aiService.callOpenAI(context + "\nGenerare gli use case per: " + development.getDescription());
		String inputOutputData = aiService.callOpenAI(context + "\nIdentificare input e output principali per: " + development.getDescription());
		String technicalDependencies = aiService.callOpenAI(context + "\nElenca le dipendenze tecniche per: " + development.getDescription());

		DevelopmentMetadataEntity metadata = metadataRepository.findByDevelopmentId(developmentId).orElse(new DevelopmentMetadataEntity());
		metadata.setDevelopment(development);
		metadata.setFunctionalRequirements(functionalRequirements);
		metadata.setUseCases(useCases);
		metadata.setInputOutputData(inputOutputData);
		metadata.setTechnicalDependencies(technicalDependencies);

		metadataRepository.save(metadata);
		return ResponseEntity.ok("Metadati generati con successo per lo sviluppo: " + developmentId);

	}

//	/**
//	 * Recupera i documenti più simili alla richiesta usando la similarità coseno.
//	 */
//	private List<DocumentEntity> findRelevantDocuments(List<Float> queryEmbedding) {
//
//		List<DocumentEntity> allDocuments = documentRepository.findAll().stream().map(obj -> (DocumentEntity) obj).collect(Collectors.toList());
//
//		// Ordina i documenti in base alla similarità coseno con la query
//		return allDocuments.stream()
//				.sorted(Comparator.comparingDouble(doc -> EmbeddingUtils.cosineSimilarity(queryEmbedding, ((DocumentEntity) doc).getEmbedding())).reversed())
//				.limit(5).collect(Collectors.toList());
//
//	}

	public ResponseEntity<DevelopmentMetadataEntity> getMetadata(Long developmentId) {

		Optional<DevelopmentMetadataEntity> metadata = metadataRepository.findByDevelopmentId(developmentId);
		return metadata.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

	}

}
