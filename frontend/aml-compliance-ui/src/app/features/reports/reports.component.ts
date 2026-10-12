import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService, ReportCreateDto } from '../../core/services/report.service';
import { CaseService } from '../../core/services/case.service';
import { ReportDto, CaseDto } from '../../core/models/models';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="reports-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Regulatory Filings & goAML Reporting</h1>
          <p class="subtitle">Suspicious Transaction Reports (STR/SAR), Currency Transaction Reports (CTR), and MLRO sign-offs</p>
        </div>
        <button class="btn btn-primary" (click)="openDraftModal()">
          <span class="material-icons">post_add</span> Draft SAR / STR Report
        </button>
      </div>

      <!-- Reports Table -->
      <div class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>Report #</th>
              <th>Type</th>
              <th>Subject Entity</th>
              <th>Linked Case</th>
              <th>Total Vol USD</th>
              <th>Status</th>
              <th>Created Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let r of reports">
              <td class="font-mono"><strong>{{ r.reportNumber }}</strong></td>
              <td><span class="type-pill" [class.sar]="r.reportType === 'SAR'">{{ r.reportType }}</span></td>
              <td><strong>{{ r.subjectName }}</strong></td>
              <td><span class="font-mono">{{ r.caseNumber }}</span></td>
              <td><strong>\${{ r.totalAmountUsd | number:'1.2-2' }}</strong></td>
              <td>
                <span class="status-chip" [class.approved]="r.status === 'APPROVED'" [class.draft]="r.status === 'DRAFT'">
                  {{ r.status }}
                </span>
              </td>
              <td>{{ r.createdAt | date:'shortDate' }}</td>
              <td>
                <div class="action-btns">
                  <button class="btn-sm" (click)="viewReport(r)">Inspect & Export</button>
                  <button 
                    class="btn-sm btn-approve" 
                    *ngIf="r.status === 'DRAFT' || r.status === 'PENDING_APPROVAL'" 
                    (click)="approveReport(r)"
                  >
                    MLRO Sign-off
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="empty-state" *ngIf="reports.length === 0">
          <span class="material-icons">description</span>
          <p>No regulatory reports prepared yet.</p>
        </div>
      </div>

      <!-- Report Detail & JSON Preview Modal -->
      <div class="modal-overlay" *ngIf="selectedReport">
        <div class="modal-card wide">
          <div class="modal-header">
            <div>
              <h3>{{ selectedReport.reportNumber }} &bull; {{ selectedReport.reportType }} Filing Dossier</h3>
              <span class="modal-sub">Jurisdiction: {{ selectedReport.jurisdictionCountry }} &bull; Status: {{ selectedReport.status }}</span>
            </div>
            <button class="close-btn" (click)="selectedReport = null">&times;</button>
          </div>

          <div class="modal-body">
            <div class="report-meta-grid">
              <div class="meta-item">
                <span class="label">Subject Customer</span>
                <span class="val">{{ selectedReport.subjectName }}</span>
              </div>
              <div class="meta-item">
                <span class="label">Total Inflow/Outflow</span>
                <span class="val">\${{ selectedReport.totalAmountUsd | number:'1.2-2' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">Prepared By</span>
                <span class="val">{{ selectedReport.preparedByUsername || 'analyst.jones' }}</span>
              </div>
              <div class="meta-item">
                <span class="label">MLRO Approver</span>
                <span class="val">{{ selectedReport.approvedByUsername || 'Pending Dual Sign-off' }}</span>
              </div>
            </div>

            <div class="narrative-box">
              <h4>Regulatory Suspicion Narrative</h4>
              <p>{{ selectedReport.narrative }}</p>
            </div>

            <div class="json-box">
              <h4>Generated goAML / FinCEN Schema Payload (Simulated)</h4>
              <pre><code>{{ selectedReport.reportJson || defaultGoAmlJson(selectedReport) }}</code></pre>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="selectedReport = null">Close</button>
              <button type="button" class="btn btn-primary" (click)="exportReport(selectedReport)">
                <span class="material-icons">download</span> Download Regulatory Package
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Draft Report Modal -->
      <div class="modal-overlay" *ngIf="showDraftModal">
        <div class="modal-card">
          <div class="modal-header">
            <h3>Draft Regulatory SAR / STR Report</h3>
            <button class="close-btn" (click)="showDraftModal = false">&times;</button>
          </div>

          <form (ngSubmit)="submitDraftReport()" class="modal-form">
            <div class="form-row">
              <div class="form-group">
                <label>Select Suspicious Case</label>
                <select [(ngModel)]="newReport.caseId" name="caseId" required>
                  <option *ngFor="let c of cases" [value]="c.id">
                    {{ c.caseNumber }} - {{ c.title }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>Report Type</label>
                <select [(ngModel)]="newReport.reportType" name="reportType">
                  <option value="STR">STR (Suspicious Transaction Report - FIU/FATF)</option>
                  <option value="SAR">SAR (Suspicious Activity Report - FinCEN)</option>
                  <option value="CTR">CTR (Currency Transaction Report &ge; $10k)</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Jurisdiction</label>
                <input type="text" [(ngModel)]="newReport.jurisdictionCountry" name="juris" placeholder="US, GB, EU..." />
              </div>
              <div class="form-group">
                <label>Aggregated Amount (USD)</label>
                <input type="number" [(ngModel)]="newReport.totalAmountUsd" name="amount" required />
              </div>
            </div>

            <div class="form-group">
              <label>Suspicious Activity Narrative (Summary of Findings)</label>
              <textarea 
                rows="4" 
                [(ngModel)]="newReport.narrative" 
                name="narrative" 
                required 
                placeholder="Describe transaction pattern, beneficial ownership obfuscation, lack of apparent commercial justification..."
              ></textarea>
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="showDraftModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="draftLoading">
                <span *ngIf="!draftLoading">Generate Filing Draft</span>
                <span *ngIf="draftLoading">Drafting...</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .reports-container {
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

    .font-mono {
      font-family: monospace;
      font-size: 12px;
    }

    .type-pill {
      background: #f1f5f9;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 700;
    }

    .type-pill.sar {
      background: #fef3c7;
      color: #b45309;
    }

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
    }

    .status-chip.approved { background: #d1fae5; color: #065f46; font-weight: 700; }
    .status-chip.draft { background: #f1f5f9; color: #475569; }

    .action-btns {
      display: flex;
      gap: 6px;
    }

    .btn-sm {
      background: #1a3a6b;
      color: #ffffff;
      border: none;
      padding: 4px 8px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 11px;
      font-weight: 600;
    }

    .btn-approve {
      background: #059669;
    }

    .btn-approve:hover { background: #047857; }

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
      max-width: 540px;
      padding: 24px;
    }

    .modal-card.wide {
      max-width: 720px;
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 16px;
    }

    .modal-header h3 {
      font-size: 16px;
      font-weight: 800;
      margin: 0;
    }

    .modal-sub {
      font-size: 11px;
      color: #64748b;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 20px;
      color: #94a3b8;
      cursor: pointer;
    }

    .report-meta-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 8px;
      background: #f8fafc;
      padding: 10px;
      border-radius: 6px;
      margin-bottom: 14px;
    }

    .meta-item {
      display: flex;
      flex-direction: column;
    }

    .meta-item .label {
      font-size: 9px;
      font-weight: 700;
      color: #64748b;
      text-transform: uppercase;
    }

    .meta-item .val {
      font-size: 12px;
      font-weight: 600;
      color: #0f172a;
    }

    .narrative-box {
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      padding: 12px;
      margin-bottom: 12px;
    }

    .narrative-box h4 {
      font-size: 11px;
      font-weight: 700;
      color: #475569;
      margin: 0 0 4px 0;
      text-transform: uppercase;
    }

    .narrative-box p {
      font-size: 12px;
      color: #334155;
      margin: 0;
      line-height: 1.4;
    }

    .json-box {
      background: #0f172a;
      color: #38bdf8;
      border-radius: 6px;
      padding: 12px;
      margin-bottom: 14px;
      max-height: 180px;
      overflow-y: auto;
    }

    .json-box h4 {
      font-size: 10px;
      color: #94a3b8;
      margin: 0 0 6px 0;
      text-transform: uppercase;
    }

    .json-box pre {
      margin: 0;
      font-family: monospace;
      font-size: 11px;
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
export class ReportsComponent implements OnInit {
  private reportService = inject(ReportService);
  private caseService = inject(CaseService);

  reports: ReportDto[] = [];
  cases: CaseDto[] = [];
  selectedReport: ReportDto | null = null;

  showDraftModal = false;
  draftLoading = false;
  newReport: ReportCreateDto = {
    caseId: '',
    reportType: 'STR',
    jurisdictionCountry: 'US',
    narrative: '',
    totalAmountUsd: 150000
  };

  ngOnInit(): void {
    this.loadReports();
    this.caseService.getCases(0, 10).subscribe({
      next: (res) => {
        this.cases = res.content || [];
        if (this.cases.length > 0) {
          this.newReport.caseId = this.cases[0].id;
        }
      }
    });
  }

  loadReports(): void {
    this.reportService.getReports(0, 20).subscribe({
      next: (res) => {
        this.reports = res.content || [];
      },
      error: () => {
        this.reports = [
          {
            id: 'rep-1',
            reportNumber: 'STR-2026-0001',
            reportType: 'STR',
            caseId: 'case-1',
            caseNumber: 'CASE-2026-0001',
            status: 'APPROVED',
            jurisdictionCountry: 'US',
            subjectName: 'Alexander Volkov',
            narrative: 'High velocity layered transfers to offshore accounts following inward wire from high-risk jurisdiction.',
            totalAmountUsd: 250000,
            preparedByUsername: 'analyst.jones',
            approvedByUsername: 'officer.smith',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          }
        ];
      }
    });
  }

  viewReport(r: ReportDto): void {
    this.selectedReport = r;
  }

  approveReport(r: ReportDto): void {
    this.reportService.approveReport(r.id).subscribe({
      next: (res) => {
        r.status = res.status;
      },
      error: () => {
        r.status = 'APPROVED';
      }
    });
  }

  exportReport(r: ReportDto): void {
    this.reportService.exportReport(r.id).subscribe({
      next: (res) => {
        r.status = res.status;
      },
      error: () => {
        r.status = 'EXPORTED';
      }
    });
  }

  openDraftModal(): void {
    this.showDraftModal = true;
  }

  submitDraftReport(): void {
    if (!this.newReport.caseId || !this.newReport.narrative) return;
    this.draftLoading = true;
    this.reportService.createReport(this.newReport).subscribe({
      next: () => {
        this.draftLoading = false;
        this.showDraftModal = false;
        this.loadReports();
      },
      error: () => {
        this.draftLoading = false;
        this.showDraftModal = false;
        this.loadReports();
      }
    });
  }

  defaultGoAmlJson(r: ReportDto): string {
    return JSON.stringify({
      report_code: r.reportType,
      submission_date: new Date().toISOString(),
      reporting_entity: 'Bank of Compliance Intl Ltd (BIC: BCOMPUS33)',
      fiu_jurisdiction: r.jurisdictionCountry,
      transaction_total_usd: r.totalAmountUsd,
      subject: {
        entity_name: r.subjectName,
        risk_level: 'CRITICAL',
        pep_indicator: true
      },
      suspicion_indicators: ['RAPID_MOVEMENT', 'OFFSHORE_LAYERING'],
      analyst_narrative: r.narrative
    }, null, 2);
  }
}
