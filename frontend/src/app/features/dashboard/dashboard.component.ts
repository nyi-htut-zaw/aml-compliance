import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../core/services/dashboard.service';
import { AlertService } from '../../core/services/alert.service';
import { AuthService } from '../../core/services/auth.service';
import { DashboardKpis, AlertDto } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-container">
      <!-- Page Header -->
      <div class="page-header">
        <div class="header-titles">
          <h1>Compliance Surveillance Command Center</h1>
          <p class="subtitle">
            Jurisdiction: <strong>{{ authService.jurisdiction() }}</strong> &bull; 
            Live Monitoring &bull; Simulated Real-time Surveillance Stream
          </p>
        </div>
        <div class="header-actions">
          <button class="btn btn-secondary" (click)="loadData()">
            <span class="material-icons">refresh</span> Refresh
          </button>
          <a routerLink="/transactions" class="btn btn-primary">
            <span class="material-icons">add_card</span> Simulate Transaction
          </a>
        </div>
      </div>

      <!-- KPI Summary Cards -->
      <div class="kpi-grid" *ngIf="kpis">
        <div class="kpi-card danger">
          <div class="kpi-icon">
            <span class="material-icons">notifications_active</span>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">Active Alerts</span>
            <span class="kpi-value">{{ kpis.alertMetrics.totalNew + kpis.alertMetrics.totalUnderReview }}</span>
            <span class="kpi-subtext">Overdue SLA: <strong>{{ kpis.alertMetrics.overdueSla }}</strong></span>
          </div>
        </div>

        <div class="kpi-card warning">
          <div class="kpi-icon">
            <span class="material-icons">folder_open</span>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">Active Cases</span>
            <span class="kpi-value">{{ kpis.caseMetrics.totalActive }}</span>
            <span class="kpi-subtext">MLRO Escalated: <strong>{{ kpis.caseMetrics.escalatedToMlro }}</strong></span>
          </div>
        </div>

        <div class="kpi-card critical">
          <div class="kpi-icon">
            <span class="material-icons">person_off</span>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">High/Critical Risk Entities</span>
            <span class="kpi-value">{{ kpis.customerMetrics.highRisk + kpis.customerMetrics.criticalRisk }}</span>
            <span class="kpi-subtext">Total Entities: <strong>{{ kpis.customerMetrics.totalCustomers }}</strong></span>
          </div>
        </div>

        <div class="kpi-card info">
          <div class="kpi-icon">
            <span class="material-icons">currency_exchange</span>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">Monitored Volume</span>
            <span class="kpi-value">{{ kpis.transactionMetrics.totalTransactions }}</span>
            <span class="kpi-subtext">Ingested & Screened</span>
          </div>
        </div>

        <div class="kpi-card success">
          <div class="kpi-icon">
            <span class="material-icons">rule_folder</span>
          </div>
          <div class="kpi-content">
            <span class="kpi-label">Regulatory Reports</span>
            <span class="kpi-value">{{ kpis.reportMetrics.draftReports }} Drafts</span>
            <span class="kpi-subtext">Approved: <strong>{{ kpis.reportMetrics.approvedReports }}</strong></span>
          </div>
        </div>
      </div>

      <!-- Main Visual Distribution Grid -->
      <div class="metrics-split" *ngIf="kpis">
        <!-- Severity Distribution Card -->
        <div class="panel">
          <div class="panel-header">
            <h2>Alert Severity Distribution</h2>
            <span class="badge">By Threat Level</span>
          </div>
          <div class="panel-body">
            <div class="severity-bars">
              <div class="bar-row">
                <span class="bar-label"><span class="dot critical"></span> CRITICAL</span>
                <div class="progress-track">
                  <div class="progress-fill critical" [style.width.%]="calcPct(kpis.alertMetrics.critical)"></div>
                </div>
                <span class="bar-count">{{ kpis.alertMetrics.critical }}</span>
              </div>

              <div class="bar-row">
                <span class="bar-label"><span class="dot high"></span> HIGH</span>
                <div class="progress-track">
                  <div class="progress-fill high" [style.width.%]="calcPct(kpis.alertMetrics.high)"></div>
                </div>
                <span class="bar-count">{{ kpis.alertMetrics.high }}</span>
              </div>

              <div class="bar-row">
                <span class="bar-label"><span class="dot medium"></span> MEDIUM</span>
                <div class="progress-track">
                  <div class="progress-fill medium" [style.width.%]="calcPct(kpis.alertMetrics.medium)"></div>
                </div>
                <span class="bar-count">{{ kpis.alertMetrics.medium }}</span>
              </div>

              <div class="bar-row">
                <span class="bar-label"><span class="dot low"></span> LOW</span>
                <div class="progress-track">
                  <div class="progress-fill low" [style.width.%]="calcPct(kpis.alertMetrics.low)"></div>
                </div>
                <span class="bar-count">{{ kpis.alertMetrics.low }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Case Pipeline Workflow Card -->
        <div class="panel">
          <div class="panel-header">
            <h2>Investigation Lifecycle Pipeline</h2>
            <span class="badge">Cases</span>
          </div>
          <div class="panel-body">
            <div class="pipeline-grid">
              <div class="pipeline-step">
                <span class="step-num">{{ kpis.caseMetrics.totalActive }}</span>
                <span class="step-label">Under Investigation</span>
                <span class="step-sub">L1/L2 Analyst Review</span>
              </div>
              <div class="step-arrow"><span class="material-icons">arrow_forward</span></div>
              <div class="pipeline-step highlight">
                <span class="step-num">{{ kpis.caseMetrics.escalatedToMlro }}</span>
                <span class="step-label">Escalated to MLRO</span>
                <span class="step-sub">Dual Approval Gate</span>
              </div>
              <div class="step-arrow"><span class="material-icons">arrow_forward</span></div>
              <div class="pipeline-step">
                <span class="step-num">{{ kpis.caseMetrics.closedSuspicious }}</span>
                <span class="step-label">Closed Suspicious</span>
                <span class="step-sub">Pending STR Draft</span>
              </div>
              <div class="step-arrow"><span class="material-icons">arrow_forward</span></div>
              <div class="pipeline-step">
                <span class="step-num">{{ kpis.caseMetrics.filedStr }}</span>
                <span class="step-label">SAR/STR Prepared</span>
                <span class="step-sub">FIU goAML Ready</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Action Shortcuts -->
      <div class="actions-strip">
        <h3>Fast Action Shortcuts</h3>
        <div class="shortcut-buttons">
          <a routerLink="/customers" class="shortcut-btn">
            <span class="material-icons">person_add</span>
            <span>Onboard Customer</span>
          </a>
          <a routerLink="/transactions" class="shortcut-btn">
            <span class="material-icons">payments</span>
            <span>Simulate Transaction</span>
          </a>
          <a routerLink="/screening" class="shortcut-btn">
            <span class="material-icons">manage_search</span>
            <span>Fuzzy Name Screening</span>
          </a>
          <a routerLink="/alerts" class="shortcut-btn">
            <span class="material-icons">priority_high</span>
            <span>Triage Urgent Alerts</span>
          </a>
          <a routerLink="/reports" class="shortcut-btn">
            <span class="material-icons">post_add</span>
            <span>Draft Regulatory SAR</span>
          </a>
        </div>
      </div>

      <!-- Active Alerts Preview Table -->
      <div class="panel">
        <div class="panel-header">
          <h2>High Priority Alerts Awaiting Triage</h2>
          <a routerLink="/alerts" class="view-all-link">View All Alerts &rarr;</a>
        </div>
        <div class="panel-body no-padding">
          <table class="data-table" *ngIf="recentAlerts.length > 0">
            <thead>
              <tr>
                <th>Alert #</th>
                <th>Subject Customer</th>
                <th>Detection Rule</th>
                <th>Severity</th>
                <th>Score</th>
                <th>Status</th>
                <th>Created</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let alert of recentAlerts">
                <td class="font-mono"><strong>{{ alert.alertNumber }}</strong></td>
                <td>{{ alert.customerName }}</td>
                <td><span class="rule-chip">{{ alert.ruleCode || 'SCENARIO_TRIGGER' }}</span></td>
                <td>
                  <span class="badge" [ngClass]="alert.severity.toLowerCase()">
                    {{ alert.severity }}
                  </span>
                </td>
                <td><span class="score-pill">{{ alert.score }}</span></td>
                <td><span class="status-chip">{{ alert.status }}</span></td>
                <td>{{ alert.createdAt | date:'short' }}</td>
                <td>
                  <a [routerLink]="['/alerts']" class="btn-sm">Triage</a>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="empty-state" *ngIf="recentAlerts.length === 0">
            <span class="material-icons">task_alt</span>
            <p>No active alerts requiring immediate triage.</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      display: flex;
      flex-direction: column;
      gap: 24px;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .header-titles h1 {
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

    .header-actions {
      display: flex;
      gap: 12px;
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
      text-decoration: none;
      transition: all 0.2s;
      border: 1px solid transparent;
    }

    .btn .material-icons {
      font-size: 18px;
    }

    .btn-primary {
      background: #1a3a6b;
      color: #ffffff;
    }

    .btn-primary:hover {
      background: #2a5298;
    }

    .btn-secondary {
      background: #ffffff;
      color: #1e293b;
      border-color: #cbd5e1;
    }

    .btn-secondary:hover {
      background: #f1f5f9;
    }

    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
    }

    .kpi-card {
      background: #ffffff;
      border-radius: 8px;
      padding: 16px;
      border-left: 4px solid #1a3a6b;
      box-shadow: 0 1px 3px rgba(0,0,0,0.06);
      display: flex;
      gap: 14px;
      align-items: center;
    }

    .kpi-card.danger { border-left-color: #ef4444; }
    .kpi-card.warning { border-left-color: #f59e0b; }
    .kpi-card.critical { border-left-color: #dc2626; }
    .kpi-card.info { border-left-color: #3b82f6; }
    .kpi-card.success { border-left-color: #10b981; }

    .kpi-icon {
      width: 44px;
      height: 44px;
      border-radius: 8px;
      background: #f1f5f9;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .kpi-card.danger .kpi-icon { background: #fee2e2; color: #ef4444; }
    .kpi-card.warning .kpi-icon { background: #fef3c7; color: #f59e0b; }
    .kpi-card.critical .kpi-icon { background: #fee2e2; color: #dc2626; }
    .kpi-card.info .kpi-icon { background: #dbeafe; color: #3b82f6; }
    .kpi-card.success .kpi-icon { background: #d1fae5; color: #10b981; }

    .kpi-content {
      display: flex;
      flex-direction: column;
    }

    .kpi-label {
      font-size: 11px;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .kpi-value {
      font-size: 22px;
      font-weight: 800;
      color: #0f172a;
      line-height: 1.2;
    }

    .kpi-subtext {
      font-size: 11px;
      color: #64748b;
      margin-top: 2px;
    }

    .metrics-split {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
    }

    .panel {
      background: #ffffff;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
      box-shadow: 0 1px 3px rgba(0,0,0,0.04);
    }

    .panel-header {
      padding: 16px 20px;
      border-bottom: 1px solid #f1f5f9;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .panel-header h2 {
      font-size: 15px;
      font-weight: 700;
      color: #0f172a;
      margin: 0;
    }

    .panel-body {
      padding: 20px;
    }

    .panel-body.no-padding {
      padding: 0;
    }

    .severity-bars {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }

    .bar-row {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .bar-label {
      width: 100px;
      font-size: 12px;
      font-weight: 600;
      color: #334155;
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
    }

    .dot.critical, .progress-fill.critical { background: #dc2626; }
    .dot.high, .progress-fill.high { background: #ea580c; }
    .dot.medium, .progress-fill.medium { background: #f59e0b; }
    .dot.low, .progress-fill.low { background: #16a34a; }

    .progress-track {
      flex: 1;
      height: 10px;
      background: #f1f5f9;
      border-radius: 5px;
      overflow: hidden;
    }

    .progress-fill {
      height: 100%;
      border-radius: 5px;
      transition: width 0.4s ease;
    }

    .bar-count {
      width: 40px;
      text-align: right;
      font-size: 13px;
      font-weight: 700;
      color: #0f172a;
    }

    .pipeline-grid {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 8px;
    }

    .pipeline-step {
      flex: 1;
      background: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 12px;
      text-align: center;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .pipeline-step.highlight {
      background: #fffbeb;
      border-color: #fde68a;
    }

    .step-num {
      font-size: 20px;
      font-weight: 800;
      color: #1a3a6b;
    }

    .step-label {
      font-size: 11px;
      font-weight: 700;
      color: #1e293b;
    }

    .step-sub {
      font-size: 9px;
      color: #64748b;
    }

    .step-arrow {
      color: #94a3b8;
    }

    .actions-strip {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 16px 20px;
    }

    .actions-strip h3 {
      font-size: 13px;
      font-weight: 700;
      color: #475569;
      margin: 0 0 12px 0;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .shortcut-buttons {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }

    .shortcut-btn {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 8px 14px;
      background: #f8fafc;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      text-decoration: none;
      color: #1e293b;
      font-size: 12px;
      font-weight: 600;
      transition: all 0.2s;
    }

    .shortcut-btn:hover {
      background: #1a3a6b;
      color: #ffffff;
      border-color: #1a3a6b;
    }

    .shortcut-btn:hover .material-icons {
      color: #c9a94d;
    }

    .shortcut-btn .material-icons {
      font-size: 18px;
      color: #1a3a6b;
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
      letter-spacing: 0.5px;
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

    .rule-chip {
      background: #f1f5f9;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
      color: #334155;
    }

    .badge {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 0.5px;
    }

    .badge.critical { background: #fee2e2; color: #b91c1c; }
    .badge.high { background: #ffedd5; color: #c2410c; }
    .badge.medium { background: #fef3c7; color: #b45309; }
    .badge.low { background: #dcfce7; color: #15803d; }

    .score-pill {
      font-weight: 700;
      color: #0f172a;
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
      padding: 4px 10px;
      border-radius: 4px;
      text-decoration: none;
      font-size: 11px;
      font-weight: 600;
    }

    .btn-sm:hover {
      background: #2a5298;
    }

    .view-all-link {
      font-size: 12px;
      font-weight: 600;
      color: #1a3a6b;
      text-decoration: none;
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
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  private alertService = inject(AlertService);
  authService = inject(AuthService);

  kpis: DashboardKpis | null = null;
  recentAlerts: AlertDto[] = [];

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.dashboardService.getKpis().subscribe({
      next: (data) => {
        this.kpis = data;
      },
      error: () => {
        // Fallback demo metrics if backend is cold
        this.kpis = {
          alertMetrics: {
            totalNew: 14,
            totalAssigned: 8,
            totalUnderReview: 6,
            totalEscalated: 3,
            overdueSla: 2,
            critical: 4,
            high: 7,
            medium: 9,
            low: 2
          },
          caseMetrics: {
            totalActive: 7,
            escalatedToMlro: 3,
            closedSuspicious: 4,
            filedStr: 2,
            slaBreached: 1
          },
          customerMetrics: {
            totalCustomers: 250,
            highRisk: 18,
            criticalRisk: 5,
            kycExpired: 3
          },
          transactionMetrics: {
            totalTransactions: 1420
          },
          reportMetrics: {
            draftReports: 2,
            approvedReports: 3,
            exportedReports: 1
          },
          generatedAt: new Date().toISOString()
        };
      }
    });

    this.alertService.getAlerts(0, 5).subscribe({
      next: (res) => {
        this.recentAlerts = res.content || [];
      },
      error: () => {
        this.recentAlerts = [
          {
            id: 'demo-1',
            alertNumber: 'ALT-2026-0041',
            title: 'Rapid Outflow After High-Value Inflow',
            customerId: 'c1',
            customerName: 'Global Maritime Trading Ltd',
            triggerEvent: 'TRANSACTION_MONITORING',
            severity: 'CRITICAL',
            score: 95,
            status: 'NEW',
            slaDueAt: new Date(Date.now() + 3600000).toISOString(),
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          },
          {
            id: 'demo-2',
            alertNumber: 'ALT-2026-0039',
            title: 'Multiple Structuring Transactions Under Threshold',
            customerId: 'c2',
            customerName: 'Marcus Vance',
            triggerEvent: 'TRANSACTION_MONITORING',
            severity: 'HIGH',
            score: 82,
            status: 'UNDER_REVIEW',
            slaDueAt: new Date(Date.now() + 7200000).toISOString(),
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          }
        ];
      }
    });
  }

  calcPct(count: number): number {
    if (!this.kpis) return 0;
    const total = (this.kpis.alertMetrics.critical + this.kpis.alertMetrics.high + this.kpis.alertMetrics.medium + this.kpis.alertMetrics.low) || 1;
    return Math.round((count / total) * 100);
  }
}
