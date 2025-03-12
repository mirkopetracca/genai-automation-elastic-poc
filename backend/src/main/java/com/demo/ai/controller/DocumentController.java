package com.demo.ai.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ai.model.DocumentEntity;
import com.demo.ai.repository.DocumentRepository;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

	private final DocumentRepository documentRepository;

	public DocumentController(DocumentRepository documentRepository) {

		this.documentRepository = documentRepository;

	}

	@GetMapping("/development/{developmentId}")
	public ResponseEntity<List<DocumentEntity>> getDocumentsByDevelopment(@PathVariable Long developmentId) {

		//List<DocumentEntity> documents = documentRepository.findByDevelopmentId(developmentId);
		
		List<DocumentEntity> documents = new ArrayList<DocumentEntity>();

		if (documents.isEmpty()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

		}

		return ResponseEntity.ok(documents);

	}
	
    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(documentId);

        if (documentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        DocumentEntity document = documentOpt.get();
        ByteArrayResource resource = new ByteArrayResource(document.getExtractedText().getBytes());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }
    
    
}