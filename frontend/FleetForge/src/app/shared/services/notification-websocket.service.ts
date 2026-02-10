import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Client, IFrame, IMessage, StompSubscription } from '@stomp/stompjs';
import { NotificationDTO } from '../dtos/notification.dtos';
import { AuthService } from '../../auth/service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationWebSocketService implements OnDestroy {
  private client: Client;
  private isConnected$ = new BehaviorSubject<boolean>(false);
  private notificationsSubject$ = new Subject<NotificationDTO>();
  private subscriptions: StompSubscription[] = [];
  private connectionResolve?: () => void;
  private connectionTimeout?: number;

  constructor(private authService: AuthService) {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws/websocket',
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.setupClientHandlers();
  }

  private setupClientHandlers(): void {
    this.client.onConnect = (frame: IFrame) => {
      console.log('✅ Notification WebSocket connected:', frame);
      this.isConnected$.next(true);
      if (this.connectionResolve) {
        this.connectionResolve();
        this.connectionResolve = undefined;
      }
      if (this.connectionTimeout) {
        clearTimeout(this.connectionTimeout);
      }
    };

    this.client.onDisconnect = (frame: IFrame) => {
      console.log('❌ Notification WebSocket disconnected:', frame);
      this.isConnected$.next(false);
    };

    this.client.onStompError = (frame: IFrame) => {
      console.error('❌ Notification STOMP error:', frame);
      this.isConnected$.next(false);
    };
  }

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.client.active && this.isConnected$.value) {
        resolve();
        return;
      }

      const token = this.authService.getToken();
      if (!token) {
        reject(new Error('No authentication token available'));
        return;
      }

      this.client.connectHeaders = {
        Authorization: `Bearer ${token}`
      };

      this.connectionResolve = resolve;
      this.connectionTimeout = window.setTimeout(() => {
        this.connectionResolve = undefined;
        reject(new Error('Notification WebSocket connection timeout'));
      }, 15000);

      this.client.activate();
    });
  }

  disconnect(): Promise<void> {
    return new Promise((resolve) => {
      if (!this.client.active) {
        resolve();
        return;
      }

      this.subscriptions.forEach((sub) => {
        if (sub.unsubscribe) {
          sub.unsubscribe();
        }
      });
      this.subscriptions = [];

      this.client.deactivate().then(() => {
        console.log('✅ Notification WebSocket disconnected');
        resolve();
      });
    });
  }

  subscribeToNotifications(): Observable<NotificationDTO> {
    if (!this.client.active || !this.isConnected$.value) {
      throw new Error('Notification WebSocket is not connected. Call connect() first.');
    }
    
    const sub = this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
      console.log('📬 Received notification:', message.body);
      const notification = JSON.parse(message.body) as NotificationDTO;
      this.notificationsSubject$.next(notification);
      message.ack();
    });

    this.subscriptions.push(sub);
    return this.notificationsSubject$.asObservable();
  }

  getConnectionStatus(): Observable<boolean> {
    return this.isConnected$.asObservable();
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
