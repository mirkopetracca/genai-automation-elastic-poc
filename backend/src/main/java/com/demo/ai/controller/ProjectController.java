package com.demo.ai.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ai.model.ProjectEntity;
import com.demo.ai.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {

		this.projectService = projectService;

	}

	@GetMapping
	public List<ProjectEntity> getAllProjects() {

		return projectService.getAllProjects();

	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectEntity> getProjectById(@PathVariable Long id) {

		return projectService.getProjectById(id);

	}

	@PostMapping
	public ProjectEntity createProject(@RequestBody ProjectEntity project) {

		return projectService.createProject(project);

	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjectEntity> updateProject(@PathVariable Long id, @RequestBody ProjectEntity projectDetails) {

		return projectService.updateProject(id, projectDetails);

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteProject(@PathVariable Long id) {

		return projectService.deleteProject(id);

	}
}