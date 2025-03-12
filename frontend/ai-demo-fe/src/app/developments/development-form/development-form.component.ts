import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DevelopmentsService } from '../../services/developments.service';

@Component({
  selector: 'app-development-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './development-form.component.html',
  styleUrls: ['./development-form.component.scss']
})
export class DevelopmentFormComponent {
  @Input() projectId!: number; // 🔹 Riceve l'ID del progetto
  @Output() developmentCreated = new EventEmitter<void>();

  title = '';
  description = '';

  constructor(private developmentsService: DevelopmentsService) {}

  createDevelopment() {
    if (!this.projectId) {
      alert('Errore: Nessun progetto selezionato!');
      return;
    }

    this.developmentsService.createDevelopment(this.projectId, this.title, this.description).subscribe(() => {
      this.developmentCreated.emit();
      this.title = '';
      this.description = '';
    });
  }
}
