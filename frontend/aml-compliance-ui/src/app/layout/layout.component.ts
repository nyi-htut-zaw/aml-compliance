import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="enterprise-app">
      <!-- Top Navigation Bar -->
      <header class="topbar">
        <div class="brand">
          <span class="material-icons brand-icon">security</span>
          <div class="brand-text">
            <span class="brand-title">AML GUARDIAN</span>
            <span class="brand-subtitle">Financial Crime & Compliance Platform</span>
          </div>
          <span class="env-pill">SIMULATION DEMO</span>
        </div>

        <div class="topbar-actions">
          <!-- Jurisdiction Selector -->
          <div class="jurisdiction-selector">
            <span class="material-icons juris-icon">public</span>
            <label for="jurisSelect" class="sr-only">Jurisdiction</label>
            <select id="jurisSelect" [value]="authService.jurisdiction()" (change)="onJurisdictionChange($event)">
              <option value="US">🇺🇸 FinCEN (USA / BSA)</option>
              <option value="EU">🇪🇺 6AMLD (European Union)</option>
              <option value="UK">🇬🇧 FCA / MLR (United Kingdom)</option>
              <option value="SG">🇸🇬 MAS Notice 626 (Singapore)</option>
              <option value="GLOBAL">🌐 FATF 40 Recommendations (Global)</option>
            </select>
          </div>

          <!-- User Menu -->
          <div class="user-profile" *ngIf="authService.currentUser() as user">
            <div class="avatar">{{ user.fullName.charAt(0) }}</div>
            <div class="user-meta">
              <span class="user-name">{{ user.fullName }}</span>
              <span class="user-role">{{ user.roles[0] || 'ANALYST' }} &bull; {{ user.branchCode || 'HQ' }}</span>
            </div>
            <button class="logout-btn" (click)="logout()" title="Sign Out">
              <span class="material-icons">logout</span>
            </button>
          </div>
        </div>
      </header>

      <!-- Main Shell Body: Sidenav + Content Area -->
      <div class="shell-body">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
          <nav class="nav-menu">
            <div class="nav-group">
              <span class="nav-heading">OPERATIONS</span>
              <a routerLink="/dashboard" routerLinkActive="active" class="nav-item">
                <span class="material-icons">dashboard</span>
                <span>Dashboard</span>
              </a>
              <a routerLink="/customers" routerLinkActive="active" class="nav-item">
                <span class="material-icons">badge</span>
                <span>Customer 360 & KYC</span>
              </a>
              <a routerLink="/transactions" routerLinkActive="active" class="nav-item">
                <span class="material-icons">receipt_long</span>
                <span>Transactions</span>
              </a>
            </div>

            <div class="nav-group">
              <span class="nav-heading">INVESTIGATIONS</span>
              <a routerLink="/alerts" routerLinkActive="active" class="nav-item">
                <span class="material-icons">warning_amber</span>
                <span>Alerts Triage</span>
              </a>
              <a routerLink="/cases" routerLinkActive="active" class="nav-item">
                <span class="material-icons">folder_shared</span>
                <span>Case Management</span>
              </a>
            </div>

            <div class="nav-group">
              <span class="nav-heading">COMPLIANCE & SURVEILLANCE</span>
              <a routerLink="/screening" routerLinkActive="active" class="nav-item">
                <span class="material-icons">person_search</span>
                <span>Sanctions & PEP</span>
              </a>
              <a routerLink="/rules" routerLinkActive="active" class="nav-item">
                <span class="material-icons">rule</span>
                <span>Detection Rules</span>
              </a>
              <a routerLink="/reports" routerLinkActive="active" class="nav-item">
                <span class="material-icons">assignment</span>
                <span>Regulatory Reports</span>
              </a>
            </div>

            <div class="nav-group">
              <span class="nav-heading">GOVERNANCE</span>
              <a routerLink="/audit" routerLinkActive="active" class="nav-item">
                <span class="material-icons">history_edu</span>
                <span>Audit Trail</span>
              </a>
            </div>
          </nav>
        </aside>

        <!-- Main Content Viewport -->
        <main class="main-content">
          <router-outlet></router-outlet>
        </main>
      </div>

      <!-- Compliance Disclaimer Footer -->
      <footer class="disclaimer-bar">
        <span class="material-icons disc-icon">gavel</span>
        <span>
          <strong>LEGAL DISCLAIMER:</strong> This is an enterprise compliance simulation prototype. 
          Regulatory decisions, SAR/STR filings, and sanction hit dispositions require review and authorization by qualified, certified compliance officers.
        </span>
      </footer>
    </div>
  `,
  styles: [`
    .enterprise-app {
      display: flex;
      flex-direction: column;
      height: 100vh;
      overflow: hidden;
      background: var(--color-bg, #f4f6fa);
      font-family: 'Inter', -apple-system, sans-serif;
    }

    .topbar {
      height: 64px;
      background: #0d1f3c;
      color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.25);
      z-index: 100;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .brand-icon {
      font-size: 30px;
      color: #c9a94d;
    }

    .brand-text {
      display: flex;
      flex-direction: column;
    }

    .brand-title {
      font-weight: 800;
      font-size: 16px;
      letter-spacing: 1.5px;
      color: #ffffff;
    }

    .brand-subtitle {
      font-size: 11px;
      color: #90a4ae;
      font-weight: 400;
    }

    .env-pill {
      background: rgba(201, 169, 77, 0.2);
      border: 1px solid #c9a94d;
      color: #f0d080;
      font-size: 10px;
      font-weight: 700;
      padding: 2px 8px;
      border-radius: 12px;
      margin-left: 8px;
      letter-spacing: 0.5px;
    }

    .topbar-actions {
      display: flex;
      align-items: center;
      gap: 20px;
    }

    .jurisdiction-selector {
      display: flex;
      align-items: center;
      gap: 6px;
      background: #1a3a6b;
      padding: 6px 12px;
      border-radius: 6px;
      border: 1px solid rgba(255,255,255,0.15);
    }

    .juris-icon {
      font-size: 18px;
      color: #64b5f6;
    }

    .jurisdiction-selector select {
      background: transparent;
      color: #ffffff;
      border: none;
      outline: none;
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
    }

    .jurisdiction-selector select option {
      background: #0d1f3c;
      color: #ffffff;
    }

    .user-profile {
      display: flex;
      align-items: center;
      gap: 10px;
      background: rgba(255,255,255,0.06);
      padding: 4px 12px;
      border-radius: 20px;
    }

    .avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #c9a94d;
      color: #0d1f3c;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
    }

    .user-meta {
      display: flex;
      flex-direction: column;
    }

    .user-name {
      font-size: 12px;
      font-weight: 600;
      color: #ffffff;
    }

    .user-role {
      font-size: 10px;
      color: #90a4ae;
    }

    .logout-btn {
      background: none;
      border: none;
      color: #cfd8dc;
      cursor: pointer;
      display: flex;
      align-items: center;
      padding: 4px;
      border-radius: 4px;
      transition: color 0.2s;
    }

    .logout-btn:hover {
      color: #ff5252;
    }

    .shell-body {
      display: flex;
      flex: 1;
      overflow: hidden;
    }

    .sidebar {
      width: 250px;
      background: #ffffff;
      border-right: 1px solid #e0e6ef;
      overflow-y: auto;
      padding: 16px 0;
      display: flex;
      flex-direction: column;
    }

    .nav-group {
      margin-bottom: 20px;
    }

    .nav-heading {
      display: block;
      font-size: 10px;
      font-weight: 700;
      letter-spacing: 1px;
      color: #8a94a6;
      padding: 0 20px 8px;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 20px;
      color: #4a5568;
      text-decoration: none;
      font-size: 13px;
      font-weight: 500;
      border-left: 3px solid transparent;
      transition: all 0.2s ease;
    }

    .nav-item .material-icons {
      font-size: 20px;
      color: #718096;
    }

    .nav-item:hover {
      background: #f7fafc;
      color: #1a3a6b;
    }

    .nav-item:hover .material-icons {
      color: #1a3a6b;
    }

    .nav-item.active {
      background: #edf2f7;
      color: #1a3a6b;
      font-weight: 600;
      border-left-color: #1a3a6b;
    }

    .nav-item.active .material-icons {
      color: #1a3a6b;
    }

    .main-content {
      flex: 1;
      overflow-y: auto;
      padding: 24px;
      background: #f4f6fa;
    }

    .disclaimer-bar {
      height: 32px;
      background: #1e293b;
      color: #94a3b8;
      font-size: 11px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      padding: 0 16px;
      z-index: 99;
      border-top: 1px solid rgba(255,255,255,0.08);
    }

    .disc-icon {
      font-size: 14px;
      color: #f59e0b;
    }
  `]
})
export class LayoutComponent {
  authService = inject(AuthService);
  private router = inject(Router);

  onJurisdictionChange(event: Event): void {
    const val = (event.target as HTMLSelectElement).value;
    this.authService.setJurisdiction(val);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
