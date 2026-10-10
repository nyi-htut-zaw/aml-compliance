import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AlertDto, AlertDispositionDto, AlertMetrics, Page } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private apiUrl = `${environment.apiUrl}/alerts`;

  constructor(private http: HttpClient) {}

  getAlerts(page = 0, size = 15, status?: string, severity?: string, customerId?: string): Observable<Page<AlertDto>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) params = params.set('status', status);
    if (severity) params = params.set('severity', severity);
    if (customerId) params = params.set('customerId', customerId);

    return this.http.get<Page<AlertDto>>(this.apiUrl, { params });
  }

  getAlertById(id: string): Observable<AlertDto> {
    return this.http.get<AlertDto>(`${this.apiUrl}/${id}`);
  }

  getAlertByNumber(alertNumber: string): Observable<AlertDto> {
    return this.http.get<AlertDto>(`${this.apiUrl}/number/${alertNumber}`);
  }

  assignAlert(id: string, userId: string): Observable<AlertDto> {
    const params = new HttpParams().set('userId', userId);
    return this.http.post<AlertDto>(`${this.apiUrl}/${id}/assign`, null, { params });
  }

  disposeAlert(id: string, dto: AlertDispositionDto): Observable<AlertDto> {
    return this.http.post<AlertDto>(`${this.apiUrl}/${id}/dispose`, dto);
  }

  getMetrics(): Observable<AlertMetrics> {
    return this.http.get<AlertMetrics>(`${this.apiUrl}/metrics`);
  }
}
