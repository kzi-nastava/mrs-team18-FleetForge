import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserRole } from './sidebar-config';

// Service to manage sidebar state and user role
// This allows any component to subscribe to sidebar visibility and current user role
@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  // Track whether sidebar is open or closed
  private sidebarOpenSubject = new BehaviorSubject<boolean>(false);
  public sidebarOpen$: Observable<boolean> = this.sidebarOpenSubject.asObservable();

  // Track auth state - default is unauthenticated
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$: Observable<boolean> = this.isAuthenticatedSubject.asObservable();

  // Track current user role (PASSENGER, DRIVER, ADMIN). Null = unauthenticated/unknown
  private userRoleSubject = new BehaviorSubject<UserRole | null>(null);
  public userRole$: Observable<UserRole | null> = this.userRoleSubject.asObservable();

  constructor() {}

  // Toggle sidebar open/closed state (only if authenticated)
  toggleSidebar(): void {
    if (!this.isAuthenticatedSubject.value) {
      this.sidebarOpenSubject.next(false);
      return;
    }
    this.sidebarOpenSubject.next(!this.sidebarOpenSubject.value);
  }

  // Close sidebar
  closeSidebar(): void {
    this.sidebarOpenSubject.next(false);
  }

  // Open sidebar (only if authenticated)
  openSidebar(): void {
    if (!this.isAuthenticatedSubject.value) {
      this.sidebarOpenSubject.next(false);
      return;
    }
    this.sidebarOpenSubject.next(true);
  }

  // Set authentication state
  setAuthenticated(isAuthenticated: boolean): void {
    this.isAuthenticatedSubject.next(isAuthenticated);
    if (!isAuthenticated) {
      // On logout ensure sidebar is closed and role cleared
      this.sidebarOpenSubject.next(false);
      this.userRoleSubject.next(null);
    }
  }

  // Set user role (will be called after user logs in)
  setUserRole(role: UserRole | null): void {
    this.userRoleSubject.next(role);
    if (!role) {
      // If role is cleared, also close sidebar for safety
      this.sidebarOpenSubject.next(false);
    }
  }

  // Get current role (for immediate access without subscription)
  getCurrentRole(): UserRole | null {
    return this.userRoleSubject.value;
  }
}
