import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Development {
  id: number;
  title: string;
  description: string;
  projectId: number;
  project: { id: number, repositoryUrl: string, documentsPath: string };
}

@Injectable({
  providedIn: 'root'
})
export class DevelopmentsService {
  private apiUrl = 'http://localhost:8080/api/development';

  constructor(private http: HttpClient) { }

  getDevelopmentsByProject(projectId: number): Observable<Development[]> {
    return this.http.get<Development[]>(`${this.apiUrl}/project/${projectId}`);
  }

  createDevelopment(projectId: number, title: string, description: string): Observable<Development> {
    const development = {
      title,
      description,
      project: { id: projectId }
    };

    return this.http.post<Development>(`${this.apiUrl}`, development, {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  getDevelopmentById(developmentId: number): Observable<Development> {
    return this.http.get<Development>(`${this.apiUrl}/${developmentId}`);
  }

  updateDevelopment(developmentId: number, updatedData: { title: string; description: string }): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${developmentId}`, updatedData);
  }

}
