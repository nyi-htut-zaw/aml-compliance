import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, CustomerDto, CustomerCreateDto, Page } from '../models/models';

export interface BeneficialOwnerDto {
  id?: string;
  ownerName: string;
  ownerType: string;
  ownershipPercentage: number;
  controlType: string;
  pep: boolean;
  sanctionHit: boolean;
  jurisdictionCountry: string;
  verified: boolean;
}

export interface KycReviewDto {
  id: string;
  customerId: string;
  reviewType: string;
  status: string;
  notes?: string;
  reviewedByUsername?: string;
  completedAt?: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = `${environment.apiUrl}/customers`;

  constructor(private http: HttpClient) {}

  getCustomers(page = 0, size = 10, search?: string, riskRating?: string, kycStatus?: string): Observable<ApiResponse<Page<CustomerDto>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (search) params = params.set('search', search);
    if (riskRating) params = params.set('riskRating', riskRating);
    if (kycStatus) params = params.set('kycStatus', kycStatus);

    return this.http.get<ApiResponse<Page<CustomerDto>>>(this.apiUrl, { params });
  }

  getCustomerById(id: string): Observable<ApiResponse<CustomerDto>> {
    return this.http.get<ApiResponse<CustomerDto>>(`${this.apiUrl}/${id}`);
  }

  createCustomer(payload: CustomerCreateDto): Observable<ApiResponse<CustomerDto>> {
    return this.http.post<ApiResponse<CustomerDto>>(this.apiUrl, payload);
  }

  getBeneficialOwners(customerId: string): Observable<ApiResponse<BeneficialOwnerDto[]>> {
    return this.http.get<ApiResponse<BeneficialOwnerDto[]>>(`${this.apiUrl}/${customerId}/beneficial-owners`);
  }

  addBeneficialOwner(customerId: string, payload: BeneficialOwnerDto): Observable<ApiResponse<BeneficialOwnerDto>> {
    return this.http.post<ApiResponse<BeneficialOwnerDto>>(`${this.apiUrl}/${customerId}/beneficial-owners`, payload);
  }

  getKycReviews(customerId: string): Observable<ApiResponse<KycReviewDto[]>> {
    return this.http.get<ApiResponse<KycReviewDto[]>>(`${environment.apiUrl}/kyc/customer/${customerId}`);
  }
}
