package com.demo.ai.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "documents")
public class DocumentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileName;

	@Lob
	private String extractedText;

	@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DocumentChunkEntity> chunks;

	public Long getId() {

		return id;

	}

	public void setId(Long id) {

		this.id = id;

	}

	public String getFileName() {

		return fileName;

	}

	public void setFileName(String fileName) {

		this.fileName = fileName;

	}

	public String getExtractedText() {

		return extractedText;

	}

	public void setExtractedText(String extractedText) {

		this.extractedText = extractedText;

	}

	public List<DocumentChunkEntity> getChunks() {

		return chunks;

	}

	public void setChunks(List<DocumentChunkEntity> chunks) {

		this.chunks = chunks;

	}

}
