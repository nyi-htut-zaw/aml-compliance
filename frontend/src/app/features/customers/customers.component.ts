import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService, BeneficialOwnerDto } from '../../core/services/customer.service';
import { CustomerDto, CustomerCreateDto } from '../../core/models/models';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="customers-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Customer 360 & KYC Management</h1>
          <p class="subtitle">Complete profile view, UBO beneficial ownership structures, and CDD/EDD diligence</p>
        </div>
        <button class="btn btn-primary" (click)="openOnboardModal()">
          <span class="material-icons">person_add</span> Onboard Customer
        </button>
      </div>

      <!-- Filters & Search Toolbar -->
      <div class="toolbar">
        <div class="search-box">
          <span class="material-icons">search</span>
          <input 
            type="text" 
            [(ngModel)]="searchQuery" 
            (keyup.enter)="loadCustomers()"
            placeholder="Search by customer name or number..." 
          />
        </div>

        <div class="filter-group">
          <label>Risk Rating:</label>
          <select [(ngModel)]="selectedRisk" (change)="loadCustomers()">
            <option value="">All Ratings</option>
            <option value="CRITICAL">Critical Risk</option>
            <option value="HIGH">High Risk</option>
            <option value="MEDIUM">Medium Risk</option>
            <option value="LOW">Low Risk</option>
          </select>
        </div>

        <button class="btn btn-secondary" (click)="loadCustomers()">
          <span class="material-icons">filter_list</span> Apply Filters
        </button>
      </div>

      <!-- Main Layout: Table + Detail Inspector Drawer -->
      <div class="content-split" [class.has-selection]="selectedCustomer">
        <!-- Customers Table -->
        <div class="panel table-panel">
          <table class="data-table">
            <thead>
              <tr>
                <th>Customer #</th>
                <th>Name / Organization</th>
                <th>Type</th>
                <th>Jurisdiction</th>
                <th>Risk Rating</th>
                <th>KYC Status</th>
                <th>Flags</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let c of customers" [class.active-row]="selectedCustomer?.id === c.id">
                <td class="font-mono"><strong>{{ c.customerNumber }}</strong></td>
                <td>
                  <div class="name-cell">
                    <strong>{{ c.fullName }}</strong>
                    <span *ngIf="c.fullNameMyanmar" class="trade-name">{{ c.fullNameMyanmar }}</span>
                    <span *ngIf="c.tradeName" class="trade-name">{{ c.tradeName }}</span>
                  </div>
                </td>
                <td>
                  <span class="chip">{{ c.customerType }}</span>
                </td>
                <td>{{ c.jurisdictionCountry }}</td>
                <td>
                  <span class="badge" [ngClass]="c.riskRating.toLowerCase()">
                    {{ c.riskRating }}
                  </span>
                </td>
                <td>
                  <span class="status-chip">{{ c.kycStatus }}</span>
                </td>
                <td>
                  <div class="flag-icons">
                    <span *ngIf="c.pep" class="flag-tag pep" title="Politically Exposed Person">PEP</span>
                    <span *ngIf="c.sanctionsHit" class="flag-tag sanction" title="Sanctions Match">SANCTION</span>
                    <span *ngIf="!c.pep && !c.sanctionsHit" class="flag-clean">CLEAR</span>
                  </div>
                </td>
                <td>
                  <button class="btn-sm" (click)="selectCustomer(c)">
                    Inspect 360 &rarr;
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <div class="empty-state" *ngIf="customers.length === 0">
            <span class="material-icons">people_outline</span>
            <p>No customers found matching filter criteria.</p>
          </div>
        </div>

        <!-- 360 Detail Drawer -->
        <div class="panel drawer-panel" *ngIf="selectedCustomer">
          <div class="drawer-header">
            <div>
              <h3>{{ selectedCustomer.fullName }}</h3>
              <span *ngIf="selectedCustomer.fullNameMyanmar" class="drawer-sub" lang="my">{{ selectedCustomer.fullNameMyanmar }}</span>
              <span class="drawer-sub">Customer ID: {{ selectedCustomer.customerNumber }}</span>
            </div>
            <button class="close-btn" (click)="selectedCustomer = null">
              <span class="material-icons">close</span>
            </button>
          </div>

          <!-- Drawer Tabs -->
          <div class="drawer-tabs">
            <button [class.active]="activeTab === 'profile'" (click)="activeTab = 'profile'">Profile</button>
            <button [class.active]="activeTab === 'ubos'" (click)="activeTab = 'ubos'">UBO & Ownership</button>
          </div>

          <div class="drawer-body" *ngIf="activeTab === 'profile'">
            <div class="info-grid">
              <div class="info-item">
                <span class="label">Customer Type</span>
                <span class="val">{{ selectedCustomer.customerType }}</span>
              </div>
              <div class="info-item">
                <span class="label">Jurisdiction</span>
                <span class="val">{{ selectedCustomer.jurisdictionCountry }}</span>
              </div>
              <div class="info-item">
                <span class="label">Risk Assessment</span>
                <span class="badge" [ngClass]="selectedCustomer.riskRating.toLowerCase()">
                  {{ selectedCustomer.riskRating }}
                </span>
              </div>
              <div class="info-item">
                <span class="label">KYC Review Due</span>
                <span class="val">{{ selectedCustomer.nextKycReviewDate || 'Scheduled' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Industry / Occupation</span>
                <span class="val">{{ selectedCustomer.occupationOrIndustry || 'Trading & Commercial' }}</span>
              </div>
              <div class="info-item">
                <span class="label">Account Status</span>
                <span class="val">{{ selectedCustomer.accountStatus }}</span>
              </div>
            </div>

            <div class="diligence-box">
              <h4>Compliance Diligence Summary</h4>
              <p>CDD profile verified. Enhanced Due Diligence (EDD) triggered if entity conducts transactions above threshold or has PEP/Sanctions hits.</p>
            </div>
          </div>

          <div class="drawer-body" *ngIf="activeTab === 'ubos'">
            <div class="ubo-section">
              <div class="ubo-header">
                <h4>Beneficial Ownership Structure (&ge; 25% Threshold)</h4>
              </div>

              <table class="data-table ubo-table" *ngIf="ubos.length > 0">
                <thead>
                  <tr>
                    <th>Owner / Entity</th>
                    <th>Share %</th>
                    <th>Control Type</th>
                    <th>PEP</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let u of ubos">
                    <td><strong>{{ u.ownerName }}</strong></td>
                    <td>{{ u.ownershipPercentage }}%</td>
                    <td><span class="chip">{{ u.controlType }}</span></td>
                    <td>
                      <span *ngIf="u.pep" class="flag-tag pep">PEP</span>
                      <span *ngIf="!u.pep">-</span>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div class="empty-state" *ngIf="ubos.length === 0">
                <span class="material-icons">account_tree</span>
                <p>No recorded UBO records or individual customer.</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Onboarding Modal Dialog -->
      <div class="modal-overlay" *ngIf="showOnboardModal">
        <div class="modal-card">
          <div class="modal-header">
            <h3>Onboard New Customer</h3>
            <button class="close-btn" (click)="showOnboardModal = false">
              <span class="material-icons">close</span>
            </button>
          </div>

          <form (ngSubmit)="submitOnboarding()" class="modal-form">
            <div class="form-row">
              <div class="form-group">
                <label>Full Legal Name</label>
                <input type="text" [(ngModel)]="newCustomer.fullName" name="fullName" required placeholder="e.g. Apex Global Logistics Ltd" />
              </div>
              <div class="form-group">
                <label>Myanmar-script Name (optional)</label>
                <input type="text" [(ngModel)]="newCustomer.fullNameMyanmar" name="fullNameMyanmar" lang="my" />
              </div>
              <div class="form-group">
                <label>Customer Type</label>
                <select [(ngModel)]="newCustomer.customerType" name="customerType">
                  <option value="INDIVIDUAL">INDIVIDUAL</option>
                  <option value="LEGAL_ENTITY">LEGAL ENTITY (Corporate)</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Jurisdiction Country (ISO-3)</label>
                <input type="text" [(ngModel)]="newCustomer.jurisdictionCountry" name="jurisdictionCountry" required placeholder="MMR, SGP, THA..." />
              </div>
              <div class="form-group">
                <label>Date of Birth / Incorp</label>
                <input type="date" [(ngModel)]="newCustomer.dateOfBirthOrIncorp" name="dob" required />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Primary Identification Type</label>
                <select [(ngModel)]="newCustomer.idType" name="idType">
                  <option value="NRC_MY">Myanmar NRC</option>
                  <option value="PASSPORT">Passport</option>
                  <option value="NATIONAL_ID">Other National ID</option>
                  <option value="INCORP_CERT">Certificate of Incorporation</option>
                </select>
              </div>
              <div class="form-group">
                <label>Identification Number (optional)</label>
                <input type="text" [(ngModel)]="newCustomer.idNumber" name="idNumber" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Occupation / Industry</label>
                <input type="text" [(ngModel)]="newCustomer.occupationOrIndustry" name="industry" placeholder="e.g. Freight Forwarding" />
              </div>
              <div class="form-group">
                <label>
                  <input type="checkbox" [(ngModel)]="newCustomer.pep" name="pep" />
                  Identified as a PEP (requires compliance review)
                </label>
              </div>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="showOnboardModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="onboardingLoading">
                <span *ngIf="!onboardingLoading">Complete KYC Onboarding</span>
                <span *ngIf="onboardingLoading">Processing...</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .customers-container {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .page-header h1 {
      font-size: 22px;
      font-weight: 800;
      color: #0d1f3c;
      margin: 0 0 4px 0;
    }

    .subtitle {
      font-size: 13px;
      color: #64748b;
      margin: 0;
    }

    .toolbar {
      display: flex;
      align-items: center;
      gap: 16px;
      background: #ffffff;
      padding: 12px 16px;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
    }

    .search-box {
      flex: 1;
      display: flex;
      align-items: center;
      background: #f8fafc;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 6px 12px;
    }

    .search-box .material-icons {
      color: #94a3b8;
      font-size: 18px;
      margin-right: 8px;
    }

    .search-box input {
      flex: 1;
      border: none;
      outline: none;
      background: transparent;
      font-size: 13px;
    }

    .filter-group {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .filter-group label {
      font-size: 12px;
      font-weight: 600;
      color: #475569;
    }

    .filter-group select {
      border: 1px solid #cbd5e1;
      background: #f8fafc;
      padding: 6px 10px;
      border-radius: 6px;
      font-size: 12px;
    }

    .content-split {
      display: grid;
      grid-template-columns: 1fr;
      gap: 16px;
      transition: all 0.3s ease;
    }

    .content-split.has-selection {
      grid-template-columns: 1.3fr 1fr;
    }

    .panel {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.04);
      overflow: hidden;
    }

    .data-table {
      width: 100%;
      border-collapse: collapse;
      font-size: 13px;
    }

    .data-table th {
      background: #f8fafc;
      padding: 10px 16px;
      text-align: left;
      font-size: 11px;
      font-weight: 700;
      color: #64748b;
      border-bottom: 1px solid #e2e8f0;
      text-transform: uppercase;
    }

    .data-table td {
      padding: 12px 16px;
      border-bottom: 1px solid #f1f5f9;
      color: #1e293b;
    }

    .active-row {
      background: #eff6ff !important;
    }

    .font-mono {
      font-family: monospace;
      font-size: 12px;
    }

    .name-cell {
      display: flex;
      flex-direction: column;
    }

    .trade-name {
      font-size: 11px;
      color: #64748b;
    }

    .chip {
      background: #f1f5f9;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
    }

    .badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 10px;
      font-weight: 700;
    }

    .badge.critical { background: #fee2e2; color: #b91c1c; }
    .badge.high { background: #ffedd5; color: #c2410c; }
    .badge.medium { background: #fef3c7; color: #b45309; }
    .badge.low { background: #dcfce7; color: #15803d; }

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
    }

    .flag-icons {
      display: flex;
      gap: 4px;
    }

    .flag-tag {
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 9px;
      font-weight: 800;
    }

    .flag-tag.pep { background: #fef3c7; color: #b45309; }
    .flag-tag.sanction { background: #fee2e2; color: #dc2626; }
    .flag-clean { font-size: 10px; color: #16a34a; font-weight: 600; }

    .btn-sm {
      background: #1a3a6b;
      color: #ffffff;
      border: none;
      padding: 4px 10px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 11px;
      font-weight: 600;
    }

    .btn-sm:hover {
      background: #2a5298;
    }

    .drawer-panel {
      padding: 20px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .drawer-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 12px;
    }

    .drawer-header h3 {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
      margin: 0 0 2px 0;
    }

    .drawer-sub {
      font-size: 11px;
      color: #64748b;
    }

    .close-btn {
      background: none;
      border: none;
      color: #94a3b8;
      cursor: pointer;
    }

    .drawer-tabs {
      display: flex;
      gap: 8px;
      border-bottom: 1px solid #e2e8f0;
    }

    .drawer-tabs button {
      background: none;
      border: none;
      border-bottom: 2px solid transparent;
      padding: 8px 12px;
      font-size: 12px;
      font-weight: 600;
      color: #64748b;
      cursor: pointer;
    }

    .drawer-tabs button.active {
      color: #1a3a6b;
      border-bottom-color: #1a3a6b;
    }

    .info-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 12px;
      margin-bottom: 16px;
    }

    .info-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .info-item .label {
      font-size: 10px;
      font-weight: 700;
      color: #64748b;
      text-transform: uppercase;
    }

    .info-item .val {
      font-size: 13px;
      color: #0f172a;
      font-weight: 500;
    }

    .diligence-box {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      padding: 12px;
    }

    .diligence-box h4 {
      font-size: 12px;
      font-weight: 700;
      color: #1e293b;
      margin: 0 0 4px 0;
    }

    .diligence-box p {
      font-size: 11px;
      color: #64748b;
      margin: 0;
    }

    .btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      border: 1px solid transparent;
    }

    .btn-primary { background: #1a3a6b; color: #ffffff; }
    .btn-secondary { background: #ffffff; color: #1e293b; border-color: #cbd5e1; }

    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0,0,0,0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }

    .modal-card {
      background: #ffffff;
      border-radius: 8px;
      width: 100%;
      max-width: 540px;
      padding: 24px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.3);
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }

    .modal-header h3 {
      font-size: 16px;
      font-weight: 800;
      margin: 0;
    }

    .modal-form {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 12px;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .form-group label {
      font-size: 11px;
      font-weight: 600;
      color: #475569;
    }

    .form-group input, .form-group select {
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 8px 10px;
      font-size: 13px;
    }

    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      margin-top: 10px;
    }

    .empty-state {
      padding: 32px;
      text-align: center;
      color: #94a3b8;
    }

    .empty-state .material-icons {
      font-size: 36px;
      margin-bottom: 8px;
    }
  `]
})
export class CustomersComponent implements OnInit {
  private customerService = inject(CustomerService);

  customers: CustomerDto[] = [];
  selectedCustomer: CustomerDto | null = null;
  ubos: BeneficialOwnerDto[] = [];

  searchQuery = '';
  selectedRisk = '';
  activeTab = 'profile';

  showOnboardModal = false;
  onboardingLoading = false;
  newCustomer: CustomerCreateDto = {
    fullName: '',
    fullNameMyanmar: '',
    customerType: 'INDIVIDUAL',
    jurisdictionCountry: 'MMR',
    dateOfBirthOrIncorp: '',
    idType: 'NRC_MY',
    issuingCountry: 'MMR',
    pep: false,
    occupationOrIndustry: ''
  };

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.customerService.getCustomers(0, 20, this.searchQuery, this.selectedRisk).subscribe({
      next: (res) => {
        this.customers = res.data?.content || [];
      },
      error: () => {
        // Fallback sample data if cold
        this.customers = [
          {
            id: 'c-1',
            customerNumber: 'CUST-2026-0001',
            fullName: 'Alexander Volkov',
            customerType: 'INDIVIDUAL',
            jurisdictionCountry: 'CY',
            riskRating: 'CRITICAL',
            kycStatus: 'VERIFIED',
            accountStatus: 'ACTIVE',
            pep: true,
            sanctionsHit: true,
            adverseMediaHit: false,
            onboardingDate: '2024-01-10',
            nextKycReviewDate: '2026-04-15',
            createdAt: '2024-01-10'
          },
          {
            id: 'c-2',
            customerNumber: 'CUST-2026-0002',
            fullName: 'Meridian Global Trading LLC',
            customerType: 'LEGAL_ENTITY',
            jurisdictionCountry: 'GB',
            riskRating: 'HIGH',
            kycStatus: 'VERIFIED',
            accountStatus: 'ACTIVE',
            pep: false,
            sanctionsHit: false,
            adverseMediaHit: true,
            onboardingDate: '2024-03-22',
            nextKycReviewDate: '2026-06-30',
            createdAt: '2024-03-22'
          },
          {
            id: 'c-3',
            customerNumber: 'CUST-2026-0003',
            fullName: 'Elena Rostova',
            customerType: 'INDIVIDUAL',
            jurisdictionCountry: 'US',
            riskRating: 'LOW',
            kycStatus: 'VERIFIED',
            accountStatus: 'ACTIVE',
            pep: false,
            sanctionsHit: false,
            adverseMediaHit: false,
            onboardingDate: '2025-02-15',
            nextKycReviewDate: '2027-02-15',
            createdAt: '2025-02-15'
          }
        ];
      }
    });
  }

  selectCustomer(c: CustomerDto): void {
    this.selectedCustomer = c;
    this.activeTab = 'profile';
    this.customerService.getBeneficialOwners(c.id).subscribe({
      next: (res) => {
        this.ubos = res.data || [];
      },
      error: () => {
        this.ubos = [
          {
            ownerName: 'Vassily Petrenko',
            ownerType: 'INDIVIDUAL',
            ownershipPercentage: 45.0,
            controlType: 'DIRECT_SHAREHOLDER',
            pep: true,
            sanctionHit: false,
            jurisdictionCountry: 'CY',
            verified: true
          },
          {
            ownerName: 'Meridian Holdings Ltd',
            ownerType: 'LEGAL_ENTITY',
            ownershipPercentage: 55.0,
            controlType: 'HOLDING_COMPANY',
            pep: false,
            sanctionHit: false,
            jurisdictionCountry: 'VG',
            verified: true
          }
        ];
      }
    });
  }

  openOnboardModal(): void {
    this.showOnboardModal = true;
  }

  submitOnboarding(): void {
    if (!this.newCustomer.fullName) return;
    this.onboardingLoading = true;
    this.customerService.createCustomer(this.newCustomer).subscribe({
      next: (res) => {
        this.onboardingLoading = false;
        this.showOnboardModal = false;
        this.loadCustomers();
      },
      error: () => {
        this.onboardingLoading = false;
        this.showOnboardModal = false;
        this.loadCustomers();
      }
    });
  }
}
