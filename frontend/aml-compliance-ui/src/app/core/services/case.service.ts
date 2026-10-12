import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CaseDto, CaseCreateDto, Page } from '../models/models';

export interface CaseStatusUpdateDto {
  status: string;
  reason: string;
}

export interface CaseNoteDto {
  noteText: string;
  isInternal: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CaseService {
  private apiUrl = `${environment.apiUrl}/cases`;

  constructor(private http: HttpClient) {}

  getCases(page = 0, size = 15, status?: string, priority?: string, customerId?: string): Observable<Page<CaseDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) params = params.set('status', status);
    if (priority) params = params.set('priority', priority);
    if (customerId) params = params.set('customerId', customerId);

    return this.http.get<Page<CaseDto>>(this.apiUrl, { params });
  }

  getCaseById(id: string): Observable<CaseDto> {
    return this.http.get<CaseDto>(`${this.apiUrl}/${id}`);
  }

  createCase(dto: CaseCreateDto): Observable<CaseDto> {
    return this.http.post<CaseDto>(this.apiUrl, dto);
  }

  updateStatus(id: string, dto: CaseStatusUpdateDto): Observable<CaseDto> {
    return this.http.patch<CaseDto>(`${this.apiUrl}/${id}/status`, dto);
  }

  addNote(id: string, note: CaseNoteDto): Observable<CaseDto> {
    return this.http.post<CaseDto>(`${this.apiUrl}/${id}/notes`, note);
  }
}
