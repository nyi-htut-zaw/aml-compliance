import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { CustomersComponent } from './features/customers/customers.component';
import { TransactionsComponent } from './features/transactions/transactions.component';
import { AlertsComponent } from './features/alerts/alerts.component';
import { CasesComponent } from './features/cases/cases.component';
import { ScreeningComponent } from './features/screening/screening.component';
import { RulesComponent } from './features/rules/rules.component';
import { ReportsComponent } from './features/reports/reports.component';
import { AuditComponent } from './features/audit/audit.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'customers', component: CustomersComponent },
      { path: 'transactions', component: TransactionsComponent },
      { path: 'alerts', component: AlertsComponent },
      { path: 'cases', component: CasesComponent },
      { path: 'screening', component: ScreeningComponent },
      { path: 'rules', component: RulesComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'audit', component: AuditComponent }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
