import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, TransactionDto, Page } from '../models/models';

export interface TransactionIngestDto {
  referenceNumber?: string;
  accountId: string;
  transactionType: string;
  channel: string;
  amount: number;
  currency: string;
  counterpartyName?: string;
  counterpartyAccount?: string;
  counterpartyBank?: string;
  counterpartyCountry: string;
  narration?: string;
  bookingDate?: string;
}

export interface IngestionResult {
  transaction: TransactionDto;
  alertsGenerated: number;
  alertIds: string[];
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getTransactions(page = 0, size = 15, customerId?: string, minAmount?: number): Observable<ApiResponse<Page<TransactionDto>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (customerId) params = params.set('customerId', customerId);
    if (minAmount) params = params.set('minAmount', minAmount.toString());

    return this.http.get<ApiResponse<Page<TransactionDto>>>(this.apiUrl, { params });
  }

  getTransactionById(id: string): Observable<ApiResponse<TransactionDto>> {
    return this.http.get<ApiResponse<TransactionDto>>(`${this.apiUrl}/${id}`);
  }

  ingestTransaction(payload: TransactionIngestDto): Observable<ApiResponse<IngestionResult>> {
    return this.http.post<ApiResponse<IngestionResult>>(`${this.apiUrl}/ingest`, payload);
  }
}
