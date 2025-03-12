package com.demo.ai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.ai.service.AIService;

@RestController
@RequestMapping("/api/ai")
public class AIController {

	private final AIService aiService;

	public AIController(AIService aiService) {

		this.aiService = aiService;

	}

//	@PostMapping("/upload")
//	public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file, @RequestParam("developmentId") Long developmentId) {
//
//		return aiService.uploadDocument(file, developmentId);
//
//	}

//	@PostMapping("/prompt")
//	public ResponseEntity<String> promptDocuments(@RequestBody Map<String, Object> request) {
//
//		return aiService.promptDocuments(request);
//
//	}
}