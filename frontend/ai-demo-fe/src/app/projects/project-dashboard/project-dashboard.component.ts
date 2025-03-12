import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectsService, Project } from '../../services/projects.service';
import { ProjectFormComponent } from '../project-form/project-form.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-project-dashboard',
  standalone: true,
  imports: [CommonModule, ProjectFormComponent, RouterModule], // 🔹 AGGIUNGI QUI
  templateUrl: './project-dashboard.component.html',
  styleUrls: ['./project-dashboard.component.scss']
})
export class ProjectDashboardComponent implements OnInit {
  projects: Project[] = [];
  showForm = false;

  constructor(private projectsService: ProjectsService) {}

  ngOnInit(): void {
    this.projectsService.getProjects().subscribe((data) => {
      this.projects = data;
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }
}
