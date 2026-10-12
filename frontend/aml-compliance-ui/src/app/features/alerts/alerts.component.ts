import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../core/services/alert.service';
import { AlertDto, AlertDispositionDto } from '../../core/models/models';

@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="alerts-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Alert Triage & Disposition Center</h1>
          <p class="subtitle">Adjudicate automated surveillance alerts, investigate hits, and escalate cases</p>
        </div>
        <button class="btn btn-secondary" (click)="loadAlerts()">
          <span class="material-icons">refresh</span> Refresh Queue
        </button>
      </div>

      <!-- Severity Filter Badges -->
      <div class="filter-strip">
        <button class="filter-pill" [class.active]="selectedSeverity === ''" (click)="setSeverity('')">
          All Severities
        </button>
        <button class="filter-pill critical" [class.active]="selectedSeverity === 'CRITICAL'" (click)="setSeverity('CRITICAL')">
          Critical Risk
        </button>
        <button class="filter-pill high" [class.active]="selectedSeverity === 'HIGH'" (click)="setSeverity('HIGH')">
          High Risk
        </button>
        <button class="filter-pill medium" [class.active]="selectedSeverity === 'MEDIUM'" (click)="setSeverity('MEDIUM')">
          Medium Risk
        </button>
        <button class="filter-pill low" [class.active]="selectedSeverity === 'LOW'" (click)="setSeverity('LOW')">
          Low Risk
        </button>
      </div>

      <!-- Alerts Data Table -->
      <div class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>Alert #</th>
              <th>Trigger Reason</th>
              <th>Subject Entity</th>
              <th>Severity</th>
              <th>Risk Score</th>
              <th>SLA Due</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let a of alerts" [class.overdue]="isOverdue(a.slaDueAt)">
              <td class="font-mono"><strong>{{ a.alertNumber }}</strong></td>
              <td>
                <div class="title-cell">
                  <strong>{{ a.title }}</strong>
                  <span class="rule-tag">{{ a.ruleCode || 'SURVEILLANCE_RULE' }}</span>
                </div>
              </td>
              <td>{{ a.customerName }}</td>
              <td>
                <span class="badge" [ngClass]="a.severity.toLowerCase()">
                  {{ a.severity }}
                </span>
              </td>
              <td><span class="score-pill">{{ a.score }}</span></td>
              <td>
                <div class="sla-cell">
                  <span>{{ a.slaDueAt | date:'short' }}</span>
                  <span *ngIf="isOverdue(a.slaDueAt)" class="sla-warn">OVERDUE</span>
                </div>
              </td>
              <td><span class="status-chip">{{ a.status }}</span></td>
              <td>
                <button class="btn-sm" (click)="openTriageModal(a)">
                  Triage / Adjudicate &rarr;
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="empty-state" *ngIf="alerts.length === 0">
          <span class="material-icons">task_alt</span>
          <p>No active alerts in this queue.</p>
        </div>
      </div>

      <!-- Triage & Disposition Modal Dialog -->
      <div class="modal-overlay" *ngIf="activeAlert">
        <div class="modal-card">
          <div class="modal-header">
            <div>
              <h3>Triage Alert: {{ activeAlert.alertNumber }}</h3>
              <span class="modal-sub">{{ activeAlert.title }}</span>
            </div>
            <button class="close-btn" (click)="activeAlert = null">&times;</button>
          </div>

          <div class="modal-body">
            <!-- Alert Context Card -->
            <div class="context-grid">
              <div class="context-item">
                <span class="label">Customer Entity</span>
                <span class="val">{{ activeAlert.customerName }}</span>
              </div>
              <div class="context-item">
                <span class="label">Severity / Score</span>
                <span class="val">{{ activeAlert.severity }} (Score: {{ activeAlert.score }})</span>
              </div>
              <div class="context-item">
                <span class="label">Trigger Event</span>
                <span class="val">{{ activeAlert.triggerEvent }}</span>
              </div>
              <div class="context-item">
                <span class="label">SLA Deadline</span>
                <span class="val">{{ activeAlert.slaDueAt | date:'medium' }}</span>
              </div>
            </div>

            <!-- Adjudication Form -->
            <form (ngSubmit)="submitDisposition()" class="disposition-form">
              <div class="form-group">
                <label>Compliance Determination (Disposition Action)</label>
                <select [(ngModel)]="dispositionData.disposition" name="disposition" required>
                  <option value="CONVERT_TO_CASE">CONVERT TO INVESTIGATION CASE (High Suspicion)</option>
                  <option value="ESCALATED">ESCALATE TO SENIOR MLRO</option>
                  <option value="CLOSED_FALSE_POSITIVE">CLOSE AS FALSE POSITIVE (Legitimate Business)</option>
                  <option value="CLOSED_TRUE_POSITIVE">CLOSE AS TRUE POSITIVE (Documented & Mitigated)</option>
                </select>
              </div>

              <div class="form-group">
                <label>Investigator Rationale & Due Diligence Notes (Mandatory)</label>
                <textarea 
                  rows="3" 
                  [(ngModel)]="dispositionData.notes" 
                  name="notes" 
                  placeholder="Detail documentary evidence examined, customer background checks, and rationale for this determination..." 
                  required
                ></textarea>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="activeAlert = null">Cancel</button>
                <button type="submit" class="btn btn-primary" [disabled]="loadingDisposition || !dispositionData.notes">
                  <span *ngIf="!loadingDisposition">Confirm Adjudication</span>
                  <span *ngIf="loadingDisposition">Saving...</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .alerts-container {
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

    .filter-strip {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }

    .filter-pill {
      background: #ffffff;
      border: 1px solid #cbd5e1;
      padding: 6px 14px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      color: #475569;
      cursor: pointer;
      transition: all 0.2s;
    }

    .filter-pill:hover {
      border-color: #1a3a6b;
      color: #1a3a6b;
    }

    .filter-pill.active {
      background: #1a3a6b;
      color: #ffffff;
      border-color: #1a3a6b;
    }

    .filter-pill.critical.active { background: #dc2626; border-color: #dc2626; }
    .filter-pill.high.active { background: #ea580c; border-color: #ea580c; }
    .filter-pill.medium.active { background: #f59e0b; border-color: #f59e0b; }
    .filter-pill.low.active { background: #16a34a; border-color: #16a34a; }

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

    .title-cell {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .rule-tag {
      font-size: 10px;
      color: #64748b;
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

    .score-pill {
      font-weight: 700;
    }

    .sla-cell {
      display: flex;
      flex-direction: column;
    }

    .sla-warn {
      font-size: 9px;
      color: #dc2626;
      font-weight: 800;
    }

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
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
      align-items: flex-start;
      margin-bottom: 16px;
    }

    .modal-header h3 {
      font-size: 16px;
      font-weight: 800;
      margin: 0;
    }

    .modal-sub {
      font-size: 12px;
      color: #64748b;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 20px;
      color: #94a3b8;
      cursor: pointer;
    }

    .context-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
      background: #f8fafc;
      padding: 12px;
      border-radius: 6px;
      margin-bottom: 16px;
    }

    .context-item {
      display: flex;
      flex-direction: column;
    }

    .context-item .label {
      font-size: 10px;
      font-weight: 700;
      color: #64748b;
      text-transform: uppercase;
    }

    .context-item .val {
      font-size: 12px;
      font-weight: 600;
      color: #1e293b;
    }

    .disposition-form {
      display: flex;
      flex-direction: column;
      gap: 14px;
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

    .form-group select, .form-group textarea {
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 8px 10px;
      font-size: 13px;
      font-family: inherit;
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
export class AlertsComponent implements OnInit {
  private alertService = inject(AlertService);

  alerts: AlertDto[] = [];
  selectedSeverity = '';
  activeAlert: AlertDto | null = null;
  loadingDisposition = false;

  dispositionData: AlertDispositionDto = {
    disposition: 'CONVERT_TO_CASE',
    notes: ''
  };

  ngOnInit(): void {
    this.loadAlerts();
  }

  loadAlerts(): void {
    this.alertService.getAlerts(0, 20, undefined, this.selectedSeverity || undefined).subscribe({
      next: (res) => {
        this.alerts = res.content || [];
      },
      error: () => {
        // Fallback demo alerts
        this.alerts = [
          {
            id: 'alt-1',
            alertNumber: 'ALT-2026-0001',
            title: 'Multiple Structuring Transactions Under Reporting Threshold',
            customerId: 'c-1',
            customerName: 'Marcus Vance',
            triggerEvent: 'TRANSACTION_MONITORING',
            severity: 'HIGH',
            score: 85,
            status: 'NEW',
            slaDueAt: new Date(Date.now() + 18000000).toISOString(),
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          },
          {
            id: 'alt-2',
            alertNumber: 'ALT-2026-0002',
            title: 'Rapid Outflow After High-Value Inward Remittance',
            customerId: 'c-2',
            customerName: 'Global Maritime Trading Ltd',
            triggerEvent: 'TRANSACTION_MONITORING',
            severity: 'CRITICAL',
            score: 95,
            status: 'UNDER_REVIEW',
            slaDueAt: new Date(Date.now() + 3600000).toISOString(),
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          }
        ];
      }
    });
  }

  setSeverity(sev: string): void {
    this.selectedSeverity = sev;
    this.loadAlerts();
  }

  isOverdue(dueAt: string): boolean {
    return new Date(dueAt).getTime() < Date.now();
  }

  openTriageModal(a: AlertDto): void {
    this.activeAlert = a;
    this.dispositionData = {
      disposition: 'CONVERT_TO_CASE',
      notes: ''
    };
  }

  submitDisposition(): void {
    if (!this.activeAlert) return;
    this.loadingDisposition = true;
    this.alertService.disposeAlert(this.activeAlert.id, this.dispositionData).subscribe({
      next: () => {
        this.loadingDisposition = false;
        this.activeAlert = null;
        this.loadAlerts();
      },
      error: () => {
        this.loadingDisposition = false;
        this.activeAlert = null;
        this.loadAlerts();
      }
    });
  }
}
