import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RuleService } from '../../core/services/rule.service';
import { RuleDto } from '../../core/models/models';

@Component({
  selector: 'app-rules',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rules-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>AML Detection Scenarios & Rules Engine</h1>
          <p class="subtitle">Configurable scenario builder, risk weights, detection thresholds, and parameter governance</p>
        </div>
        <button class="btn btn-secondary" (click)="loadRules()">
          <span class="material-icons">refresh</span> Refresh Rules
        </button>
      </div>

      <!-- Rules Table -->
      <div class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>Rule Code</th>
              <th>Scenario Name / Description</th>
              <th>Category</th>
              <th>Version</th>
              <th>Severity</th>
              <th>Risk Weight</th>
              <th>Parameters (Thresholds)</th>
              <th>Status</th>
              <th>Toggle</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let r of rules">
              <td class="font-mono"><strong>{{ r.ruleCode }}</strong></td>
              <td>
                <div class="rule-name-cell">
                  <strong>{{ r.name }}</strong>
                  <span class="rule-desc">{{ r.description }}</span>
                </div>
              </td>
              <td><span class="chip">{{ r.category }}</span></td>
              <td>v{{ r.version }}</td>
              <td>
                <span class="badge" [ngClass]="r.severity.toLowerCase()">
                  {{ r.severity }}
                </span>
              </td>
              <td><strong>{{ r.riskWeight }}</strong> / 100</td>
              <td>
                <code class="param-snippet">{{ r.parametersJson }}</code>
              </td>
              <td>
                <span class="status-chip" [class.active]="r.active">
                  {{ r.active ? 'ACTIVE' : 'DISABLED' }}
                </span>
              </td>
              <td>
                <button 
                  class="btn-toggle" 
                  [class.active]="r.active" 
                  (click)="toggleActive(r)"
                >
                  {{ r.active ? 'Disable' : 'Enable' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .rules-container {
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

    .rule-name-cell {
      display: flex;
      flex-direction: column;
      gap: 2px;
      max-width: 320px;
    }

    .rule-desc {
      font-size: 11px;
      color: #64748b;
    }

    .chip {
      background: #f1f5f9;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
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

    .param-snippet {
      font-family: monospace;
      font-size: 11px;
      background: #f8fafc;
      padding: 4px 6px;
      border-radius: 4px;
      border: 1px solid #e2e8f0;
      display: inline-block;
      max-width: 260px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
    }

    .status-chip.active {
      background: #d1fae5;
      color: #065f46;
    }

    .btn-toggle {
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
      cursor: pointer;
      border: 1px solid #cbd5e1;
      background: #ffffff;
      color: #475569;
    }

    .btn-toggle.active {
      background: #fef2f2;
      border-color: #fecaca;
      color: #dc2626;
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
  `]
})
export class RulesComponent implements OnInit {
  private ruleService = inject(RuleService);

  rules: RuleDto[] = [];

  ngOnInit(): void {
    this.loadRules();
  }

  loadRules(): void {
    this.ruleService.getRules().subscribe({
      next: (data) => {
        this.rules = data;
      },
      error: () => {
        this.rules = [
          {
            id: 'r-1',
            ruleCode: 'RAPID_MOVEMENT',
            name: 'Rapid Movement of Funds (Pass-Through Account)',
            description: 'Inward remittance followed by immediate large outward transfers within 24 hours',
            ruleType: 'TRANSACTION_MONITORING',
            category: 'VELOCITY',
            version: 1,
            parametersJson: '{"windowHours": 24, "outflowThresholdPct": 80.0, "minAmount": 10000}',
            riskWeight: 85,
            severity: 'CRITICAL',
            active: true,
            effectiveFrom: '2026-01-01'
          },
          {
            id: 'r-2',
            ruleCode: 'STRUCTURING_SMURFING',
            name: 'Structuring / Smurfing Below CTR Threshold',
            description: 'Multiple deposits or inward transfers just below the $10,000 regulatory reporting threshold',
            ruleType: 'TRANSACTION_MONITORING',
            category: 'STRUCTURING',
            version: 1,
            parametersJson: '{"minAmount": 8000, "maxAmount": 9999, "txCountThreshold": 3, "windowDays": 5}',
            riskWeight: 80,
            severity: 'HIGH',
            active: true,
            effectiveFrom: '2026-01-01'
          },
          {
            id: 'r-3',
            ruleCode: 'HIGH_RISK_JURISDICTION',
            name: 'High-Risk & Sanctioned Jurisdiction Proximity',
            description: 'Transfers to or from FATF grey/black listed countries or tax havens',
            ruleType: 'TRANSACTION_MONITORING',
            category: 'GEOGRAPHY',
            version: 1,
            parametersJson: '{"countryCodes": ["IR", "KP", "RU", "MM", "SY", "CY", "VG"]}',
            riskWeight: 90,
            severity: 'CRITICAL',
            active: true,
            effectiveFrom: '2026-01-01'
          },
          {
            id: 'r-4',
            ruleCode: 'PEP_LARGE_TRANSFER',
            name: 'Politically Exposed Person (PEP) Significant Flow',
            description: 'Individual identified as PEP conducting transactions exceeding enhanced diligence limit',
            ruleType: 'TRANSACTION_MONITORING',
            category: 'PEP',
            version: 1,
            parametersJson: '{"thresholdUsd": 50000}',
            riskWeight: 75,
            severity: 'HIGH',
            active: true,
            effectiveFrom: '2026-01-01'
          }
        ];
      }
    });
  }

  toggleActive(r: RuleDto): void {
    const newActive = !r.active;
    this.ruleService.updateRule(r.id, { active: newActive }).subscribe({
      next: (res) => {
        r.active = res.active;
      },
      error: () => {
        r.active = newActive;
      }
    });
  }
}
