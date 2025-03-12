package com.demo.ai.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
@JsonIgnoreProperties("developments")
public class ProjectEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String repositoryUrl;
	private String sourcePath;
	private String technologies;
	private String documentsPath;

	@OneToMany(mappedBy = "project")
	private List<DevelopmentEntity> developments;

	// Getters and Setters
	public Long getId() {

		return id;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	public String getRepositoryUrl() {

		return repositoryUrl;

	}

	public void setRepositoryUrl(String repositoryUrl) {

		this.repositoryUrl = repositoryUrl;

	}

	public String getSourcePath() {

		return sourcePath;

	}

	public void setSourcePath(String sourcePath) {

		this.sourcePath = sourcePath;

	}

	public String getTechnologies() {

		return technologies;

	}

	public void setTechnologies(String technologies) {

		this.technologies = technologies;

	}

	public List<DevelopmentEntity> getDevelopments() {

		return developments;

	}

	public void setDevelopments(List<DevelopmentEntity> developments) {

		this.developments = developments;

	}

	public String getDocumentsPath() {

		return documentsPath;

	}

	public void setDocumentsPath(String documentsPath) {

		this.documentsPath = documentsPath;

	}

	public void setId(Long id) {

		this.id = id;

	}

}