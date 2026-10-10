import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <!-- Brand Header -->
        <div class="card-header">
          <div class="brand-badge">
            <span class="material-icons brand-icon">security</span>
          </div>
          <h1>AML GUARDIAN</h1>
          <p class="subtitle">Enterprise Anti-Money Laundering & Compliance Platform</p>
          <div class="mode-badge">SIMULATION MODE &bull; NON-PRODUCTION</div>
        </div>

        <!-- Error Alert -->
        <div class="error-banner" *ngIf="errorMessage">
          <span class="material-icons">error_outline</span>
          <span>{{ errorMessage }}</span>
        </div>

        <!-- Login Form -->
        <form (ngSubmit)="onSubmit()" class="login-form">
          <div class="form-group">
            <label for="username">Username</label>
            <div class="input-prefix">
              <span class="material-icons">account_circle</span>
              <input 
                id="username" 
                type="text" 
                [(ngModel)]="username" 
                name="username" 
                placeholder="Enter username" 
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <div class="input-prefix">
              <span class="material-icons">lock</span>
              <input 
                id="password" 
                type="password" 
                [(ngModel)]="password" 
                name="password" 
                placeholder="Enter password" 
                required
              />
            </div>
          </div>

          <button type="submit" class="submit-btn" [disabled]="loading">
            <span *ngIf="!loading">Authenticate & Enter Platform</span>
            <span *ngIf="loading">Authenticating...</span>
          </button>
        </form>

        <!-- Quick Demo Switcher -->
        <div class="demo-profiles">
          <div class="divider">
            <span>OR SELECT A DEMO ROLE</span>
          </div>

          <div class="profiles-grid">
            <button class="role-card" (click)="quickLogin('analyst.jones', 'Pass1234!')">
              <span class="material-icons role-icon">analytics</span>
              <div class="role-text">
                <span class="role-title">Senior AML Analyst</span>
                <span class="role-desc">analyst.jones &bull; Alert & Case triage</span>
              </div>
            </button>

            <button class="role-card" (click)="quickLogin('officer.smith', 'Pass1234!')">
              <span class="material-icons role-icon">verified_user</span>
              <div class="role-text">
                <span class="role-title">Compliance Officer</span>
                <span class="role-desc">officer.smith &bull; KYC reviews & Approvals</span>
              </div>
            </button>

            <button class="role-card" (click)="quickLogin('auditor.davis', 'Pass1234!')">
              <span class="material-icons role-icon">fact_check</span>
              <div class="role-text">
                <span class="role-title">Internal Auditor</span>
                <span class="role-desc">auditor.davis &bull; Immutable audit trail</span>
              </div>
            </button>

            <button class="role-card" (click)="quickLogin('admin.evans', 'Pass1234!')">
              <span class="material-icons role-icon">admin_panel_settings</span>
              <div class="role-text">
                <span class="role-title">Compliance Admin</span>
                <span class="role-desc">admin.evans &bull; Rule engine & Security</span>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #0d1f3c 0%, #1a3a6b 50%, #0d1f3c 100%);
      padding: 20px;
      font-family: 'Inter', -apple-system, sans-serif;
    }

    .login-card {
      width: 100%;
      max-width: 480px;
      background: #ffffff;
      border-radius: 12px;
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
      padding: 36px 32px;
    }

    .card-header {
      text-align: center;
      margin-bottom: 24px;
    }

    .brand-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 56px;
      height: 56px;
      border-radius: 50%;
      background: #1a3a6b;
      margin-bottom: 12px;
    }

    .brand-icon {
      font-size: 32px;
      color: #c9a94d;
    }

    h1 {
      font-size: 22px;
      font-weight: 800;
      color: #0d1f3c;
      letter-spacing: 1px;
      margin: 0 0 6px 0;
    }

    .subtitle {
      font-size: 13px;
      color: #64748b;
      margin: 0 0 12px 0;
    }

    .mode-badge {
      display: inline-block;
      background: #fef3c7;
      color: #92400e;
      border: 1px solid #fde68a;
      font-size: 10px;
      font-weight: 700;
      padding: 3px 10px;
      border-radius: 12px;
      letter-spacing: 0.5px;
    }

    .error-banner {
      display: flex;
      align-items: center;
      gap: 8px;
      background: #fee2e2;
      border-left: 4px solid #ef4444;
      color: #b91c1c;
      padding: 10px 12px;
      border-radius: 4px;
      font-size: 13px;
      margin-bottom: 20px;
    }

    .error-banner .material-icons {
      font-size: 18px;
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .form-group label {
      font-size: 12px;
      font-weight: 600;
      color: #334155;
    }

    .input-prefix {
      display: flex;
      align-items: center;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      padding: 0 12px;
      background: #f8fafc;
      transition: border-color 0.2s, box-shadow 0.2s;
    }

    .input-prefix:focus-within {
      border-color: #1a3a6b;
      box-shadow: 0 0 0 3px rgba(26, 58, 107, 0.12);
      background: #ffffff;
    }

    .input-prefix .material-icons {
      font-size: 18px;
      color: #94a3b8;
      margin-right: 8px;
    }

    .input-prefix input {
      flex: 1;
      border: none;
      outline: none;
      background: transparent;
      padding: 10px 0;
      font-size: 14px;
      color: #0f172a;
    }

    .submit-btn {
      margin-top: 8px;
      background: #1a3a6b;
      color: #ffffff;
      border: none;
      border-radius: 6px;
      padding: 12px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.2s;
    }

    .submit-btn:hover:not(:disabled) {
      background: #2a5298;
    }

    .submit-btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .demo-profiles {
      margin-top: 24px;
    }

    .divider {
      text-align: center;
      position: relative;
      margin-bottom: 16px;
    }

    .divider::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 1px;
      background: #e2e8f0;
      z-index: 1;
    }

    .divider span {
      position: relative;
      z-index: 2;
      background: #ffffff;
      padding: 0 12px;
      font-size: 11px;
      font-weight: 700;
      color: #94a3b8;
      letter-spacing: 0.5px;
    }

    .profiles-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
    }

    .role-card {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      background: #f8fafc;
      cursor: pointer;
      text-align: left;
      transition: all 0.2s;
    }

    .role-card:hover {
      border-color: #1a3a6b;
      background: #f1f5f9;
      transform: translateY(-1px);
    }

    .role-icon {
      font-size: 20px;
      color: #1a3a6b;
    }

    .role-text {
      display: flex;
      flex-direction: column;
    }

    .role-title {
      font-size: 12px;
      font-weight: 600;
      color: #1e293b;
    }

    .role-desc {
      font-size: 10px;
      color: #64748b;
    }
  `]
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  loading = false;
  errorMessage = '';

  onSubmit(): void {
    if (!this.username || !this.password) return;
    this.login(this.username, this.password);
  }

  quickLogin(user: string, pass: string): void {
    this.username = user;
    this.password = pass;
    this.login(user, pass);
  }

  private login(user: string, pass: string): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login({ username: user, password: pass }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Authentication failed. Please check credentials.';
      }
    });
  }
}
