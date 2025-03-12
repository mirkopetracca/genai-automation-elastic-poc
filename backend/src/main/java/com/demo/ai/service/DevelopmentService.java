package com.demo.ai.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.demo.ai.model.DevelopmentEntity;
import com.demo.ai.model.DocumentEntity;
import com.demo.ai.model.ProjectEntity;
import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.DocumentRepository;
import com.demo.ai.util.EmbeddingUtils;

import io.pinecone.clients.Index;
import io.pinecone.configs.PineconeConfig;
import io.pinecone.configs.PineconeConnection;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.unsigned_indices_model.VectorWithUnsignedIndices;

@Service
public class DevelopmentService {

	private final DevelopmentRepository developmentRepository;
	private final DocumentRepository documentRepository;
	private final AIService aiService;

	@Value("${github.access.token}")
	private String accessToken;

	private final Tika tika = new Tika();

	@Value("${github.access.token}") // 🔹 Recupera il valore dal file di configurazione
	private String githubAccessToken;

	public DevelopmentService(DevelopmentRepository developmentRepository, DocumentRepository documentRepository, AIService aiService) {

		this.developmentRepository = developmentRepository;
		this.documentRepository = documentRepository;
		this.aiService = aiService;

	}

//	public ResponseEntity<String> scanAndAssociateDocuments(Long developmentId, int topX) {
//
//		DevelopmentEntity development = developmentRepository.findById(developmentId)
//				.orElseThrow(() -> new IllegalArgumentException("Development ID non trovato"));
//		ProjectEntity project = development.getProject();
//
//		if (project == null || project.getDocumentsPath() == null || project.getRepositoryUrl() == null) {
//
//			return ResponseEntity.badRequest().body("Repository o documentsPath non configurato per il progetto.");
//
//		}
//
//		try {
//
//			GitHub github = GitHub.connectUsingOAuth(githubAccessToken);
//			String repoName = project.getRepositoryUrl().replace("https://github.com/", "");
//			GHRepository repository = github.getRepository(repoName);
//
//			Set<String> relevantDocuments = new HashSet<>();
//			scanDirectory(repository, project.getDocumentsPath(), development, relevantDocuments, topX);
//
//			// 🔹 Ordiniamo i documenti per similarità e prendiamo i primi X
//			List<DocumentEntity> sortedDocs = new ArrayList<>(development.getDocuments());
//			sortedDocs.sort((doc1, doc2) -> Double.compare(
//					(EmbeddingUtils.cosineSimilarity(aiService.generateEmbedding(development.getTitle()), doc2.getEmbedding())
//							+ EmbeddingUtils.cosineSimilarity(aiService.generateEmbedding(development.getDescription()), doc2.getEmbedding())) / 2,
//					(EmbeddingUtils.cosineSimilarity(aiService.generateEmbedding(development.getTitle()), doc1.getEmbedding())
//							+ EmbeddingUtils.cosineSimilarity(aiService.generateEmbedding(development.getDescription()), doc1.getEmbedding())) / 2));
//
//			List<DocumentEntity> selectedDocs = sortedDocs.subList(0, Math.min(topX, sortedDocs.size()));
//
//			// 🔹 Rimuoviamo documenti non più rilevanti
//			development.getDocuments().retainAll(selectedDocs);
//			documentRepository.saveAll(selectedDocs);
//			developmentRepository.save(development);
//
//			return ResponseEntity.ok("Scansione completata, selezionati i top " + topX + " documenti più rilevanti.");
//
//		} catch (IOException e) {
//
//			return ResponseEntity.internalServerError().body("Errore nell'accesso ai documenti su GitHub: " + e.getMessage());
//
//		}
//
//	}
//
//	private void scanDirectory(GHRepository repository, String path, DevelopmentEntity development, Set<String> relevantDocuments, int topX)
//			throws IOException {
//
//		List<GHContent> contents = repository.getDirectoryContent(path);
//
//		for (GHContent file : contents) {
//
//			if (file.isFile()) {
//
//				processFile(file, development, relevantDocuments, topX); // 🔹 Passiamo l'insieme dei documenti rilevanti
//
//			} else if (file.isDirectory()) {
//
//				scanDirectory(repository, file.getPath(), development, relevantDocuments, topX); // 🔹 Ricorsione nelle sottocartelle
//
//			}
//
//		}
//
//	}
//
//	private void processFile(GHContent file, DevelopmentEntity development, Set<String> relevantDocuments, int topX) {
//
//		try (InputStream inputStream = file.read()) {
//
//			String extractedText = tika.parseToString(inputStream);
//			List<Float> docTextEmbedding = aiService.generateEmbedding(extractedText);
//			List<Float> titleEmbedding = aiService.generateEmbedding(development.getTitle());
//			List<Float> descriptionEmbedding = aiService.generateEmbedding(development.getDescription());
//
//			double titleSimilarity = EmbeddingUtils.cosineSimilarity(titleEmbedding, docTextEmbedding);
//			double descriptionSimilarity = EmbeddingUtils.cosineSimilarity(descriptionEmbedding, docTextEmbedding);
//
//			// 🔹 Calcoliamo la similarità media tra titolo e descrizione
//			double meanSimilarity = (titleSimilarity + descriptionSimilarity) / 2;
//
//			// 🔹 Aggiungiamo il documento alla lista dei candidati
//			relevantDocuments.add(file.getName());
//
//			DocumentEntity document = documentRepository.findByFileNameAndDevelopments_Id(file.getName(), development.getId()).orElse(new DocumentEntity());
//
//			document.setFileName(file.getName());
//			document.setExtractedText(extractedText);
//			document.setEmbedding(docTextEmbedding);
//
//			development.getDocuments().add(document);
//			document.getDevelopments().add(development);
//
//			documentRepository.save(document);
//			developmentRepository.save(development);
//
//		} catch (IOException | TikaException e) {
//
//			System.err.println("Errore nella lettura del file: " + file.getName() + " - " + e.getMessage());
//
//		}
//
//	}
//
//	private void scanWithPinecone(GHRepository repository, String path, DevelopmentEntity development, Set<String> relevantDocuments) throws IOException {
//
//		List<GHContent> contents = repository.getDirectoryContent(path);
//
//		for (GHContent file : contents) {
//
//			if (file.isFile()) {
//
//				sendToPinecone(file, development, relevantDocuments); // 🔹 Passiamo l'insieme dei documenti rilevanti
//
//			} else if (file.isDirectory()) {
//
//				scanWithPinecone(repository, file.getPath(), development, relevantDocuments); // 🔹 Ricorsione nelle sottocartelle
//
//			}
//
//		}
//
//	}
//
//	private void sendToPinecone(GHContent file, DevelopmentEntity development, Set<String> relevantDocuments) {
//
//		try (InputStream inputStream = file.read()) {
//
//			String extractedText = tika.parseToString(inputStream);
//			List<Float> embedding = aiService.generateEmbedding(extractedText);
//
//			sendRequest(file.getName(), embedding);
//
//		} catch (Exception e) {
//
//			System.err.println("Errore nella lettura del file: " + file.getName() + " - " + e.getMessage());
//
//		}
//
//	}
//
//	public void sendRequest(String documentId, List<Float> embedding) throws Exception {
//
//		PineconeConfig config = new PineconeConfig("pcsk_7Gdin3_DpHTr16jgYBvfuqnbitzMRKSZkw5PLEExbAoVt1cGK45PcwSQwpZ7QJB2HWyRoL");
//		config.setHost("https://genai-poc-gylu3f0.svc.aped-4627-b74a.pinecone.io");
//		PineconeConnection connection = new PineconeConnection(config);
//		Index index = new Index(connection, "genai-poc");
//
//		VectorWithUnsignedIndices vector = new VectorWithUnsignedIndices(documentId, embedding);
//		UpsertResponse response = index.upsert(List.of(vector), "default");
//
//		index.close();
//
//	}
//
//	public static long generateId(String input) {
//
//		byte[] bytes = input.getBytes();
//		Checksum crc32 = new CRC32();
//		crc32.update(bytes, 0, bytes.length);
//		return crc32.getValue(); // Restituisce un valore numerico a 32 bit (0 - 4294967295)
//
//	}

}
