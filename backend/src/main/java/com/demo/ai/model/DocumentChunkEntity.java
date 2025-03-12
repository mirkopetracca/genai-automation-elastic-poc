package com.demo.ai.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "document_chunks")
public class DocumentChunkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "document_id", nullable = false)
	private DocumentEntity document;

	@Column(name = "title", length = 512)
	private String title;

	@Column(name = "chunk_text", columnDefinition = "TEXT")
	private String chunkText;

	@Column(name = "embedding", columnDefinition = "REAL[]")
	private float[] embedding;

	@Column(name = "language", columnDefinition = "VARCHAR(10)")
	private String language;

	@Column(name = "tsvector_content", columnDefinition = "TSVECTOR", insertable = false, updatable = false)
	private String tsvectorContent;

	@ManyToMany(mappedBy = "documentChunks", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	private Set<DevelopmentEntity> developments = new HashSet<>();

	// 🔹 Getters e Setters
	public Long getId() {

		return id;

	}

	public void setId(Long id) {

		this.id = id;

	}

	public DocumentEntity getDocument() {

		return document;

	}

	public void setDocument(DocumentEntity document) {

		this.document = document;

	}

	public String getTitle() {

		return title;

	}

	public void setTitle(String title) {

		this.title = title;

	}

	public String getChunkText() {

		return chunkText;

	}

	public void setChunkText(String chunkText) {

		this.chunkText = chunkText;

	}

	public float[] getEmbedding() {

		return embedding;

	}

	public void setEmbedding(float[] embedding) {

		this.embedding = embedding;

	}

	public Set<DevelopmentEntity> getDevelopments() {

		return developments;

	}

	public void setDevelopments(Set<DevelopmentEntity> developments) {

		this.developments = developments;

	}

	public String getLanguage() {

		return language;

	}

	public void setLanguage(String language) {

		this.language = language;

	}

	public String getTsvectorContent() {

		return tsvectorContent;

	}

	public void setTsvectorContent(String tsvectorContent) {

		this.tsvectorContent = tsvectorContent;

	}

}