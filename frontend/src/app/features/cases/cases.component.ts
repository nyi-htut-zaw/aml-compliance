import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CaseService, CaseNoteDto } from '../../core/services/case.service';
import { CustomerService } from '../../core/services/customer.service';
import { CaseDto, CaseCreateDto, CustomerDto } from '../../core/models/models';

@Component({
  selector: 'app-cases',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="cases-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Financial Crime Investigation Workspaces</h1>
          <p class="subtitle">Multi-entity case lifecycle, maker-checker escalation to MLRO, and STR preparation</p>
        </div>
        <button class="btn btn-primary" (click)="openCreateModal()">
          <span class="material-icons">create_new_folder</span> Create Investigation Case
        </button>
      </div>

      <!-- Filters & Stats -->
      <div class="toolbar">
        <div class="filter-group">
          <label>Status:</label>
          <select [(ngModel)]="selectedStatus" (change)="loadCases()">
            <option value="">All Statuses</option>
            <option value="NEW">New</option>
            <option value="UNDER_INVESTIGATION">Under Investigation</option>
            <option value="ESCALATED_TO_MLRO">Escalated to MLRO</option>
            <option value="CLOSED_SUSPICIOUS">Closed Suspicious</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Priority:</label>
          <select [(ngModel)]="selectedPriority" (change)="loadCases()">
            <option value="">All Priorities</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
          </select>
        </div>

        <button class="btn btn-secondary" (click)="loadCases()">
          <span class="material-icons">refresh</span> Refresh
        </button>
      </div>

      <!-- Main Layout: Table + Workspace Inspector -->
      <div class="content-split" [class.has-selection]="selectedCase">
        <!-- Cases List -->
        <div class="panel">
          <table class="data-table">
            <thead>
              <tr>
                <th>Case #</th>
                <th>Title / Scope</th>
                <th>Subject Entity</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Evidence Count</th>
                <th>SLA Due</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let c of cases" [class.active-row]="selectedCase?.id === c.id">
                <td class="font-mono"><strong>{{ c.caseNumber }}</strong></td>
                <td><strong>{{ c.title }}</strong></td>
                <td>{{ c.customerName }}</td>
                <td>
                  <span class="badge" [ngClass]="c.priority.toLowerCase()">
                    {{ c.priority }}
                  </span>
                </td>
                <td>
                  <span class="status-chip" [class.escalated]="c.status === 'ESCALATED_TO_MLRO'">
                    {{ c.status }}
                  </span>
                </td>
                <td>
                  <span class="chip">{{ c.alertsCount }} Alerts &bull; {{ c.transactionsCount }} Txs</span>
                </td>
                <td>{{ c.slaDueAt | date:'shortDate' }}</td>
                <td>
                  <button class="btn-sm" (click)="selectCase(c)">
                    Open Workspace &rarr;
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <div class="empty-state" *ngIf="cases.length === 0">
            <span class="material-icons">folder_off</span>
            <p>No investigation cases found.</p>
          </div>
        </div>

        <!-- Investigation Workspace Drawer -->
        <div class="panel drawer-panel" *ngIf="selectedCase">
          <div class="drawer-header">
            <div>
              <h3>{{ selectedCase.caseNumber }}</h3>
              <span class="drawer-title">{{ selectedCase.title }}</span>
            </div>
            <button class="close-btn" (click)="selectedCase = null">&times;</button>
          </div>

          <div class="drawer-body">
            <!-- Context Badges -->
            <div class="badge-strip">
              <span class="badge" [ngClass]="selectedCase.priority.toLowerCase()">
                Priority: {{ selectedCase.priority }}
              </span>
              <span class="status-chip">Status: {{ selectedCase.status }}</span>
              <span class="chip">Analyst: {{ selectedCase.primaryAnalystName || 'Assigned' }}</span>
            </div>

            <!-- Subject Entity Summary -->
            <div class="subject-card">
              <span class="sub-label">SUBJECT OF INVESTIGATION</span>
              <h4>{{ selectedCase.customerName }}</h4>
              <span class="sub-id">Customer Number: {{ selectedCase.customerNumber }}</span>
            </div>

            <!-- Action Toolbar: Escalate / Change Status -->
            <div class="actions-panel">
              <h4>Workflow Actions</h4>
              <div class="action-buttons">
                <button 
                  class="btn btn-warning btn-sm-action" 
                  *ngIf="selectedCase.status !== 'ESCALATED_TO_MLRO'" 
                  (click)="escalateToMlro()"
                >
                  <span class="material-icons">gavel</span> Escalate to MLRO
                </button>

                <button 
                  class="btn btn-primary btn-sm-action" 
                  *ngIf="selectedCase.status !== 'CLOSED_SUSPICIOUS'" 
                  (click)="closeAsSuspicious()"
                >
                  <span class="material-icons">report</span> Close as Suspicious
                </button>
              </div>
            </div>

            <!-- Investigator Notes Form -->
            <div class="notes-section">
              <h4>Add Investigator Due Diligence Note</h4>
              <textarea 
                rows="2" 
                [(ngModel)]="newNoteText" 
                placeholder="Log interviews, documentary checks, or open-source intelligence findings..."
              ></textarea>
              <button class="btn btn-secondary btn-sm" (click)="addNote()" [disabled]="!newNoteText">
                Save Note to Dossier
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Create Case Modal -->
      <div class="modal-overlay" *ngIf="showCreateModal">
        <div class="modal-card">
          <div class="modal-header">
            <h3>Initiate Financial Crime Investigation Case</h3>
            <button class="close-btn" (click)="showCreateModal = false">&times;</button>
          </div>

          <form (ngSubmit)="submitCreateCase()" class="modal-form">
            <div class="form-group">
              <label>Case Title / Investigation Topic</label>
              <input type="text" [(ngModel)]="newCase.title" name="title" required placeholder="e.g. Cross-Border Shell Company Funnel Investigation" />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Subject Customer</label>
                <select [(ngModel)]="newCase.customerId" name="customerId" required>
                  <option *ngFor="let c of customers" [value]="c.id">
                    {{ c.fullName }} ({{ c.customerNumber }})
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>Priority</label>
                <select [(ngModel)]="newCase.priority" name="priority">
                  <option value="CRITICAL">CRITICAL</option>
                  <option value="HIGH">HIGH</option>
                  <option value="MEDIUM">MEDIUM</option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label>Initial Hypothesis / Predicate Crime Allegation</label>
              <textarea rows="3" [(ngModel)]="newCase.description" name="desc" required placeholder="Describe triggering transactions, trade anomalies, or sanctions proximity..."></textarea>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="showCreateModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="createLoading">
                <span *ngIf="!createLoading">Open Case Dossier</span>
                <span *ngIf="createLoading">Creating...</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .cases-container {
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

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
    }

    .status-chip.escalated {
      background: #fef3c7;
      color: #b45309;
      font-weight: 800;
    }

    .chip {
      background: #f1f5f9;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
    }

    .btn-sm {
      background: #1a3a6b;
      color: #ffffff;
      border: none;
      padding: 5px 12px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 11px;
      font-weight: 600;
    }

    .btn-sm:hover { background: #2a5298; }

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
      padding-bottom: 10px;
    }

    .drawer-header h3 {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
      margin: 0;
    }

    .drawer-title {
      font-size: 13px;
      color: #475569;
      font-weight: 600;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 20px;
      color: #94a3b8;
      cursor: pointer;
    }

    .badge-strip {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;
    }

    .subject-card {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      padding: 12px;
      margin-bottom: 14px;
    }

    .sub-label {
      font-size: 9px;
      font-weight: 700;
      color: #64748b;
      letter-spacing: 0.5px;
    }

    .subject-card h4 {
      margin: 4px 0 2px 0;
      font-size: 14px;
      color: #0f172a;
    }

    .sub-id {
      font-size: 11px;
      color: #64748b;
    }

    .actions-panel {
      margin-bottom: 16px;
    }

    .actions-panel h4 {
      font-size: 12px;
      font-weight: 700;
      color: #1e293b;
      margin: 0 0 8px 0;
    }

    .action-buttons {
      display: flex;
      gap: 8px;
    }

    .btn-sm-action {
      font-size: 11px;
      padding: 6px 10px;
    }

    .btn-warning {
      background: #f59e0b;
      color: #ffffff;
      border: none;
    }

    .btn-warning:hover { background: #d97706; }

    .notes-section {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .notes-section h4 {
      font-size: 12px;
      font-weight: 700;
      color: #1e293b;
      margin: 0;
    }

    .notes-section textarea {
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 8px;
      font-size: 12px;
      font-family: inherit;
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
      top: 0; left: 0; right: 0; bottom: 0;
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
      max-width: 560px;
      padding: 24px;
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

    .form-group input, .form-group select, .form-group textarea {
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
export class CasesComponent implements OnInit {
  private caseService = inject(CaseService);
  private customerService = inject(CustomerService);

  cases: CaseDto[] = [];
  customers: CustomerDto[] = [];
  selectedCase: CaseDto | null = null;
  selectedStatus = '';
  selectedPriority = '';
  newNoteText = '';

  showCreateModal = false;
  createLoading = false;
  newCase: CaseCreateDto = {
    title: '',
    description: '',
    priority: 'HIGH',
    customerId: ''
  };

  ngOnInit(): void {
    this.loadCases();
    this.customerService.getCustomers(0, 10).subscribe({
      next: (res) => {
        this.customers = res.data?.content || [];
        if (this.customers.length > 0) {
          this.newCase.customerId = this.customers[0].id;
        }
      }
    });
  }

  loadCases(): void {
    this.caseService.getCases(0, 20, this.selectedStatus || undefined, this.selectedPriority || undefined).subscribe({
      next: (res) => {
        this.cases = res.content || [];
      },
      error: () => {
        // Fallback sample data
        this.cases = [
          {
            id: 'case-1',
            caseNumber: 'CASE-2026-0001',
            title: 'Layering Scheme Across Offshore Jurisdictions',
            priority: 'CRITICAL',
            status: 'ESCALATED_TO_MLRO',
            customerId: 'c-1',
            customerNumber: 'CUST-2026-0001',
            customerName: 'Alexander Volkov',
            primaryAnalystName: 'analyst.jones',
            slaDueAt: new Date(Date.now() + 86400000).toISOString(),
            slaBreached: false,
            alertsCount: 3,
            transactionsCount: 8,
            evidenceCount: 5,
            createdAt: new Date().toISOString()
          },
          {
            id: 'case-2',
            caseNumber: 'CASE-2026-0002',
            title: 'Smurfing & Structuring Inward Remittances',
            priority: 'HIGH',
            status: 'UNDER_INVESTIGATION',
            customerId: 'c-2',
            customerNumber: 'CUST-2026-0002',
            customerName: 'Meridian Global Trading LLC',
            primaryAnalystName: 'analyst.jones',
            slaDueAt: new Date(Date.now() + 172800000).toISOString(),
            slaBreached: false,
            alertsCount: 2,
            transactionsCount: 14,
            evidenceCount: 2,
            createdAt: new Date().toISOString()
          }
        ];
      }
    });
  }

  selectCase(c: CaseDto): void {
    this.selectedCase = c;
  }

  openCreateModal(): void {
    this.showCreateModal = true;
  }

  submitCreateCase(): void {
    if (!this.newCase.title || !this.newCase.customerId) return;
    this.createLoading = true;
    this.caseService.createCase(this.newCase).subscribe({
      next: (c) => {
        this.createLoading = false;
        this.showCreateModal = false;
        this.loadCases();
        this.selectedCase = c;
      },
      error: () => {
        this.createLoading = false;
        this.showCreateModal = false;
        this.loadCases();
      }
    });
  }

  escalateToMlro(): void {
    if (!this.selectedCase) return;
    this.caseService.updateStatus(this.selectedCase.id, {
      status: 'ESCALATED_TO_MLRO',
      reason: 'Dual-control escalation: Evidence indicates cross-border layering scheme.'
    }).subscribe({
      next: (c) => {
        this.selectedCase = c;
        this.loadCases();
      },
      error: () => {
        if (this.selectedCase) this.selectedCase.status = 'ESCALATED_TO_MLRO';
        this.loadCases();
      }
    });
  }

  closeAsSuspicious(): void {
    if (!this.selectedCase) return;
    this.caseService.updateStatus(this.selectedCase.id, {
      status: 'CLOSED_SUSPICIOUS',
      reason: 'Investigation concluded: Material financial crime indicators confirmed. Prepare STR.'
    }).subscribe({
      next: (c) => {
        this.selectedCase = c;
        this.loadCases();
      },
      error: () => {
        if (this.selectedCase) this.selectedCase.status = 'CLOSED_SUSPICIOUS';
        this.loadCases();
      }
    });
  }

  addNote(): void {
    if (!this.selectedCase || !this.newNoteText) return;
    this.caseService.addNote(this.selectedCase.id, {
      noteText: this.newNoteText,
      isInternal: true
    }).subscribe({
      next: () => {
        this.newNoteText = '';
      },
      error: () => {
        this.newNoteText = '';
      }
    });
  }
}
