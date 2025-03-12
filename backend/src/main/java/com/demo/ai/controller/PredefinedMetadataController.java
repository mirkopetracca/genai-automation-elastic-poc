package com.demo.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ai.model.DevelopmentMetadataEntity;
import com.demo.ai.service.PredefinedMetadataService;

@RestController
@RequestMapping("/api/metadata")
public class PredefinedMetadataController {

	private final PredefinedMetadataService predefinedMetadataService;

	public PredefinedMetadataController(PredefinedMetadataService predefinedMetadataService) {

		this.predefinedMetadataService = predefinedMetadataService;

	}

	@PostMapping("/generate/{developmentId}")
	public ResponseEntity<String> generateMetadata(@PathVariable Long developmentId) {

		return predefinedMetadataService.generateMetadata(developmentId);

	}

	@GetMapping("/{developmentId}")
	public ResponseEntity<DevelopmentMetadataEntity> getMetadata(@PathVariable Long developmentId) {

		return predefinedMetadataService.getMetadata(developmentId);

	}
}