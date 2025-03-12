import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DevelopmentsService, Development } from '../../services/developments.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-development-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './development-list.component.html',
  styleUrls: ['./development-list.component.scss']
})
export class DevelopmentListComponent implements OnInit {
  @Input() projectId!: number;
  developments: Development[] = [];

  constructor(private developmentsService: DevelopmentsService) {}

  ngOnInit(): void {
    this.loadDevelopments();
  }

  loadDevelopments(): void {
    if (this.projectId) {
      this.developmentsService.getDevelopmentsByProject(this.projectId).subscribe((data) => {
        this.developments = data;
      });
    }
  }
}
