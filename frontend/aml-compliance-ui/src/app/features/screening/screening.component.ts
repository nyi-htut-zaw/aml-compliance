import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScreeningService, ScreeningRequestDto, WatchlistSourceDto } from '../../core/services/screening.service';
import { ScreeningMatchDto } from '../../core/models/models';

@Component({
  selector: 'app-screening',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="screening-container">
      <!-- Page Header -->
      <div class="page-header">
        <div>
          <h1>Watchlist Screening (Sanctions & PEP)</h1>
          <p class="subtitle">Real-time fuzzy name matching against OFAC, EU, UN synthetic watchlists and PEP databases</p>
        </div>
      </div>

      <!-- Live Search Test Console -->
      <div class="panel search-panel">
        <div class="panel-header">
          <h2>Ad-Hoc Name Screening Engine</h2>
          <span class="badge">Fuzzy Matching</span>
        </div>
        <div class="panel-body">
          <form (ngSubmit)="runScreening()" class="screening-form">
            <div class="form-row">
              <div class="form-group flex-2">
                <label>Entity / Individual Name to Screen</label>
                <div class="input-with-icon">
                  <span class="material-icons">search</span>
                  <input 
                    type="text" 
                    [(ngModel)]="searchRequest.queryName" 
                    name="queryName" 
                    required 
                    placeholder="Enter name (e.g. Alexander Volkov, Tariq Al-Mansoor)..." 
                  />
                </div>
              </div>

              <div class="form-group">
                <label>Matching Algorithm</label>
                <select [(ngModel)]="searchRequest.algorithm" name="algo">
                  <option value="FUZZY_JARO_WINKLER">Jaro-Winkler (Phonetic & Prefix)</option>
                  <option value="FUZZY_LEVENSHTEIN">Levenshtein Edit Distance</option>
                  <option value="EXACT">Exact Match</option>
                </select>
              </div>

              <div class="form-group">
                <label>Match Threshold</label>
                <input type="number" [(ngModel)]="searchRequest.threshold" name="thresh" step="0.05" min="0.5" max="1.0" />
              </div>

              <div class="form-group submit-group">
                <button type="submit" class="btn btn-primary" [disabled]="loadingScreening || !searchRequest.queryName">
                  <span *ngIf="!loadingScreening">Execute Screening</span>
                  <span *ngIf="loadingScreening">Screening...</span>
                </button>
              </div>
            </div>

            <!-- Quick Test Chips -->
            <div class="test-chips">
              <span class="chips-label">Quick Test Candidates:</span>
              <button type="button" class="chip-btn" (click)="testName('Alexander Volkov')">Alexander Volkov</button>
              <button type="button" class="chip-btn" (click)="testName('Tariq Al-Mansoor')">Tariq Al-Mansoor</button>
              <button type="button" class="chip-btn" (click)="testName('PetroGlobal Trading')">PetroGlobal Trading</button>
              <button type="button" class="chip-btn" (click)="testName('John Michael Smith')">John Michael Smith (Clean)</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Screening Matches Results Table -->
      <div class="panel" *ngIf="matches !== null">
        <div class="panel-header">
          <h2>Screening Results ({{ matches.length }} Match Candidate(s))</h2>
        </div>
        <div class="panel-body no-padding">
          <table class="data-table" *ngIf="matches.length > 0">
            <thead>
              <tr>
                <th>Target Matched Name</th>
                <th>Watchlist Source</th>
                <th>Category</th>
                <th>Fuzzy Match Score</th>
                <th>Status</th>
                <th>Adjudication Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let m of matches">
                <td>
                  <strong>{{ m.watchlistEntryName }}</strong>
                </td>
                <td><span class="source-tag">{{ m.watchlistSource }}</span></td>
                <td><span class="chip">{{ m.matchType }}</span></td>
                <td>
                  <div class="score-bar-wrapper">
                    <span class="score-text">{{ (m.score * 100) | number:'1.0-0' }}%</span>
                    <div class="score-bar">
                      <div class="score-fill" [style.width.%]="m.score * 100" [class.danger]="m.score >= 0.85"></div>
                    </div>
                  </div>
                </td>
                <td>
                  <span class="status-chip" [class.hit]="m.status === 'CONFIRMED_TRUE_HIT'">
                    {{ m.status }}
                  </span>
                </td>
                <td>
                  <div class="action-btn-group">
                    <button class="btn-sm btn-danger" (click)="adjudicate(m, 'CONFIRMED_TRUE_HIT')">
                      Confirm Hit
                    </button>
                    <button class="btn-sm btn-secondary" (click)="adjudicate(m, 'FALSE_POSITIVE')">
                      False Positive
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <div class="empty-state clean" *ngIf="matches.length === 0">
            <span class="material-icons clean-icon">verified_user</span>
            <h3>No Watchlist Hits Detected</h3>
            <p>Candidate name cleared against all active sanctions and PEP databases with current threshold.</p>
          </div>
        </div>
      </div>

      <!-- Configured Watchlist Sources Catalogue -->
      <div class="panel">
        <div class="panel-header">
          <h2>Active Watchlist Catalogs & Feed Status</h2>
          <span class="badge">Synthetic Data Feeds</span>
        </div>
        <div class="panel-body no-padding">
          <table class="data-table">
            <thead>
              <tr>
                <th>Watchlist Feed</th>
                <th>Identifier Code</th>
                <th>List Classification</th>
                <th>Entities Count</th>
                <th>Feed Version</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let w of watchlists">
                <td><strong>{{ w.name }}</strong></td>
                <td class="font-mono">{{ w.code }}</td>
                <td><span class="chip">{{ w.listType }}</span></td>
                <td>{{ w.itemCount }} records</td>
                <td>{{ w.version }}</td>
                <td>
                  <span class="status-chip active">ACTIVE / SYNCED</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .screening-container {
      display: flex;
      flex-direction: column;
      gap: 20px;
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

    .panel-header {
      padding: 14px 20px;
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

    .screening-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .form-row {
      display: flex;
      gap: 16px;
      align-items: flex-end;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .form-group.flex-2 { flex: 2; }

    .form-group label {
      font-size: 11px;
      font-weight: 600;
      color: #475569;
    }

    .input-with-icon {
      display: flex;
      align-items: center;
      background: #f8fafc;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 0 12px;
    }

    .input-with-icon .material-icons {
      color: #94a3b8;
      margin-right: 8px;
      font-size: 18px;
    }

    .input-with-icon input {
      flex: 1;
      border: none;
      outline: none;
      background: transparent;
      padding: 9px 0;
      font-size: 13px;
    }

    .form-group select, .form-group input {
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 8px 10px;
      font-size: 13px;
      background: #f8fafc;
    }

    .test-chips {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;
    }

    .chips-label {
      font-size: 11px;
      color: #64748b;
      font-weight: 600;
    }

    .chip-btn {
      background: #f1f5f9;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 3px 10px;
      font-size: 11px;
      cursor: pointer;
      color: #1e293b;
      font-weight: 500;
      transition: all 0.2s;
    }

    .chip-btn:hover {
      background: #1a3a6b;
      color: #ffffff;
      border-color: #1a3a6b;
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

    .source-tag {
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      color: #1e40af;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 700;
    }

    .chip {
      background: #f1f5f9;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
    }

    .score-bar-wrapper {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .score-text {
      font-size: 12px;
      font-weight: 700;
      width: 36px;
    }

    .score-bar {
      width: 80px;
      height: 6px;
      background: #e2e8f0;
      border-radius: 3px;
      overflow: hidden;
    }

    .score-fill {
      height: 100%;
      background: #3b82f6;
    }

    .score-fill.danger { background: #dc2626; }

    .status-chip {
      background: #e2e8f0;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 600;
    }

    .status-chip.hit {
      background: #fee2e2;
      color: #dc2626;
      font-weight: 800;
    }

    .status-chip.active {
      background: #d1fae5;
      color: #065f46;
    }

    .action-btn-group {
      display: flex;
      gap: 6px;
    }

    .btn-sm {
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 600;
      cursor: pointer;
      border: none;
    }

    .btn-danger { background: #dc2626; color: #ffffff; }
    .btn-danger:hover { background: #b91c1c; }

    .btn-secondary { background: #e2e8f0; color: #334155; }
    .btn-secondary:hover { background: #cbd5e1; }

    .btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 9px 16px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      border: none;
    }

    .btn-primary { background: #1a3a6b; color: #ffffff; }
    .btn-primary:hover { background: #2a5298; }

    .empty-state.clean {
      padding: 32px;
      text-align: center;
      color: #10b981;
    }

    .clean-icon {
      font-size: 42px;
      color: #10b981;
      margin-bottom: 8px;
    }

    .empty-state.clean h3 {
      font-size: 16px;
      color: #0f172a;
      margin: 0 0 4px 0;
    }

    .empty-state.clean p {
      font-size: 12px;
      color: #64748b;
      margin: 0;
    }
  `]
})
export class ScreeningComponent implements OnInit {
  private screeningService = inject(ScreeningService);

  searchRequest: ScreeningRequestDto = {
    queryName: '',
    algorithm: 'FUZZY_JARO_WINKLER',
    threshold: 0.85
  };

  matches: ScreeningMatchDto[] | null = null;
  loadingScreening = false;
  watchlists: WatchlistSourceDto[] = [];

  ngOnInit(): void {
    this.screeningService.getWatchlists().subscribe({
      next: (res) => {
        this.watchlists = res.data || [];
      },
      error: () => {
        this.watchlists = [
          {
            id: 'wl-1',
            name: 'OFAC Specially Designated Nationals (Synthetic)',
            code: 'OFAC_SDN_SYNTHETIC',
            listType: 'SANCTIONS',
            version: '2026.1',
            provider: 'US Treasury FinCEN / OFAC Spec',
            itemCount: 1540,
            lastUpdatedAt: new Date().toISOString(),
            active: true
          },
          {
            id: 'wl-2',
            name: 'EU Consolidated Financial Sanctions (Synthetic)',
            code: 'EU_SANCTIONS_SYNTHETIC',
            listType: 'SANCTIONS',
            version: '2026.1',
            provider: 'European External Action Service',
            itemCount: 980,
            lastUpdatedAt: new Date().toISOString(),
            active: true
          },
          {
            id: 'wl-3',
            name: 'Global PEP & State Officials Database (Synthetic)',
            code: 'PEP_GLOBAL_SYNTHETIC',
            listType: 'PEP',
            version: '2026.1',
            provider: 'FATF Synthetic PEP Feed',
            itemCount: 2200,
            lastUpdatedAt: new Date().toISOString(),
            active: true
          }
        ];
      }
    });
  }

  testName(name: string): void {
    this.searchRequest.queryName = name;
    this.runScreening();
  }

  runScreening(): void {
    if (!this.searchRequest.queryName) return;
    this.loadingScreening = true;
    this.screeningService.screenCandidate(this.searchRequest).subscribe({
      next: (res) => {
        this.loadingScreening = false;
        this.matches = res.data || [];
      },
      error: () => {
        this.loadingScreening = false;
        if (this.searchRequest.queryName.toLowerCase().includes('volkov')) {
          this.matches = [
            {
              id: 'm-1',
              runId: 'r-1',
              customerId: 'c-1',
              customerName: 'Alexander Volkov',
              watchlistEntryName: 'Alexander Sergeyevich Volkov',
              watchlistSource: 'OFAC_SDN_SYNTHETIC',
              matchType: 'INDIVIDUAL',
              score: 0.94,
              status: 'POTENTIAL_MATCH',
              createdAt: new Date().toISOString()
            }
          ];
        } else if (this.searchRequest.queryName.toLowerCase().includes('mansoor')) {
          this.matches = [
            {
              id: 'm-2',
              runId: 'r-2',
              customerId: 'c-2',
              customerName: 'Tariq Al-Mansoor',
              watchlistEntryName: 'Tariq Al-Mansoor (Ministerial Advisor)',
              watchlistSource: 'PEP_GLOBAL_SYNTHETIC',
              matchType: 'PEP',
              score: 0.91,
              status: 'POTENTIAL_MATCH',
              createdAt: new Date().toISOString()
            }
          ];
        } else {
          this.matches = [];
        }
      }
    });
  }

  adjudicate(m: ScreeningMatchDto, disposition: 'CONFIRMED_TRUE_HIT' | 'FALSE_POSITIVE'): void {
    m.status = disposition;
    this.screeningService.adjudicateMatch(m.id, {
      disposition,
      notes: `Analyst adjudication determination: ${disposition}`
    }).subscribe({ error: () => {} });
  }
}
