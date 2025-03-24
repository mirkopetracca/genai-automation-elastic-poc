package com.demo.ai.service;

import com.demo.ai.model.DocumentEntity;
import com.demo.ai.model.bean.DocumentChunkDTO;
import com.demo.ai.repository.DocumentRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LlamaIndexService {

    private static final Log log = LogFactory.getLog(LlamaIndexService.class);

    private final DocumentRepository documentRepository;
    @Value("${llamaIndex.documents.url}")
    private String llamaIndexDocumentsUrl;

    @Value("${llamaIndex.search.url}")
    private String llamaIndexSearchUrl;


    public LlamaIndexService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void loadDocuments(String githubRepo, String folder) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("githubRepo", githubRepo);
        requestBody.put("folder", folder);

        log.info(("\n\nPROMPT:\n" + requestBody));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(llamaIndexDocumentsUrl, HttpMethod.POST, request, Map.class);

        log.info(("\n\nresponse:\n" + responseEntity));
    }

    public List<DocumentChunkDTO> filterDocumentsWithReAG(String query) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        log.info(("\n\nPROMPT:\n" + query));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(llamaIndexSearchUrl, HttpMethod.POST, request, Map.class);

        List<Map<String, String>> results = (List<Map<String, String>>) responseEntity.getBody().get("results");
        List<DocumentChunkDTO> documents = new ArrayList<>();
        if (results != null && !results.isEmpty()) {
            for(Map<String, String> item : results) {
                String filename = item.get("filename");
                Long id = this.createOrGetDocumentEntity(filename);
                String text = item.get("text");
                DocumentChunkDTO doc = new DocumentChunkDTO(id, filename, getChunkTitle(text), text);
                documents.add(doc);
            }
        }
        return documents;
    }

    private Long createOrGetDocumentEntity(String filename) {
        Optional<DocumentEntity> documentEntity = documentRepository.findByFileName(filename);
        if(documentEntity.isPresent()) {
            log.info(("documento presente, restuisco ID: " + documentEntity.get().getId()));
            return documentEntity.get().getId();
        } else {
            DocumentEntity doc = new DocumentEntity();
            doc.setFileName(filename);
            log.info(("creo nuovo documento"));
            return documentRepository.save(doc).getId();
        }
    }

    private String getChunkTitle(String chunkText) {
        return chunkText.indexOf("\n") != -1 ? chunkText.split("\n")[0] : chunkText.substring(0, 50);

    }
}
