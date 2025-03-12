import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { SidebarNavigatorComponent } from './components/sidebar-navigator/sidebar-navigator.component';
import { ProjectsService } from './services/projects.service';
import { DevelopmentsService } from './services/developments.service';
import { Development } from './services/developments.service';
import { trigger, transition, style, animate } from '@angular/animations';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, SidebarNavigatorComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [
    trigger('routeAnimations', [
      transition('* <=> *', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class AppComponent {
  projects = [];
  constructor(
    private projectsService: ProjectsService, 
    private developmentsService: DevelopmentsService,
    private router: Router
  ) {
    this.loadProjects();
  }

  prepareRoute(outlet: RouterOutlet) {
    return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
  }
  
  loadProjects() {
    this.projectsService.getProjects().subscribe((data: any) => {
      this.projects = data;
      data.forEach((project: any) => {
        this.developmentsService.getDevelopmentsByProject(project.id).subscribe((developments: Development[]) => {
          project.developments = developments;
        });
      });
    });
  }

  onProjectSelected(project: any) {
    console.log("Progetto selezionato:", project);
  }

  onDevelopmentSelected(development: any) {
    console.log("Sviluppo selezionato:", development);
    this.router.navigate(['/developments', development.id]);
  }
}
