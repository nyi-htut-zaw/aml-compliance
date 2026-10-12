import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/models';
import { LoginRequest, AuthResponse, UserSummary } from '../models/models';

const TOKEN_KEY = 'aml_auth_token';
const USER_KEY = 'aml_user_info';
const JURISDICTION_KEY = 'aml_jurisdiction';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;

  // Reactive state signals
  currentUser = signal<UserSummary | null>(this.getStoredUser());
  token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  jurisdiction = signal<string>(localStorage.getItem(JURISDICTION_KEY) || 'US');

  isAuthenticated = computed(() => !!this.token() && !!this.currentUser());

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap(res => {
        if (res.data) {
          this.setSession(res.data.token, res.data.user);
        }
      })
    );
  }

  getCurrentUserProfile(): Observable<ApiResponse<UserSummary>> {
    return this.http.get<ApiResponse<UserSummary>>(`${this.apiUrl}/auth/me`).pipe(
      tap(res => {
        if (res.data) {
          this.currentUser.set(res.data);
          localStorage.setItem(USER_KEY, JSON.stringify(res.data));
        }
      })
    );
  }

  logout(): void {
    try {
      this.http.post(`${this.apiUrl}/auth/logout`, {}).subscribe({ error: () => {} });
    } catch (e) {
      // ignore logout network errors
    }
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.token.set(null);
    this.currentUser.set(null);
  }

  setJurisdiction(jurisdiction: string): void {
    this.jurisdiction.set(jurisdiction);
    localStorage.setItem(JURISDICTION_KEY, jurisdiction);
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    if (!user) return false;
    return user.roles.includes(role) || user.roles.includes('ADMIN');
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.currentUser();
    if (!user) return false;
    return roles.some(r => user.roles.includes(r)) || user.roles.includes('ADMIN');
  }

  hasPermission(permission: string): boolean {
    const user = this.currentUser();
    if (!user) return false;
    return user.permissions.includes(permission) || user.roles.includes('ADMIN');
  }

  private setSession(token: string, user: UserSummary): void {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.token.set(token);
    this.currentUser.set(user);
  }

  private getStoredUser(): UserSummary | null {
    try {
      const stored = localStorage.getItem(USER_KEY);
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  }
}
