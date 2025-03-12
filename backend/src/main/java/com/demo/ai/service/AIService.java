package com.demo.ai.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.DocumentRepository;
import com.demo.ai.util.EmbeddingUtils;

@Service
public class AIService {

	Log log = LogFactory.getLog(this.getClass());

	private final Tika tika = new Tika();
	private final DocumentRepository documentRepository;
	private final DevelopmentRepository developmentRepository;

	@Value("${openai.api.key}")
	private String openaiApiKey;

	@Value("${openai.model}")
	private String openaiModel;

	@Value("${openai.embedding.model}")
	private String openaiEmbeddingModel;

	@Value("${anthropic.api.key}")
	private String anthropicKey;

	@Value("${anthropic.model}")
	private String anthropicModel;

	private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
	private static final String EMBEDDING_API_URL = "https://api.openai.com/v1/embeddings";

	public AIService(DocumentRepository documentRepository, DevelopmentRepository developmentRepository) {

		this.documentRepository = documentRepository;
		this.developmentRepository = developmentRepository;

	}

//	public ResponseEntity<String> uploadDocument(MultipartFile file, Long developmentId) {
//
//		try {
//
//			var development = developmentRepository.findById(developmentId);
//
//			if (development.isEmpty()) {
//
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Development ID non trovato");
//
//			}
//
//			String extractedText = tika.parseToString(file.getInputStream());
//			List<Float> embedding = generateEmbedding(extractedText);
//
//			DocumentEntity document = new DocumentEntity();
//			document.setFileName(file.getOriginalFilename());
//			document.setExtractedText(extractedText);
//			//document.setEmbedding(embedding);
//
//			DevelopmentEntity developmentEnt = development.get();
//			document.getDevelopments().add(developmentEnt);
//
//			documentRepository.save(document);
//
//			developmentEnt.getDocuments().add(document);
//			developmentRepository.save(developmentEnt);
//
//			return ResponseEntity.ok("Documento caricato con successo");
//
//		} catch (IOException | TikaException e) {
//
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante il caricamento del documento.");
//
//		}
//
//	}

//	public ResponseEntity<String> promptDocuments(Map<String, Object> request) {
//
//		Long developmentId = ((Number) request.get("developmentId")).longValue();
//		String userQuery = (String) request.get("query");
//		List<Float> queryEmbedding = generateEmbedding(userQuery);
//		List<DocumentEntity> relevantDocs = findRelevantDocuments(developmentId, queryEmbedding);
//
//		StringBuilder context = new StringBuilder("Questi documenti contengono informazioni utili:\n");
//
//		for (DocumentEntity doc : relevantDocs) {
//
//			context.append("- ").append(doc.getExtractedText()).append("\n");
//
//		}
//
//		String aiResponse = callOpenAI(context.toString() + "\nDomanda: " + userQuery);
//		return ResponseEntity.ok(aiResponse);
//
//	}

	@SuppressWarnings("unchecked")
	public List<Float> generateEmbedding(String text) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + openaiApiKey);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", openaiEmbeddingModel);
		requestBody.put("input", text);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<Map> response = restTemplate.exchange(EMBEDDING_API_URL, HttpMethod.POST, request, Map.class);

		List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

		if (data != null && !data.isEmpty()) {

			List<Double> embeddingList = (List<Double>) data.get(0).get("embedding");

			ByteBuffer byteBuffer = ByteBuffer.allocate(embeddingList.size() * Float.BYTES);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

			for (Double value : embeddingList) {

				byteBuffer.putFloat(value.floatValue());

			}

			return EmbeddingUtils.convertByteArrayToFloatList(byteBuffer.array());

		}

		return EmbeddingUtils.convertByteArrayToFloatList(new byte[0]);

	}

	@SuppressWarnings("unchecked")
	public String callOpenAI(String text, double temperature) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + openaiApiKey);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", openaiModel);
		requestBody.put("messages", List.of(Map.of("role", "system", "content", "Sei un assistente AI specializzato nell'analisi di documenti."),
				Map.of("role", "user", "content", text)));
		requestBody.put("temperature", temperature);

		log.info(("\n\nPROMPT:\n" + text));

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<Map> responseEntity = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, request, Map.class);

		List<Map<String, Object>> choices = (List<Map<String, Object>>) responseEntity.getBody().get("choices");

		if (choices != null && !choices.isEmpty()) {

			Map<String, Object> firstChoice = choices.get(0);
			Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
			String strMessage = message != null ? message.get("content").toString() : "Errore nell'elaborazione AI.";

			log.info(("\n\nRISPOSTA:\n" + strMessage));

			return strMessage;

		}

		return "Errore: Nessuna risposta dall'AI.";

	}

	public String callOpenAI(String text) {

		return callOpenAI(text, 0.7);

	}

	public String callOpenAIForCode(String prompt) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + openaiApiKey);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", openaiModel);
		requestBody.put("messages",
				List.of(Map.of("role", "system", "content", "Genera codice strutturato separando i file con '### file: percorso.NomeFile.ext'."),
						Map.of("role", "user", "content", prompt)));
		requestBody.put("temperature", 0.7);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<Map> responseEntity = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, request, Map.class);

		List<Map<String, Object>> choices = (List<Map<String, Object>>) responseEntity.getBody().get("choices");

		if (choices != null && !choices.isEmpty()) {

			Map<String, Object> firstChoice = choices.get(0);
			Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
			return message != null ? message.get("content").toString() : "Errore nell'elaborazione AI.";

		}

		return "Errore: Nessuna risposta dall'AI.";

	}

	public String callAnthropic(String prompt) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-api-key", anthropicKey);
		headers.set("anthropic-version", "2023-06-01");

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", anthropicModel);
		requestBody.put("max_tokens", 8192);

		// System.out.println("Genera codice strutturato separando i file con '### file:
		// percorso.NomeFile.ext'" + prompt);

		requestBody.put("messages", List
				.of(Map.of("role", "user", "content", "Genera tutto il codice strutturato separando i file con '### file: percorso.NomeFile.ext'\n" + prompt)));

		log.info(("\n\nPROMPT:\n" + "Genera tutto il codice strutturato separando i file con '### file: percorso.NomeFile.ext'\n" + prompt));
		
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<Map> responseEntity = restTemplate.exchange("https://api.anthropic.com/v1/messages", HttpMethod.POST, request, Map.class);

		List<Map<String, Object>> choices = (List<Map<String, Object>>) responseEntity.getBody().get("content");

		if (choices != null && !choices.isEmpty()) {

			// System.out.println(choices.get(0).get("text").toString());
			log.info(("\n\nRISPOSTA:\n" + choices.get(0).get("text").toString()));
			return choices.get(0).get("text").toString();

		}

		return "Errore: Nessuna risposta dall'AI.";

	}

//	private List<DocumentEntity> findRelevantDocuments(Long developmentId, List<Float> queryEmbedding) {
//
//		List<DocumentEntity> documents = documentRepository.findByDevelopmentId(developmentId);
//		//documents.sort(Comparator.comparingDouble(doc -> EmbeddingUtils.cosineSimilarity(queryEmbedding, doc.getEmbedding())));
//		return documents.stream().limit(5).collect(Collectors.toList());
//
//	}

}
