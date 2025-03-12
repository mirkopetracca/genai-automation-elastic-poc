import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { DevelopmentsService, Development } from '../../services/developments.service';
import { DevelopmentDocumentsService } from '../../services/development-documents.service';
import { DevelopmentCodeComponent } from '../development-code/development-code.component';
import { RouterModule } from '@angular/router';
import { MetadataService } from '../../services/metadata.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import * as marked from 'marked';

@Component({
  selector: 'app-development-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DevelopmentCodeComponent],
  templateUrl: './development-detail.component.html',
  styleUrls: ['./development-detail.component.scss']
})

export class DevelopmentDetailComponent implements OnInit {
  development: Development | null = null;
  developmentId!: number;
  chunks: any[] = [];
  showUploadForm = false;
  metadata: any = null;
  metadataAvailable = false;
  activeTab: 'metadata' | 'code' = 'metadata';
  isEditing: boolean = false;
  editedTitle!: string;
  editedDescription!: string;
  selectedFile!: File | null;
  isScanning: boolean = false;
  isExtracting: boolean = false;
  repositoryUrl: string | null = null;
  documentsPath: string | null = null;

  metadataSections: Record<string, boolean> = {
    functionalRequirements: false,
    useCases: false,
    inputOutputData: false,
    technicalDependencies: false
  };

  constructor(
    private route: ActivatedRoute,
    private developmentsService: DevelopmentsService,
    private documentsService: DevelopmentDocumentsService,
    private metadataService: MetadataService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {
      this.developmentId = Number(params.get('id'));
      console.log("Caricato development con ID:", this.developmentId);
      this.loadData();  
    });

    this.loadMetadata();

  }

  loadData(): void {

    this.developmentId = Number(this.route.snapshot.paramMap.get('id'));

    this.developmentsService.getDevelopmentById(this.developmentId).subscribe((data) => {
      console.log("Dati sviluppo ricevuti:", data);
      if (data) {
        this.development = data;
        this.editedTitle = data.title;
        this.editedDescription = data.description;
      }
    });

    this.refreshDocuments();
    this.loadMetadata();
    this.loadDevelopmentDetails();

  }



  loadDevelopmentDetails(): void {
    this.developmentsService.getDevelopmentById(this.developmentId).subscribe((data) => {
      if (data.project) {
        this.repositoryUrl = data.project.repositoryUrl;
        this.documentsPath = data.project.documentsPath || "Percorso non specificato";
      }
    });
  }

  toggleUploadForm(): void {
    this.showUploadForm = !this.showUploadForm;
    this.selectedFile = null; // Resetta il file selezionato
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      console.log(`File selezionato: ${file.name}`);
    }
  }

  uploadDocument(): void {
    if (!this.selectedFile) {
      console.warn("Nessun file selezionato per l'upload.");
      return;
    }

    this.documentsService.uploadDocument(this.developmentId, this.selectedFile).subscribe(() => {
      console.log("Documento caricato con successo!");
      this.toggleUploadForm(); // Chiude il form dopo il caricamento
    });
  }

  setActiveTab(tab: 'metadata' | 'code'): void {
    this.activeTab = tab;
  }

  refreshDocuments(): void {
    this.documentsService.getChunksByDevelopmentId(this.developmentId).subscribe((chunks) => {
      this.chunks = chunks;
    });
  }

  toggleSection(section: string): void {
    this.metadataSections[section] = !this.metadataSections[section];
  }

  loadMetadata() {
    this.metadataService.getMetadata(this.developmentId).subscribe(
      (data) => {
        if (data) {
          this.metadata = {
            functionalRequirements: this.formatMetadata(data.functionalRequirements),
            useCases: this.formatMetadata(data.useCases),
            inputOutputData: this.formatMetadata(data.inputOutputData),
            technicalDependencies: this.formatMetadata(data.technicalDependencies)
          };
          this.metadataAvailable = true;
        }
      },
      (error) => {
        console.warn("Nessun metadato trovato per questo sviluppo.");
        this.metadataAvailable = false;
      }
    );
  }

  generateMetadata() {
    this.isExtracting = true;
    this.metadataService.generateMetadata(this.developmentId).subscribe(() => {
      this.isExtracting = false;
      console.log("Metadati generati con successo!");
      this.loadMetadata();
    });
  }

  formatMetadata(text: string): SafeHtml {
    if (!text) return '';
    let formatted: string = marked.parse(text) as string;
    return this.sanitizer.bypassSecurityTrustHtml(formatted);
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
  }

  saveDevelopmentDetails(): void {
    if (!this.development) return;

    this.developmentsService.updateDevelopment(this.developmentId, {
      title: this.editedTitle,
      description: this.editedDescription
    }).subscribe(() => {
      console.log("Dettagli sviluppo aggiornati con successo");
      this.isEditing = false;
      this.development!.title = this.editedTitle;
      this.development!.description = this.editedDescription;
    });
  }

  scanRelevantDocuments(): void {
    this.isScanning = true;
    this.documentsService.scanGithubDocuments(this.developmentId).subscribe(() => {
      console.log("Scansione completata: documenti allegati automaticamente.");
      this.isScanning = false;
      this.refreshDocuments();
    }, error => {
      console.error("Errore durante la scansione:", error);
      this.isScanning = false;
    });
  }

  downloadDocument(documentId: number, fileName: string): void {
    this.documentsService.downloadDocument(documentId).subscribe((blob) => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = fileName;
      a.click();
      URL.revokeObjectURL(objectUrl);
    });
  }

}
