import { Component, OnInit, OnDestroy, Inject, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AsyncPipe, LowerCasePipe, DOCUMENT } from '@angular/common';
import { Subscription } from 'rxjs';
import { SidebarService } from '../sidebar/sidebar.service';

import { Navbar, CurrentUserDTO } from './service/navbar';
import { NotificationService } from '../../shared/services/notification.service';
import { NotificationDropdownComponent } from './notification-dropdown/notification-dropdown.component';
import { NotificationType } from '../../shared/enums/notification-type.enum';
import { AlarmService } from '../../shared/alarm/alarm.service';

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

  private alarmSound = new Audio('sounds/alarm.mp3');
  private isAlarmPlaying = false;
  private notificationSub?: Subscription;


  constructor(
    private sidebarService: SidebarService,
    private router: Router,
    private navbarService: Navbar,
    private notificationService: NotificationService,
    private alarmService: AlarmService,
    @Inject(DOCUMENT) private document: Document,
    private cdr: ChangeDetectorRef
  ) {
    this.isAuthenticated$ = this.sidebarService.isAuthenticated$;
    this.userRole$ = this.sidebarService.userRole$;
    this.alarmSound.loop = true;
  }

  ngOnInit(): void {
    this.authSubscription = this.isAuthenticated$.subscribe(isAuthenticated => {
      if (isAuthenticated) {
        this.initializeNotifications();
        this.navbarService.getCurrentUser().subscribe(user => {
          this.currentUser = user;
          this.cdr.detectChanges();
        });
      } else {
        this.notificationService.disconnect();
      }
    });

    this.notificationSub = this.notificationService.getNotifications()
      .subscribe(notifications => {
        const  hasUnreadPanic = notifications.some(n =>
          n.type === NotificationType.PANIC_ACTIVATED && !n.isRead
        );

        if (hasUnreadPanic) {
          this.alarmService.start();
        } else {
          this.alarmService.stop();
        }
      });
  }


  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
    this.notificationSub?.unsubscribe();
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

    this.cdr.detectChanges();
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
        this.cdr.detectChanges();
      });
    } else {
      this.navbarService.goOffline({sessionId: Number(storage?.getItem('sessionId'))}).subscribe(()=>{
        this.status = false;
        this.cdr.detectChanges();
      });
    }
  }
}
