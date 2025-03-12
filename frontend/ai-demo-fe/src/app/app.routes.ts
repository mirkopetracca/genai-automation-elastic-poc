import { Routes } from '@angular/router';
import { ProjectDashboardComponent } from './projects/project-dashboard/project-dashboard.component';
import { ProjectDetailComponent } from './projects/project-detail/project-detail.component';
import { DevelopmentDetailComponent } from './developments/development-detail/development-detail.component';

export const routes: Routes = [
  { path: '', component: ProjectDashboardComponent, data: { animation: 'DashboardPage' } },
  { path: 'projects/:id', component: ProjectDetailComponent, data: { animation: 'ProjectPage' } },
  { path: 'developments/:id', component: DevelopmentDetailComponent, runGuardsAndResolvers: 'always', data: { animation: 'DevelopmentPage' } }
];
