import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReportDto, Page } from '../models/models';

export interface ReportCreateDto {
  caseId: string;
  reportType: string;
  jurisdictionCountry: string;
  narrative: string;
  totalAmountUsd: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  getReports(page = 0, size = 15, status?: string, reportType?: string): Observable<Page<ReportDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) params = params.set('status', status);
    if (reportType) params = params.set('reportType', reportType);

    return this.http.get<Page<ReportDto>>(this.apiUrl, { params });
  }

  getReportById(id: string): Observable<ReportDto> {
    return this.http.get<ReportDto>(`${this.apiUrl}/${id}`);
  }

  createReport(dto: ReportCreateDto): Observable<ReportDto> {
    return this.http.post<ReportDto>(this.apiUrl, dto);
  }

  approveReport(id: string): Observable<ReportDto> {
    return this.http.post<ReportDto>(`${this.apiUrl}/${id}/approve`, {});
  }

  exportReport(id: string): Observable<ReportDto> {
    return this.http.post<ReportDto>(`${this.apiUrl}/${id}/export`, {});
  }
}
