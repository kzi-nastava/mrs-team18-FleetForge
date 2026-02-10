import { Component, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from '../../../shared/services/notification.service';
import { NotificationDTO } from '../../../shared/dtos/notification.dtos';
import { NotificationType } from '../../../shared/enums/notification-type.enum';

@Component({
  selector: 'app-notification-dropdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-dropdown.component.html',
  styleUrls: ['./notification-dropdown.component.css']
})
export class NotificationDropdownComponent implements OnInit, OnDestroy {
  notifications: NotificationDTO[] = [];
  unreadCount: number = 0;
  isOpen: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private notificationService: NotificationService,
    private router: Router,
    private elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    // Subscribe to notifications
    this.subscriptions.push(
      this.notificationService.getNotifications().subscribe((notifications: NotificationDTO[]) => {
        this.notifications = notifications;
      })
    );

    // Subscribe to unread count
    this.subscriptions.push(
      this.notificationService.getUnreadCount().subscribe((count: number) => {
        this.unreadCount = count;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  toggleDropdown(): void {
    this.isOpen = !this.isOpen;
  }

  closeDropdown(): void {
    this.isOpen = false;
  }

  onNotificationClick(notification: NotificationDTO): void {
    if (!notification.isRead) {
      this.notificationService.markAsRead(notification.id);
    }

    
    if (notification.type === NotificationType.RIDE_CREATED) {
        this.router.navigate(['/passenger/current-ride']);

    } else if (notification.type === NotificationType.RIDE_COMPLETED) {
        this.router.navigate(['/passenger/ride-history']);
    }
    this.closeDropdown();
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead();
  }

  getNotificationIcon(type: NotificationType): string {
    switch (type) {
      case NotificationType.RIDE_ACCEPTED:
      case NotificationType.RIDE_STARTED:
    case NotificationType.RIDE_CREATED:
        return '🚗';
      case NotificationType.RIDE_COMPLETED:
        return '✅';
      case NotificationType.RIDE_CANCELLED:
        return '❌';
      case NotificationType.RIDE_SCHEDULED:
      case NotificationType.RIDE_REMINDER:
        return '⏰';
      default:
        return '🔔';
    }
  }

  getTimeAgo(createdAt: string): string {
    const now = new Date();
    const notificationTime = new Date(createdAt);
    const diffMs = now.getTime() - notificationTime.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return notificationTime.toLocaleDateString();
  }


  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.closeDropdown();
    }
  }
}
