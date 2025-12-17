import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarService } from './sidebar.service';
import { SIDEBAR_MENU_CONFIG, SidebarMenuItem, UserRole } from './sidebar-config';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit {
  // Track if sidebar is open - subscribe to service
  isOpen$!: SidebarService['sidebarOpen$'];

  // Track current user role - subscribe to service
  userRole$!: SidebarService['userRole$'];

  // Track authentication state - sidebar only works when authenticated
  isAuthenticated$!: SidebarService['isAuthenticated$'];

  // Store menu items based on current role
  menuItems: SidebarMenuItem[] = [];

  constructor(private sidebarService: SidebarService) {
    // Initialize observables after sidebarService is available
    this.isOpen$ = this.sidebarService.sidebarOpen$;
    this.userRole$ = this.sidebarService.userRole$;
    this.isAuthenticated$ = this.sidebarService.isAuthenticated$;
  }

  ngOnInit(): void {
    // Subscribe to role changes and update menu items accordingly
    this.userRole$.subscribe((role: UserRole | null) => {
      this.menuItems = role ? SIDEBAR_MENU_CONFIG[role] : [];
    });
  }

  // Close sidebar when a menu item is clicked (for better UX on mobile)
  onMenuItemClick(): void {
    this.sidebarService.closeSidebar();
  }

  // Close sidebar when clicking outside on mobile
  onBackdropClick(): void {
    this.sidebarService.closeSidebar();
  }
}
