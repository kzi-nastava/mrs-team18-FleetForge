import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AsyncPipe, LowerCasePipe } from '@angular/common';
import { SidebarService } from '../sidebar/sidebar.service';
import { UserRole } from '../sidebar/sidebar-config';

export interface NavItem {
  label: string;
  path: string;
  exact: boolean;
}

@Component({
  selector: 'app-navbar',
  imports: [RouterModule, AsyncPipe, LowerCasePipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  standalone: true,
})
export class NavbarComponent {
  navItems: NavItem[] = [
    { label: 'Home', path: '/', exact: true },
    { label: 'Map', path: '/map', exact: false },
    { label: 'Contact', path: '/contact', exact: false },
  ];

  isAuthenticated$!: SidebarService['isAuthenticated$'];
  userRole$!: SidebarService['userRole$'];
  showProfileMenu = false;

  constructor(private sidebarService: SidebarService, private router: Router) {
    this.isAuthenticated$ = this.sidebarService.isAuthenticated$;
    this.userRole$ = this.sidebarService.userRole$;
  }

  onLogoClick(): void {
    this.sidebarService.toggleSidebar();
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
  }

  onLogout(): void {
    this.sidebarService.setAuthenticated(false);
    this.sidebarService.setUserRole(null);
    this.showProfileMenu = false;
    this.router.navigate(['/']);
  }
}
