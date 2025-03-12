import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeneratedCodeService {
  private apiUrl = 'http://localhost:8080/api/code';
  constructor(private http: HttpClient) {}
  
  getGeneratedCode(developmentId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${developmentId}`);
  }

  updateGeneratedCode(fileId: number, fileName: string, fileContent: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${fileId}`, { fileName, fileContent });
  }

  commitGeneratedCode(developmentId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/commit/${developmentId}`, {}, { responseType: 'text' as 'json' });
  }

  generateCode(developmentId: number, customPrompt: string): Observable<any> {
    const url = `${this.apiUrl}/generate/${developmentId}`;
    return this.http.post<any>(url, customPrompt, { headers: { 'Content-Type': 'text/plain' } });
  }

}
