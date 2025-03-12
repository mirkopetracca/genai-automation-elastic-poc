package com.demo.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ai.model.GeneratedCodeEntity;
import com.demo.ai.service.GeneratedCodeService;
import com.demo.ai.service.GitHubIntegrationService;

@RestController
@RequestMapping("/api/code")
public class CodeController {

	private final GeneratedCodeService generatedCodeService;
	private final GitHubIntegrationService gitHubIntegrationService;

	public CodeController(GeneratedCodeService generatedCodeService, GitHubIntegrationService gitHubIntegrationService) {

		this.generatedCodeService = generatedCodeService;
		this.gitHubIntegrationService = gitHubIntegrationService;

	}

	@PostMapping("/generate/{developmentId}")
	public ResponseEntity<Map<String, Object>> generateCode(@PathVariable Long developmentId, @RequestBody(required = false) String customPrompt) {

		return gitHubIntegrationService.generateCode(developmentId, customPrompt);

	}

	@GetMapping("/{developmentId}")
	public ResponseEntity<List<GeneratedCodeEntity>> getGeneratedCode(@PathVariable Long developmentId) {

		return ResponseEntity.ok(generatedCodeService.getGeneratedCodeByDevelopmentId(developmentId));

	}

	@PutMapping("/update/{id}")
	public ResponseEntity<GeneratedCodeEntity> updateGeneratedCode(@PathVariable Long id, @RequestBody GeneratedCodeEntity updatedCode) {

		return generatedCodeService.updateGeneratedCode(id, updatedCode);

	}

	@PostMapping("/commit/{developmentId}")
	public ResponseEntity<String> commitGeneratedCode(@PathVariable Long developmentId) {

		return gitHubIntegrationService.commitGeneratedCode(developmentId);

	}

	@PostMapping("/analyze")
	public ResponseEntity<Map<String, Object>> analyzeRepository(@RequestParam Long projectId) {

		return gitHubIntegrationService.analyzeRepository(projectId);

	}

}
