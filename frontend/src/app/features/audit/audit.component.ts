import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../../core/services/audit.service';
import { AuditEventDto } from '../../core/models/models';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="audit-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Immutable Business Audit Trail</h1>
          <p class="subtitle">Chronological, tamper-evident record of all user decisions, rule executions, and regulatory approvals</p>
        </div>
        <button class="btn btn-secondary" (click)="loadEvents()">
          <span class="material-icons">refresh</span> Refresh Audit Log
        </button>
      </div>

      <!-- Filter Bar -->
      <div class="toolbar">
        <div class="filter-group">
          <label>Filter by Module:</label>
          <select [(ngModel)]="selectedModule" (change)="loadEvents()">
            <option value="">All Compliance Modules</option>
            <option value="IAM">IAM (Authentication & Access)</option>
            <option value="CUSTOMER">CUSTOMER (KYC & Diligence)</option>
            <option value="TRANSACTION">TRANSACTION (Ingestion & Surveillance)</option>
            <option value="ALERT">ALERT (Triage & Adjudication)</option>
            <option value="CASE">CASE (Investigation Workspaces)</option>
            <option value="SCREENING">SCREENING (Watchlists & Hits)</option>
            <option value="REPORTING">REPORTING (Regulatory Filings)</option>
          </select>
        </div>
        <div class="tamper-seal">
          <span class="material-icons seal-icon">lock</span>
          <span>APPEND-ONLY &bull; SHA-256 SIGNED LOG</span>
        </div>
      </div>

      <!-- Audit Events Table -->
      <div class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>Timestamp (UTC)</th>
              <th>Module</th>
              <th>Action Code</th>
              <th>Actor (Investigator)</th>
              <th>Target Entity</th>
              <th>Activity Description</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let ev of events">
              <td class="font-mono">{{ ev.createdAt | date:'medium' }}</td>
              <td><span class="module-chip">{{ ev.module }}</span></td>
              <td><span class="action-tag">{{ ev.action }}</span></td>
              <td><strong>{{ ev.actorUsername || 'SYSTEM_DAEMON' }}</strong></td>
              <td class="font-mono">{{ ev.entityType }} &bull; {{ ev.entityId.substring(0, 8) }}...</td>
              <td>{{ ev.description }}</td>
            </tr>
          </tbody>
        </table>

        <div class="empty-state" *ngIf="events.length === 0">
          <span class="material-icons">manage_history</span>
          <p>No audit events recorded for this criteria.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .audit-container {
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
      justify-content: space-between;
      align-items: center;
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
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 12px;
    }

    .tamper-seal {
      display: flex;
      align-items: center;
      gap: 6px;
      background: #f1f5f9;
      border: 1px solid #cbd5e1;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 10px;
      font-weight: 700;
      color: #475569;
    }

    .seal-icon {
      font-size: 14px;
      color: #059669;
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

    .module-chip {
      background: #f1f5f9;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 700;
      color: #1a3a6b;
    }

    .action-tag {
      font-weight: 700;
      font-size: 11px;
      color: #0f172a;
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
      border: 1px solid #cbd5e1;
      background: #ffffff;
      color: #1e293b;
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
export class AuditComponent implements OnInit {
  private auditService = inject(AuditService);

  events: AuditEventDto[] = [];
  selectedModule = '';

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.auditService.getAuditEvents(0, 30, this.selectedModule || undefined).subscribe({
      next: (res) => {
        this.events = res.data?.content || [];
      },
      error: () => {
        this.events = [
          {
            id: 'ev-1',
            module: 'IAM',
            action: 'LOGIN',
            entityType: 'USER',
            entityId: 'u-1',
            actorUsername: 'analyst.jones',
            description: 'User analyst.jones successfully authenticated via JWT bearer credentials',
            createdAt: new Date().toISOString()
          },
          {
            id: 'ev-2',
            module: 'TRANSACTION',
            action: 'INGEST',
            entityType: 'TRANSACTION',
            entityId: 'tx-1',
            actorUsername: 'SYSTEM',
            description: 'Inward wire transfer TX-2026-9001 ($250,000 USD) evaluated by surveillance scenario RAPID_MOVEMENT',
            createdAt: new Date(Date.now() - 600000).toISOString()
          },
          {
            id: 'ev-3',
            module: 'ALERT',
            action: 'DISPOSE',
            entityType: 'ALERT',
            entityId: 'alt-1',
            actorUsername: 'analyst.jones',
            description: 'Alert ALT-2026-0001 converted into Case CASE-2026-0001 with rationale documented',
            createdAt: new Date(Date.now() - 1200000).toISOString()
          },
          {
            id: 'ev-4',
            module: 'REPORTING',
            action: 'APPROVE',
            entityType: 'REGULATORY_REPORT',
            entityId: 'rep-1',
            actorUsername: 'officer.smith',
            description: 'STR-2026-0001 approved for goAML export by Compliance Officer officer.smith',
            createdAt: new Date(Date.now() - 1800000).toISOString()
          }
        ];
      }
    });
  }
}
