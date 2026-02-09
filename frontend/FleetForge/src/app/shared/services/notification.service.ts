import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { NotificationDTO } from '../dtos/notification.dtos';
import { NotificationWebSocketService } from './notification-websocket.service';
import { NotificationRestService } from './notification-rest.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService implements OnDestroy {
  private notifications$ = new BehaviorSubject<NotificationDTO[]>([]);
  private unreadCount$ = new BehaviorSubject<number>(0);
  private newNotification$ = new Subject<NotificationDTO>();
  private wsSubscription?: Subscription;
  private isInitialized = false;

  constructor(
    private notificationWsService: NotificationWebSocketService,
    private notificationRestService: NotificationRestService
  ) {}

  async initialize(): Promise<void> {
    if (this.isInitialized) {
      return;
    }

    try {
      // Connect to WebSocket
      await this.notificationWsService.connect();
      
      // Subscribe to incoming notifications
      this.wsSubscription = this.notificationWsService.subscribeToNotifications()
        .subscribe((notification) => {
          this.handleNewNotification(notification);
        });

      // Load initial notifications and unread count
      this.loadNotifications();
      this.loadUnreadCount();

      this.isInitialized = true;
      console.log('✅ Notification service initialized');
    } catch (error) {
      console.error('❌ Failed to initialize notification service:', error);
      throw error;
    }
  }

  private handleNewNotification(notification: NotificationDTO): void {
    // Add to the beginning of the list
    const currentNotifications = this.notifications$.value;
    this.notifications$.next([notification, ...currentNotifications]);
    
    if (!notification.isRead) {
      this.unreadCount$.next(this.unreadCount$.value + 1);
    }

    this.newNotification$.next(notification);
    
  }

  loadNotifications(): void {
    this.notificationRestService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications$.next(notifications);
      },
      error: (error) => {
        console.error('Failed to load notifications:', error);
      }
    });
  }

  loadUnreadCount(): void {
    this.notificationRestService.getUnreadCount().subscribe({
      next: (count) => {
        this.unreadCount$.next(count);
      },
      error: (error) => {
        console.error('Failed to load unread count:', error);
      }
    });
  }

  markAsRead(notificationId: number): void {
    this.notificationRestService.markAsRead(notificationId).subscribe({
      next: () => {
        const notifications = this.notifications$.value;
        const notification = notifications.find(n => n.id === notificationId);
        if (notification && !notification.isRead) {
          notification.isRead = true;
          this.notifications$.next([...notifications]);
          this.unreadCount$.next(Math.max(0, this.unreadCount$.value - 1));
        }
      },
      error: (error) => {
        console.error('Failed to mark notification as read:', error);
      }
    });
  }

  markAllAsRead(): void {
    this.notificationRestService.markAllAsRead().subscribe({
      next: () => {
        const notifications = this.notifications$.value;
        notifications.forEach(n => n.isRead = true);
        this.notifications$.next([...notifications]);
        this.unreadCount$.next(0);
      },
      error: (error) => {
        console.error('Failed to mark all notifications as read:', error);
      }
    });
  }

  getNotifications(): Observable<NotificationDTO[]> {
    return this.notifications$.asObservable();
  }

  getUnreadCount(): Observable<number> {
    return this.unreadCount$.asObservable();
  }


  getNewNotification(): Observable<NotificationDTO> {
    return this.newNotification$.asObservable();
  }


  ngOnDestroy(): void {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    this.notificationWsService.disconnect();
  }

  disconnect(): void {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    this.notificationWsService.disconnect();
    this.isInitialized = false;
    this.notifications$.next([]);
    this.unreadCount$.next(0);
  }
}
