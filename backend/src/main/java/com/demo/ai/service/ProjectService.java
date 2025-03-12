package com.demo.ai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.demo.ai.model.ProjectEntity;
import com.demo.ai.repository.ProjectRepository;

@Service
public class ProjectService {

	private final ProjectRepository projectRepository;

	public ProjectService(ProjectRepository projectRepository) {

		this.projectRepository = projectRepository;

	}

	public List<ProjectEntity> getAllProjects() {

		return projectRepository.findAll();

	}

	public ResponseEntity<ProjectEntity> getProjectById(Long id) {

		Optional<ProjectEntity> project = projectRepository.findById(id);
		return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

	}

	public ProjectEntity createProject(ProjectEntity project) {

		return projectRepository.save(project);

	}

	public ResponseEntity<ProjectEntity> updateProject(Long id, ProjectEntity projectDetails) {

		return projectRepository.findById(id).map(project -> {

			project.setName(projectDetails.getName());
			project.setRepositoryUrl(projectDetails.getRepositoryUrl());
			project.setSourcePath(projectDetails.getSourcePath());
			project.setTechnologies(projectDetails.getTechnologies());
			project.setDocumentsPath(projectDetails.getDocumentsPath());
			ProjectEntity updatedProject = projectRepository.save(project);
			return ResponseEntity.ok(updatedProject);

		}).orElseGet(() -> ResponseEntity.notFound().build());

	}

	public ResponseEntity<Object> deleteProject(Long id) {

		return projectRepository.findById(id).map(project -> {

			projectRepository.delete(project);
			return ResponseEntity.ok().build();

		}).orElseGet(() -> ResponseEntity.notFound().build());

	}
}
