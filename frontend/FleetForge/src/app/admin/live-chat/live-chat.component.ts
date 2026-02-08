import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '../../shared/chat/chat-window/chat-window.component';
import { ChatMessageResponseDTO, ChatResponseDTO } from '../../shared/dtos/chat.dtos';
import { ChatRestService } from '../../shared/services/chat-rest.service';
import { ChatWebSocketService } from '../../shared/services/chat-websocket.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-admin-live-chat',
  standalone: true,
  imports: [CommonModule, ChatWindowComponent],
  templateUrl: './live-chat.component.html',
  styleUrl: './live-chat.component.css'
})
export class AdminLiveChatComponent implements OnInit, OnDestroy {
  chats: ChatResponseDTO[] = [];
  messagesByChatId: Record<number, ChatMessageResponseDTO[]> = {};
  selectedChatId = 0;
  currentUserId = 0;
  isLoading = true;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  get selectedChat(): ChatResponseDTO | undefined {
    return this.chats.find((chat) => chat.id === this.selectedChatId);
  }

  get selectedMessages(): ChatMessageResponseDTO[] {
    return this.messagesByChatId[this.selectedChatId] ?? [];
  }

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
      console.log('✅ Connected to WebSocket');

      // Load all chats for admin
      const allChats = await this.chatRestService
        .getAllChats()
        .toPromise();

      if (!allChats || allChats.length === 0) {
        this.errorMessage = 'No conversations available';
        this.isLoading = false;
        this.cdr.markForCheck();
        return;
      }

      this.chats = allChats;
      this.selectedChatId = allChats[0].id;

      // Load messages for the first chat
      await this.loadChatHistory(this.selectedChatId);

      // Subscribe to incoming messages from passengers/drivers
      this.chatWebSocketService
        .subscribeToAdminMessages()
        .pipe(takeUntil(this.destroy$))
        .subscribe((message: ChatMessageResponseDTO) => {
          const chatId = message.chatId;

          if (!this.messagesByChatId[chatId]) {
            this.messagesByChatId[chatId] = [];
          }
          this.messagesByChatId[chatId] = [
            ...this.messagesByChatId[chatId],
            message
          ];

          const chat = this.chats.find((c) => c.id === chatId);
          if (chat) {
            chat.lastMessageAt = message.sentAt;
            chat.lastMessageContent = message.content;
            chat.unreadCount++;
          }
          this.cdr.markForCheck();
        });

      this.isLoading = false;
      this.cdr.markForCheck();
    } catch (error) {
      console.error('Failed to initialize admin chat:', error);
      this.errorMessage =
        error instanceof Error ? error.message : 'Connection failed';
      this.isLoading = false;
      this.cdr.markForCheck();
    }
  }

  selectChat(chatId: number): void {
    this.selectedChatId = chatId;
    const chat = this.chats.find((item) => item.id === chatId);
    if (chat) {
      chat.unreadCount = 0;
    }
    this.cdr.markForCheck();
    this.loadChatHistory(chatId);
  }

  private async loadChatHistory(chatId: number): Promise<void> {
    try {
      if (this.messagesByChatId[chatId]) {
        return;
      }

      const messages = await this.chatRestService
        .getChatHistory(chatId)
        .toPromise();

      if (messages) {
        this.messagesByChatId = {
          ...this.messagesByChatId,
          [chatId]: messages
        };
        this.cdr.markForCheck();
      }
    } catch (error) {
      console.error('Failed to load chat history:', error);
    }
  }

  handleSend(content: string): void {
    if (!this.selectedChat) return;
    this.chatWebSocketService.sendMessage(this.selectedChat.id, content);
    
    setTimeout(() => {
      this.chatRestService.getChatHistory(this.selectedChat!.id).toPromise().then((history) => {
        if (history) {
          this.messagesByChatId[this.selectedChatId] = history;
          this.cdr.markForCheck();
        }
      });
    }, 500);
  }

  getInitial(name: string): string {
    return name?.trim()?.charAt(0)?.toUpperCase() || '?';
  }

  formatTime(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '';
    }

    return date.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.chatWebSocketService.disconnect();
  }
}
