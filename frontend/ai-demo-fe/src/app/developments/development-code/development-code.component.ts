import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ✅ Import di FormsModule
import { GeneratedCodeService } from '../../services/generated-code.service';

@Component({
  selector: 'app-development-code',
  standalone: true,
  imports: [CommonModule, FormsModule], // ✅ Aggiunto FormsModule
  templateUrl: './development-code.component.html',
  styleUrls: ['./development-code.component.scss']
})
export class DevelopmentCodeComponent implements OnInit {
  @Input() developmentId!: number;
  generatedFiles: any[] = [];
  editedFiles: { [key: number]: { fileName: string; fileContent: string } } = {}; // Traccia modifiche
  isPromptOpen: boolean = false;
  customPrompt: string = ''; // Testo per il prompt personalizzato
  isGenerating: boolean = false;
  isCommitting: boolean = false;

  constructor(private generatedCodeService: GeneratedCodeService) { }

  ngOnInit(): void {
    if (this.developmentId) {
      this.loadGeneratedCode();
    }
  }

  loadGeneratedCode(): void {
    this.generatedCodeService.getGeneratedCode(this.developmentId).subscribe((data) => {
      this.generatedFiles = data.map(file => ({
        id: file.id,
        fileName: file.fileName,
        fileContent: file.fileContent
      }));
    });
  }

  updateFileContent(fileId: number, fileName: string, newContent: string): void {
    this.editedFiles[fileId] = { fileName, fileContent: newContent };
  }

  saveFile(fileId: number): void {
    if (!this.editedFiles[fileId]) return;

    this.generatedCodeService.updateGeneratedCode(
      fileId,
      this.editedFiles[fileId].fileName,
      this.editedFiles[fileId].fileContent
    ).subscribe(() => {
      console.log(`File ${fileId} aggiornato con successo`);
      delete this.editedFiles[fileId]; // Rimuove la traccia delle modifiche dopo il salvataggio
    });
  }


  togglePromptBox(): void {
    this.isPromptOpen = !this.isPromptOpen;
  }

  generateCode(): void {
    this.isGenerating = true;
    this.generatedCodeService.generateCode(this.developmentId, this.customPrompt).subscribe(response => {
      console.log("Codice generato:", response);
      this.loadGeneratedCode(); // Ricarica il codice generato dopo la chiamata API
      this.isPromptOpen = false; // Chiude il box del prompt
      this.customPrompt = ''; // Resetta il campo di input
      this.isGenerating = false;
    });
  }


  commitCode(): void {
    this.isCommitting = true;
    this.generatedCodeService.commitGeneratedCode(this.developmentId).subscribe(response => {
      this.isCommitting = false;
      console.log("Commit completato:", response);
    },
    (error) => {
      console.warn("Errore durante il commit.");
      this.isCommitting = false;
    });
  }
}
