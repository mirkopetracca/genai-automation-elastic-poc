package com.demo.ai.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "generated_code")
public class GeneratedCodeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "development_id", nullable = false)
	private DevelopmentEntity development;

	private String fileName;

	@Lob
	private String fileContent;

	private LocalDateTime createdAt;

	public GeneratedCodeEntity() {

		this.createdAt = LocalDateTime.now();

	}

	// Getters and Setters
	public Long getId() {

		return id;

	}

	public void setId(Long id) {

		this.id = id;

	}

	public DevelopmentEntity getDevelopment() {

		return development;

	}

	public void setDevelopment(DevelopmentEntity development) {

		this.development = development;

	}

	public String getFileName() {

		return fileName;

	}

	public void setFileName(String fileName) {

		this.fileName = fileName;

	}

	public String getFileContent() {

		return fileContent;

	}

	public void setFileContent(String fileContent) {

		this.fileContent = fileContent;

	}

	public LocalDateTime getCreatedAt() {

		return createdAt;

	}

	public void setCreatedAt(LocalDateTime createdAt) {

		this.createdAt = createdAt;

	}
}
