import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DevelopmentDocumentsService {

  constructor(private http: HttpClient) {}

  uploadDocument(developmentId: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('developmentId', developmentId.toString());

    return this.http.post<string>('http://localhost:8080/api/ai/upload', formData);
  }

  getDocumentsByDevelopmentId(developmentId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/documents/development/${developmentId}`);
  }

  getChunksByDevelopmentId(developmentId: number): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/development/${developmentId}/getRelevantChunks`);
  }

  scanGithubDocuments(developmentId: number): Observable<string> {
    return this.http.post<string>(`http://localhost:8080/api/development/${developmentId}/findRelevantDocuments`, {}, { responseType: 'text' as 'json' });
  }

  downloadDocument(documentId: number): Observable<Blob> {
    return this.http.get(`http://localhost:8080/api/documents/${documentId}/download`, { responseType: 'blob' });
  }
  
}
