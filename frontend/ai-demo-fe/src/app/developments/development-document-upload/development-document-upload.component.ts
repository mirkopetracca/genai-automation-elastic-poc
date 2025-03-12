import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DevelopmentDocumentsService } from '../../services/development-documents.service';

@Component({
  selector: 'app-development-document-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './development-document-upload.component.html',
  styleUrls: ['./development-document-upload.component.scss']
})
export class DevelopmentDocumentUploadComponent {
  @Input() developmentId!: number;
  selectedFile: File | null = null;
  uploadMessage: string = '';

  constructor(private documentsService: DevelopmentDocumentsService) {}

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  upload(): void {
    if (this.selectedFile && this.developmentId) {
      this.documentsService.uploadDocument(this.developmentId, this.selectedFile).subscribe({
        next: (response) => {
          this.uploadMessage = 'Documento caricato con successo!';
          this.selectedFile = null;
        },
        error: () => {
          this.uploadMessage = 'Errore durante il caricamento.';
        }
      });
    }
  }
}
