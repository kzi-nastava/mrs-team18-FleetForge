import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AsyncPipe, LowerCasePipe } from '@angular/common';
import { SidebarService } from '../sidebar/sidebar.service';
import { Navbar } from './service/navbar';

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
  status: boolean = false;

  constructor(private sidebarService: SidebarService, private router: Router, private navbarService: Navbar) {
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
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    this.router.navigate(['/']);
    this.status = false;
    this.navbarService.goOffline({sessionId: Number(localStorage.getItem('sessionId'))}).subscribe();
  }

  viewProfile(): void {
    this.userRole$.subscribe(role => {
      if(role === 'PASSENGER'){
        this.router.navigate(['profile-passenger']);
      }
      else if(role === 'ADMIN'){
        this.router.navigate(['profile-admin']);
      }
      else if(role === 'DRIVER'){
        this.router.navigate(['profile-driver']);
      }
    });
  }
  changeStatus(): void {
    if(!this.status){
      this.navbarService.goOnline().subscribe((response)=>{
        localStorage.setItem('sessionId', response.sessionId.toString());
        this.status = true;
      });
    } else {
      this.navbarService.goOffline({sessionId: Number(localStorage.getItem('sessionId'))}).subscribe(()=>{
        this.status = false;
      });
    }
  }
}
