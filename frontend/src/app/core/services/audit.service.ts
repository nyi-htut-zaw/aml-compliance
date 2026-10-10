import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuditEventDto, Page } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AuditService {
  private apiUrl = `${environment.apiUrl}/audit`;

  constructor(private http: HttpClient) {}

  getAuditEvents(page = 0, size = 20, module?: string): Observable<ApiResponse<Page<AuditEventDto>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (module) params = params.set('module', module);

    return this.http.get<ApiResponse<Page<AuditEventDto>>>(this.apiUrl, { params });
  }
}
