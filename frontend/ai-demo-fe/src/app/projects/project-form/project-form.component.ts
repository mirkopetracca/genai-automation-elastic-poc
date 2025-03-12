import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectsService, Project } from '../../services/projects.service';

@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './project-form.component.html',
  styleUrls: ['./project-form.component.scss']
})
export class ProjectFormComponent {
  project: Project = {
    id: 0,
    name: '',
    repositoryUrl: '',
    sourcePath: '',
    technologies: '',
    documentsPath: ''
  };

  constructor(private projectsService: ProjectsService) {}

  createProject(): void {
    this.projectsService.createProject(this.project).subscribe(() => {
      alert('Progetto creato con successo!');
      window.location.reload();
    });
  }
}
