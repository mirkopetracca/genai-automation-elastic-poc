import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProjectsService, Project } from '../../services/projects.service';
import { DevelopmentsService } from '../../services/developments.service';
import { DevelopmentListComponent } from '../../developments/development-list/development-list.component';
import { DevelopmentFormComponent } from '../../developments/development-form/development-form.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, DevelopmentListComponent, DevelopmentFormComponent], // 🔹 Componenti Importati
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  projectId!: number;
  project: Project | null = null;
  developments: any[] = [];
  showForm = false; // Controlla la visibilità del form

  constructor(
    private route: ActivatedRoute,
    private projectsService: ProjectsService,
    private developmentsService: DevelopmentsService
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));

    this.projectsService.getProjectById(this.projectId).subscribe((data) => {
      this.project = data;
    });

    this.refreshDevelopments();
  }

  refreshDevelopments(): void {
    this.developmentsService.getDevelopmentsByProject(this.projectId).subscribe((devs) => {
      this.developments = devs;
      this.showForm = false; // 🔹 Chiude il form dopo il refresh
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }
}
