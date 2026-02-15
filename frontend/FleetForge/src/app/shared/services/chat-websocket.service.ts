import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { Client, IFrame, IMessage, StompSubscription } from '@stomp/stompjs';
import { ChatMessageResponseDTO, SendMessageDTO } from '../dtos/chat.dtos';
import { AuthService } from '../../auth/service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ChatWebSocketService implements OnDestroy {
  private client: Client;
  private isConnected$ = new BehaviorSubject<boolean>(false);
  private messagesSubject$ = new Subject<ChatMessageResponseDTO>();
  private adminMessagesSubject$ = new Subject<ChatMessageResponseDTO>();

  private subscriptions: StompSubscription[] = [];
  private subscriptionSet: Set<Subscription> = new Set();
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

  /**
   * Set up STOMP client connection handlers.
   */
  private setupClientHandlers(): void {
    this.client.onConnect = (frame: IFrame) => {
      console.log('✅ WebSocket connected:', frame);
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
      console.log('❌ WebSocket disconnected:', frame);
      this.isConnected$.next(false);
    };

    this.client.onStompError = (frame: IFrame) => {
      console.error('❌ STOMP error:', frame);
      this.isConnected$.next(false);
    };
  }

  /**
   * Connect to WebSocket with JWT token in Authorization header.
   * Must be called before subscribing to any topics/queues.
   */
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

      console.log('📡 Attempting STOMP connection with token:', token.substring(0, 20) + '...');

      this.connectionResolve = resolve;
      this.connectionTimeout = window.setTimeout(() => {
        this.connectionResolve = undefined;
        reject(new Error('WebSocket connection timeout'));
      }, 15000);

      this.client.activate();
    });
  }

  /**
   * Disconnect from WebSocket and clean up all subscriptions.
   */
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

      this.subscriptionSet.forEach((sub) => sub.unsubscribe());
      this.subscriptionSet.clear();

      this.client.deactivate().then(() => {
        console.log('✅ WebSocket disconnected');
        resolve();
      });
    });
  }

  /**
   * Subscribe to personal message queue (/user/queue/messages).
   * Used by passenger/driver to receive messages from admin.
   */
  subscribeToMessages(): Observable<ChatMessageResponseDTO> {
    if (!this.client.active || !this.isConnected$.value) {
      throw new Error('STOMP connection is not active. Call connect() first.');
    }
    
    const sub = this.client.subscribe('/user/queue/messages', (message: IMessage) => {
      const dto = JSON.parse(message.body) as ChatMessageResponseDTO;
      this.messagesSubject$.next(dto);
      message.ack();
    });

    this.subscriptions.push(sub);
    return this.messagesSubject$.asObservable();
  }

  /**
   * Subscribe to admin broadcast queue (/topic/admin/messages).
   * Used by admin to receive messages from passengers/drivers.
   */
  subscribeToAdminMessages(): Observable<ChatMessageResponseDTO> {
    if (!this.client.active || !this.isConnected$.value) {
      throw new Error('STOMP connection is not active. Call connect() first.');
    }
    
    const sub = this.client.subscribe('/topic/admin/messages', (message: IMessage) => {
      const dto = JSON.parse(message.body) as ChatMessageResponseDTO;
      this.adminMessagesSubject$.next(dto);
      message.ack();
    });

    this.subscriptions.push(sub);
    return this.adminMessagesSubject$.asObservable();
  }


  /**
   * Send a message via WebSocket (/app/chat/send).
   * Message is saved to database and broadcast to recipient.
   */
  sendMessage(chatId: number, content: string): void {
    if (!this.client.active || !this.isConnected$.value) {
      console.warn('Cannot send message: STOMP connection is not active');
      return;
    }
    
    const message: SendMessageDTO = {
      chatId,
      content
    };

    this.client.publish({
      destination: '/app/chat/send',
      body: JSON.stringify(message)
    });
  }


  /**
   * Get connection state observable.
   * Subscribe to this to react to connection changes.
   */
  isConnected(): Observable<boolean> {
    return this.isConnected$.asObservable();
  }

  /**
   * Get current connection state synchronously.
   */
  isConnectedValue(): boolean {
    return this.isConnected$.value;
  }

  /**
   * Clean up all subscriptions and disconnect on service destroy.
   */
  ngOnDestroy(): void {
    this.disconnect();
  }
}
