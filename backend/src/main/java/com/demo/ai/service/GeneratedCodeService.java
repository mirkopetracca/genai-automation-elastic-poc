package com.demo.ai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.ai.model.DevelopmentEntity;
import com.demo.ai.model.GeneratedCodeEntity;
import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.GeneratedCodeRepository;

@Service
public class GeneratedCodeService {

	private final GeneratedCodeRepository generatedCodeRepository;
	private final DevelopmentRepository developmentRepository;

	public GeneratedCodeService(GeneratedCodeRepository generatedCodeRepository, DevelopmentRepository developmentRepository) {

		this.generatedCodeRepository = generatedCodeRepository;
		this.developmentRepository = developmentRepository;

	}

	@Transactional
	public void saveGeneratedCode(Long developmentId, String fileName, String fileContent) {

		DevelopmentEntity development = developmentRepository.findById(developmentId)
				.orElseThrow(() -> new IllegalArgumentException("Development ID non trovato"));

		GeneratedCodeEntity generatedCode = new GeneratedCodeEntity();
		generatedCode.setDevelopment(development);
		generatedCode.setFileName(fileName);
		generatedCode.setFileContent(fileContent);

		generatedCodeRepository.save(generatedCode);

	}

	public List<GeneratedCodeEntity> getGeneratedCodeByDevelopmentId(Long developmentId) {

		return generatedCodeRepository.findByDevelopmentId(developmentId);

	}

	public ResponseEntity<GeneratedCodeEntity> updateGeneratedCode(Long id, GeneratedCodeEntity updatedCode) {

		Optional<GeneratedCodeEntity> existingCodeOpt = generatedCodeRepository.findById(id);

		if (existingCodeOpt.isEmpty()) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

		}

		GeneratedCodeEntity existingCode = existingCodeOpt.get();
		existingCode.setFileName(updatedCode.getFileName());
		existingCode.setFileContent(updatedCode.getFileContent());

		GeneratedCodeEntity savedCode = generatedCodeRepository.save(existingCode);
		return ResponseEntity.ok(savedCode);

	}

	@Transactional
	public void deleteGeneratedCodeByDevelopmentId(Long developmentId) {

		generatedCodeRepository.deleteByDevelopmentId(developmentId);

	}

}
