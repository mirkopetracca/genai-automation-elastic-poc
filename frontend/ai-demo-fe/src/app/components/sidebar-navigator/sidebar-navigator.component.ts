import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar-navigator',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-navigator.component.html',
  styleUrls: ['./sidebar-navigator.component.scss']
})
export class SidebarNavigatorComponent {
  @Input() projects: any[] = [];
  @Output() projectSelected = new EventEmitter<any>();
  @Output() developmentSelected = new EventEmitter<any>();

  selectProject(project: any) {
    project.expanded = !project.expanded;
    this.projectSelected.emit(project);
  }

  selectDevelopment(development: any, event: Event) {
    event.stopPropagation();
    this.developmentSelected.emit(development);
  }
}
