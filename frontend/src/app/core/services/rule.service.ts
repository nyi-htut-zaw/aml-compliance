import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RuleDto } from '../models/models';

export interface RuleUpdateDto {
  parametersJson?: string;
  riskWeight?: number;
  severity?: string;
  active?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class RuleService {
  private apiUrl = `${environment.apiUrl}/rules`;

  constructor(private http: HttpClient) {}

  getRules(ruleType?: string): Observable<RuleDto[]> {
    const url = ruleType ? `${this.apiUrl}?ruleType=${ruleType}` : this.apiUrl;
    return this.http.get<RuleDto[]>(url);
  }

  getRule(id: string): Observable<RuleDto> {
    return this.http.get<RuleDto>(`${this.apiUrl}/${id}`);
  }

  updateRule(id: string, dto: RuleUpdateDto): Observable<RuleDto> {
    return this.http.put<RuleDto>(`${this.apiUrl}/${id}`, dto);
  }
}
