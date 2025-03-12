import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MetadataService {
  private apiUrl = 'http://localhost:8080/api/metadata';

  constructor(private http: HttpClient) {}

  getMetadata(developmentId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${developmentId}`);
  }

  generateMetadata(developmentId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/generate/${developmentId}`, {}, { responseType: 'text' as 'json' });
  }
}
