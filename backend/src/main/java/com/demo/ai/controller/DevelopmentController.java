package com.demo.ai.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ai.model.DevelopmentEntity;
import com.demo.ai.model.ProjectEntity;
import com.demo.ai.model.bean.DocumentChunkDTO;
import com.demo.ai.model.bean.DocumentWithChunksDTO;
import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.DocumentChunkRepository;
import com.demo.ai.repository.ProjectRepository;
import com.demo.ai.service.DevelopmentService;
import com.demo.ai.service.SmartChunkingService;

@RestController
@RequestMapping("/api/development")
public class DevelopmentController {

	private final DevelopmentRepository developmentRepository;
	private final ProjectRepository projectRepository;
	private final SmartChunkingService smartChunkingService;
	private final DocumentChunkRepository documentChunkRepository;

	public DevelopmentController(DevelopmentRepository developmentRepository, ProjectRepository projectRepository,
			SmartChunkingService smartChunkingService, DocumentChunkRepository documentChunkRepository) {

		this.developmentRepository = developmentRepository;
		this.projectRepository = projectRepository;
		this.smartChunkingService = smartChunkingService;
		this.documentChunkRepository = documentChunkRepository;

	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<DevelopmentEntity>> getDevelopmentsByProject(@PathVariable Long projectId) {

		List<DevelopmentEntity> developments = developmentRepository.findByProjectId(projectId);
		return ResponseEntity.ok(developments);

	}

	@GetMapping("/{id}")
	public ResponseEntity<DevelopmentEntity> getDevelopmentById(@PathVariable Long id) {

		Optional<DevelopmentEntity> development = developmentRepository.findById(id);
		return development.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

	}

	@PostMapping
	@Transactional
	public ResponseEntity<DevelopmentEntity> createDevelopment(@RequestBody DevelopmentEntity development) {

		Optional<ProjectEntity> projectOpt = projectRepository.findById(development.getProject().getId());

		if (projectOpt.isEmpty()) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

		}

		development.setProject(projectOpt.get());
		DevelopmentEntity savedDevelopment = developmentRepository.save(development);
		return ResponseEntity.ok(savedDevelopment);

	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<DevelopmentEntity> updateDevelopment(@PathVariable Long id, @RequestBody DevelopmentEntity updatedDevelopment) {

		Optional<DevelopmentEntity> existingDevelopment = developmentRepository.findById(id);

		if (existingDevelopment.isPresent()) {

			DevelopmentEntity development = existingDevelopment.get();
			development.setTitle(updatedDevelopment.getTitle());
			development.setDescription(updatedDevelopment.getDescription());

			developmentRepository.save(development);
			return ResponseEntity.ok(development);

		} else {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

		}

	}

//	@PostMapping("/{developmentId}/scan-github-documents")
//	public ResponseEntity<String> scanGithubDocuments(@PathVariable Long developmentId) {
//
//		return developmentService.scanAndAssociateDocuments(developmentId, 3);
//
//	}

	@PostMapping("/{developmentId}/findRelevantDocuments")
	public ResponseEntity<String> findRelevantDocuments(@PathVariable Long developmentId) {

		String message = smartChunkingService.searchRelevantChunksForDevelopmentId(developmentId);
		return ResponseEntity.ok(message);

	}

	@GetMapping("/{developmentId}/getRelevantChunks")
	public ResponseEntity<List<DocumentWithChunksDTO>> getChunksByDevelopment(@PathVariable Long developmentId) {

		List<DocumentWithChunksDTO> chunks = smartChunkingService.getChunksGroupedByDocument(developmentId);

		if (chunks.isEmpty()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

		}

		return ResponseEntity.ok(chunks);

	}

}
