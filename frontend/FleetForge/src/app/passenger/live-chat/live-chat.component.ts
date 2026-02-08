import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '../../shared/chat/chat-window/chat-window.component';
import { ChatMessageResponseDTO } from '../../shared/dtos/chat.dtos';
import { ChatRestService } from '../../shared/services/chat-rest.service';
import { ChatWebSocketService } from '../../shared/services/chat-websocket.service';
import { Subject } from 'rxjs';
import { takeUntil, switchMap, tap } from 'rxjs/operators';

@Component({
  selector: 'app-passenger-live-chat',
  standalone: true,
  imports: [CommonModule, ChatWindowComponent],
  templateUrl: './live-chat.component.html',
  styleUrl: './live-chat.component.css'
})
export class PassengerLiveChatComponent implements OnInit, OnDestroy {
  chatId: number | null = null;
  messages: ChatMessageResponseDTO[] = [];
  currentUserId = 0;
  isLoading = true;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private chatRestService: ChatRestService,
    private chatWebSocketService: ChatWebSocketService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeChat();
  }

  private async initializeChat(): Promise<void> {
    try {

      const userIdResponse = await this.chatRestService.getUserId().toPromise();
      this.currentUserId = userIdResponse?.userId ?? 0;

      await this.chatWebSocketService.connect();

      const chatResponse = await this.chatRestService
        .getMyChatId()
        .toPromise();

      if (!chatResponse) {
        this.errorMessage = 'Failed to initialize chat';
        this.isLoading = false;
        return;
      }

      this.chatId = chatResponse.chatId;


      const history = await this.chatRestService
        .getChatHistory(this.chatId)
        .toPromise();

      if (history) {
        this.messages = history;
      }


      await this.chatRestService.markChatAsRead(this.chatId).toPromise();

      this.chatWebSocketService
        .subscribeToMessages()
        .pipe(takeUntil(this.destroy$))
        .subscribe((message: ChatMessageResponseDTO) => {
          this.messages = [...this.messages, message];
          this.cdr.markForCheck();
        });

      console.log('✅ Chat initialized successfully');
      this.isLoading = false;
      this.cdr.markForCheck();
    } catch (error) {
      console.error('Failed to initialize chat:', error);
      this.errorMessage =
        error instanceof Error ? error.message : 'Connection failed';
      this.isLoading = false;
      this.cdr.markForCheck();

    }
  }

  handleSend(content: string): void {
    if (!this.chatId) return;
    this.chatWebSocketService.sendMessage(this.chatId, content);
    
    // Refetch messages after sending
    setTimeout(() => {
      this.chatRestService.getChatHistory(this.chatId!).toPromise().then((history) => {
        if (history) {
          this.messages = history;
          this.cdr.markForCheck();
        }
      });
    }, 500);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.chatWebSocketService.disconnect();
  }
}
