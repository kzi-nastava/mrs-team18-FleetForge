
import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AsyncPipe, LowerCasePipe, DOCUMENT } from '@angular/common';
import { Subscription } from 'rxjs';
import { SidebarService } from '../sidebar/sidebar.service';

import { Navbar, CurrentUserDTO } from './service/navbar';
import { NotificationService } from '../../shared/services/notification.service';
import { NotificationDropdownComponent } from './notification-dropdown/notification-dropdown.component';

export interface NavItem {
  label: string;
  path: string;
  exact: boolean;
}

@Component({
  selector: 'app-navbar',
  imports: [RouterModule, AsyncPipe, LowerCasePipe, NotificationDropdownComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  standalone: true,
})
export class NavbarComponent implements OnInit, OnDestroy {
  navItems: NavItem[] = [
    { label: 'Home', path: '/', exact: true },
    { label: 'Map', path: '/map', exact: false },
    { label: 'Contact', path: '/contact', exact: false },
  ];

  currentUser: CurrentUserDTO | null = null;

  isAuthenticated$!: SidebarService['isAuthenticated$'];
  userRole$!: SidebarService['userRole$'];
  showProfileMenu = false;
  status: boolean = false;
  private authSubscription?: Subscription;

  constructor(
    private sidebarService: SidebarService, 
    private router: Router, 
    private navbarService: Navbar,
    private notificationService: NotificationService,
    @Inject(DOCUMENT) private document: Document
  ) {
    this.isAuthenticated$ = this.sidebarService.isAuthenticated$;
    this.userRole$ = this.sidebarService.userRole$;
  }

  ngOnInit(): void {
    this.authSubscription = this.isAuthenticated$.subscribe(isAuthenticated => {
      if (isAuthenticated) {
        this.initializeNotifications();
        this.navbarService.getCurrentUser().subscribe(user => {
          this.currentUser = user;
        });
      } else {
        this.notificationService.disconnect();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  private async initializeNotifications(): Promise<void> {
    try {
      await this.notificationService.initialize();
    } catch (error) {
      console.error('❌ Failed to initialize notifications:', error);
    }
  }

  onLogoClick(): void {
    this.sidebarService.toggleSidebar();
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
  }

  onLogout(): void {
    const storage = this.document.defaultView?.localStorage;
    if(storage?.getItem('role') === 'DRIVER' && this.status){
      this.navbarService.goOffline({sessionId: Number(storage?.getItem('sessionId'))}).subscribe();
    }
    this.status = false;
    
    // Disconnect notifications before logging out
    this.notificationService.disconnect();
    
    this.sidebarService.setAuthenticated(false);
    this.sidebarService.setUserRole(null);
    this.showProfileMenu = false;
    storage?.removeItem('token');
    storage?.removeItem('role');
    this.router.navigate(['/']);
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
    const storage = this.document.defaultView?.localStorage;
    if(!this.status){
      this.navbarService.goOnline().subscribe((response)=>{
        storage?.setItem('sessionId', response.sessionId.toString());
        this.status = true;
      });
    } else {
      this.navbarService.goOffline({sessionId: Number(storage?.getItem('sessionId'))}).subscribe(()=>{
        this.status = false;
      });
    }
  }
}
