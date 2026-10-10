import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService, TransactionIngestDto } from '../../core/services/transaction.service';
import { CustomerService } from '../../core/services/customer.service';
import { TransactionDto, CustomerDto } from '../../core/models/models';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="trans-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Transaction Surveillance & Ingestion</h1>
          <p class="subtitle">Real-time ledger surveillance, velocity checks, and scenario simulation</p>
        </div>
        <button class="btn btn-primary" (click)="openSimModal()">
          <span class="material-icons">add_card</span> Simulate Transaction
        </button>
      </div>

      <!-- Ingestion Results Banner (shows rule evaluation results) -->
      <div class="result-banner" *ngIf="lastIngestionFeedback">
        <div class="banner-icon" [class.alert]="lastIngestionFeedback.alertsGenerated > 0">
          <span class="material-icons">{{ lastIngestionFeedback.alertsGenerated > 0 ? 'warning' : 'check_circle' }}</span>
        </div>
        <div class="banner-text">
          <strong>Transaction Ingested: {{ lastIngestionFeedback.transaction.referenceNumber }}</strong>
          <span *ngIf="lastIngestionFeedback.alertsGenerated > 0" class="alert-msg">
            SCENARIO HIT! Generated {{ lastIngestionFeedback.alertsGenerated }} compliance alert(s) for immediate triage.
          </span>
          <span *ngIf="lastIngestionFeedback.alertsGenerated === 0" class="clean-msg">
            Clear: No surveillance rules violated for this event.
          </span>
        </div>
        <button class="close-banner" (click)="lastIngestionFeedback = null">&times;</button>
      </div>

      <!-- Filter Bar -->
      <div class="toolbar">
        <div class="filter-group">
          <label>Min Amount ($):</label>
          <input type="number" [(ngModel)]="minAmount" (change)="loadTransactions()" placeholder="e.g. 5000" />
        </div>
        <button class="btn btn-secondary" (click)="loadTransactions()">
          <span class="material-icons">refresh</span> Refresh Ledger
        </button>
      </div>

      <!-- Transaction Table -->
      <div class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>Reference #</th>
              <th>Subject Entity</th>
              <th>Type / Channel</th>
              <th>Amount</th>
              <th>USD Equiv</th>
              <th>Counterparty</th>
              <th>Dest Country</th>
              <th>Booking Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let tx of transactions">
              <td class="font-mono"><strong>{{ tx.referenceNumber }}</strong></td>
              <td>{{ tx.customerName }}</td>
              <td>
                <div class="type-cell">
                  <span class="chip">{{ tx.transactionType }}</span>
                  <span class="channel-text">{{ tx.channel }}</span>
                </div>
              </td>
              <td>
                <span class="amount-val">{{ tx.amount | number:'1.2-2' }} {{ tx.currency }}</span>
              </td>
              <td>
                <span class="usd-val" [class.high-val]="tx.baseAmountUsd >= 10000">
                  \${{ tx.baseAmountUsd | number:'1.2-2' }}
                </span>
              </td>
              <td>{{ tx.counterpartyName || 'Self' }}</td>
              <td>
                <span class="flag-chip" [class.high-risk]="isHighRiskCountry(tx.counterpartyCountry)">
                  {{ tx.counterpartyCountry }}
                </span>
              </td>
              <td>{{ tx.bookingDate | date:'mediumDate' }}</td>
              <td>
                <span class="status-chip">{{ tx.status }}</span>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="empty-state" *ngIf="transactions.length === 0">
          <span class="material-icons">receipt_long</span>
          <p>No transactions found in active ledger.</p>
        </div>
      </div>

      <!-- Simulation Modal Dialog -->
      <div class="modal-overlay" *ngIf="showSimModal">
        <div class="modal-card">
          <div class="modal-header">
            <h3>Simulate / Ingest Surveillance Transaction</h3>
            <button class="close-btn" (click)="showSimModal = false">
              <span class="material-icons">close</span>
            </button>
          </div>

          <form (ngSubmit)="submitTransaction()" class="modal-form">
            <div class="form-row">
              <div class="form-group">
                <label>Target Account / Customer</label>
                <select [(ngModel)]="newTx.accountId" name="accountId" required>
                  <option *ngFor="let c of customers" [value]="c.id">
                    {{ c.fullName }} ({{ c.customerNumber }})
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>Transaction Type</label>
                <select [(ngModel)]="newTx.transactionType" name="txType">
                  <option value="WIRE_OUT">WIRE_OUT (Outbound Wire)</option>
                  <option value="WIRE_IN">WIRE_IN (Inbound Wire)</option>
                  <option value="CASH_DEPOSIT">CASH_DEPOSIT (Branch OTC)</option>
                  <option value="CROSS_BORDER_TRANSFER">CROSS_BORDER_TRANSFER</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Amount</label>
                <input type="number" [(ngModel)]="newTx.amount" name="amount" required step="0.01" />
              </div>
              <div class="form-group">
                <label>Currency</label>
                <select [(ngModel)]="newTx.currency" name="currency">
                  <option value="USD">USD (US Dollar)</option>
                  <option value="EUR">EUR (Euro)</option>
                  <option value="GBP">GBP (British Pound)</option>
                  <option value="SGD">SGD (Singapore Dollar)</option>
                </select>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Counterparty Name</label>
                <input type="text" [(ngModel)]="newTx.counterpartyName" name="cpName" placeholder="e.g. PetroGlobal Trading FZE" />
              </div>
              <div class="form-group">
                <label>Counterparty Country (ISO-2)</label>
                <input type="text" [(ngModel)]="newTx.counterpartyCountry" name="cpCountry" required placeholder="US, CY, RU, IR..." />
              </div>
            </div>

            <div class="form-group">
              <label>Narration / Purpose Code</label>
              <input type="text" [(ngModel)]="newTx.narration" name="narration" placeholder="Commercial freight settlement" />
            </div>

            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" (click)="showSimModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary" [disabled]="simLoading">
                <span *ngIf="!simLoading">Ingest & Evaluate Rules</span>
                <span *ngIf="simLoading">Processing Surveillance...</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .trans-container {
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

    .result-banner {
      display: flex;
      align-items: center;
      gap: 12px;
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      border-radius: 8px;
      padding: 12px 16px;
    }

    .banner-icon {
      color: #10b981;
    }

    .banner-icon.alert {
      color: #ef4444;
    }

    .banner-text {
      flex: 1;
      display: flex;
      flex-direction: column;
      font-size: 13px;
    }

    .alert-msg {
      color: #b91c1c;
      font-weight: 700;
      font-size: 12px;
    }

    .clean-msg {
      color: #047857;
      font-size: 12px;
    }

    .close-banner {
      background: none;
      border: none;
      font-size: 18px;
      cursor: pointer;
      color: #64748b;
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

    .filter-group input {
      border: 1px solid #cbd5e1;
      padding: 6px 10px;
      border-radius: 6px;
      font-size: 13px;
      width: 120px;
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

    .type-cell {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .chip {
      background: #f1f5f9;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 700;
      display: inline-block;
      width: fit-content;
    }

    .channel-text {
      font-size: 10px;
      color: #64748b;
    }

    .amount-val {
      font-weight: 600;
      color: #0f172a;
    }

    .usd-val {
      font-size: 12px;
      color: #64748b;
    }

    .usd-val.high-val {
      color: #dc2626;
      font-weight: 700;
    }

    .flag-chip {
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
      background: #f1f5f9;
    }

    .flag-chip.high-risk {
      background: #fee2e2;
      color: #dc2626;
    }

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
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
      max-width: 520px;
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

    .close-btn {
      background: none;
      border: none;
      color: #94a3b8;
      cursor: pointer;
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
export class TransactionsComponent implements OnInit {
  private transactionService = inject(TransactionService);
  private customerService = inject(CustomerService);

  transactions: TransactionDto[] = [];
  customers: CustomerDto[] = [];
  minAmount?: number;

  showSimModal = false;
  simLoading = false;
  lastIngestionFeedback: any = null;

  newTx: TransactionIngestDto = {
    accountId: '',
    transactionType: 'WIRE_OUT',
    channel: 'SWIFT',
    amount: 15000,
    currency: 'USD',
    counterpartyName: 'Trans-Ocean Shipping LLC',
    counterpartyCountry: 'CY',
    narration: 'Vessel bunkering payment'
  };

  ngOnInit(): void {
    this.loadTransactions();
    this.customerService.getCustomers(0, 10).subscribe({
      next: (res) => {
        this.customers = res.data?.content || [];
        if (this.customers.length > 0) {
          this.newTx.accountId = this.customers[0].id;
        }
      }
    });
  }

  loadTransactions(): void {
    this.transactionService.getTransactions(0, 20, undefined, this.minAmount).subscribe({
      next: (res) => {
        this.transactions = res.data?.content || [];
      },
      error: () => {
        // Fallback sample data if cold
        this.transactions = [
          {
            id: 'tx-1',
            referenceNumber: 'TX-2026-9001',
            accountId: 'acc-1',
            accountNumber: 'ACC-100234',
            customerId: 'c-1',
            customerName: 'Alexander Volkov',
            transactionType: 'WIRE_OUT',
            channel: 'SWIFT',
            amount: 250000,
            currency: 'USD',
            baseAmountUsd: 250000,
            counterpartyName: 'Volkov Energy FZE',
            counterpartyCountry: 'AE',
            bookingDate: '2026-03-01',
            status: 'POSTED'
          },
          {
            id: 'tx-2',
            referenceNumber: 'TX-2026-9002',
            accountId: 'acc-2',
            accountNumber: 'ACC-100877',
            customerId: 'c-2',
            customerName: 'Meridian Global Trading LLC',
            transactionType: 'WIRE_IN',
            channel: 'CHIPS',
            amount: 9800,
            currency: 'USD',
            baseAmountUsd: 9800,
            counterpartyName: 'Apex Trade Hub',
            counterpartyCountry: 'CY',
            bookingDate: '2026-03-02',
            status: 'POSTED'
          }
        ];
      }
    });
  }

  openSimModal(): void {
    this.showSimModal = true;
  }

  submitTransaction(): void {
    this.simLoading = true;
    this.transactionService.ingestTransaction(this.newTx).subscribe({
      next: (res) => {
        this.simLoading = false;
        this.showSimModal = false;
        this.lastIngestionFeedback = res.data;
        this.loadTransactions();
      },
      error: (err) => {
        this.simLoading = false;
        this.showSimModal = false;
        this.loadTransactions();
      }
    });
  }

  isHighRiskCountry(country: string): boolean {
    return ['IR', 'KP', 'RU', 'MM', 'SY', 'CY'].includes(country);
  }
}
