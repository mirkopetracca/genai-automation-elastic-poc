package com.demo.ai.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "developments")
@JsonIgnoreProperties({ "documentChunks" })
public class DevelopmentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectEntity project;

	@ManyToMany
	@JoinTable(
			name = "development_documentchunks",
			joinColumns = @JoinColumn(name = "development_id"),
			inverseJoinColumns = @JoinColumn(name = "documentchunk_id")
	)
	private Set<DocumentChunkEntity> documentChunks = new HashSet<>();

	// 🔹 Getters e Setters
	public Long getId() {

		return id;

	}

	public void setId(Long id) {

		this.id = id;

	}

	public String getTitle() {

		return title;

	}

	public void setTitle(String title) {

		this.title = title;

	}

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	public ProjectEntity getProject() {

		return project;

	}

	public void setProject(ProjectEntity project) {

		this.project = project;

	}

	public Set<DocumentChunkEntity> getDocumentChunks() {

		return documentChunks;

	}

	public void setDocumentChunks(Set<DocumentChunkEntity> documentChunks) {

		this.documentChunks = documentChunks;

	}
}
