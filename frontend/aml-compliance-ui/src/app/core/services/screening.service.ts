import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, ScreeningMatchDto, Page } from '../models/models';

export interface ScreeningRequestDto {
  queryName: string;
  countryCode?: string;
  dateOfBirth?: string;
  algorithm?: 'FUZZY_JARO_WINKLER' | 'FUZZY_LEVENSHTEIN' | 'EXACT';
  threshold?: number;
}

export interface ScreeningDispositionDto {
  disposition: 'FALSE_POSITIVE' | 'CONFIRMED_TRUE_HIT' | 'ESCALATED';
  notes: string;
}

export interface WatchlistSourceDto {
  id: string;
  name: string;
  code: string;
  listType: string;
  version: string;
  provider: string;
  itemCount: number;
  lastUpdatedAt: string;
  active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ScreeningService {
  private apiUrl = `${environment.apiUrl}/screening`;

  constructor(private http: HttpClient) {}

  screenCandidate(req: ScreeningRequestDto): Observable<ApiResponse<ScreeningMatchDto[]>> {
    return this.http.post<ApiResponse<ScreeningMatchDto[]>>(`${this.apiUrl}/search`, req);
  }

  getPendingMatches(page = 0, size = 15): Observable<ApiResponse<Page<ScreeningMatchDto>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<Page<ScreeningMatchDto>>>(`${this.apiUrl}/matches/pending`, { params });
  }

  getCustomerMatches(customerId: string): Observable<ApiResponse<ScreeningMatchDto[]>> {
    return this.http.get<ApiResponse<ScreeningMatchDto[]>>(`${this.apiUrl}/matches/customer/${customerId}`);
  }

  adjudicateMatch(matchId: string, dto: ScreeningDispositionDto): Observable<ApiResponse<ScreeningMatchDto>> {
    return this.http.post<ApiResponse<ScreeningMatchDto>>(`${this.apiUrl}/matches/${matchId}/disposition`, dto);
  }

  getWatchlists(): Observable<ApiResponse<WatchlistSourceDto[]>> {
    return this.http.get<ApiResponse<WatchlistSourceDto[]>>(`${this.apiUrl}/watchlists`);
  }
}
