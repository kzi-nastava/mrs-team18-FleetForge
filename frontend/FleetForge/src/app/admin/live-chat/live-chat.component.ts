import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '../../shared/chat/chat-window/chat-window.component';
import { ChatMessageResponseDTO, ChatResponseDTO } from '../../shared/dtos/chat.dtos';

@Component({
  selector: 'app-admin-live-chat',
  standalone: true,
  imports: [CommonModule, ChatWindowComponent],
  templateUrl: './live-chat.component.html',
  styleUrl: './live-chat.component.css'
})
export class AdminLiveChatComponent {
  currentUserId = 999;

  chats: ChatResponseDTO[] = [
    {
      id: 10,
      userId: 101,
      userName: 'Mila Popovic',
      userRole: 'PASSENGER',
      userProfilePicture: null,
      createdAt: new Date(Date.now() - 1000 * 60 * 90).toISOString(),
      lastMessageAt: new Date(Date.now() - 1000 * 60 * 12).toISOString(),
      unreadCount: 2,
      lastMessageContent: 'Can you check my receipt?'
    },
    {
      id: 11,
      userId: 202,
      userName: 'Igor Milic',
      userRole: 'DRIVER',
      userProfilePicture: null,
      createdAt: new Date(Date.now() - 1000 * 60 * 120).toISOString(),
      lastMessageAt: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
      unreadCount: 0,
      lastMessageContent: 'Thanks for the update.'
    },
    {
      id: 12,
      userId: 303,
      userName: 'Jelena Vukovic',
      userRole: 'PASSENGER',
      userProfilePicture: null,
      createdAt: new Date(Date.now() - 1000 * 60 * 240).toISOString(),
      lastMessageAt: new Date(Date.now() - 1000 * 60 * 45).toISOString(),
      unreadCount: 1,
      lastMessageContent: 'My driver is late.'
    }
  ];

  messagesByChatId: Record<number, ChatMessageResponseDTO[]> = {
    10: [
      {
        id: 1,
        chatId: 10,
        senderId: 101,
        senderName: 'Mila Popovic',
        senderRole: 'PASSENGER',
        content: 'Hi, I need a copy of my receipt.',
        sentAt: new Date(Date.now() - 1000 * 60 * 15).toISOString(),
        isRead: true,
        readAt: new Date(Date.now() - 1000 * 60 * 14).toISOString()
      },
      {
        id: 2,
        chatId: 10,
        senderId: 999,
        senderName: 'FleetForge Admin',
        senderRole: 'ADMIN',
        content: 'Sure. Which ride date?',
        sentAt: new Date(Date.now() - 1000 * 60 * 14).toISOString(),
        isRead: true,
        readAt: new Date(Date.now() - 1000 * 60 * 13).toISOString()
      },
      {
        id: 3,
        chatId: 10,
        senderId: 101,
        senderName: 'Mila Popovic',
        senderRole: 'PASSENGER',
        content: 'Yesterday around 6 PM.',
        sentAt: new Date(Date.now() - 1000 * 60 * 12).toISOString(),
        isRead: false,
        readAt: null
      }
    ],
    11: [
      {
        id: 1,
        chatId: 11,
        senderId: 202,
        senderName: 'Igor Milic',
        senderRole: 'DRIVER',
        content: 'Thanks for the update on that pickup.',
        sentAt: new Date(Date.now() - 1000 * 60 * 40).toISOString(),
        isRead: true,
        readAt: new Date(Date.now() - 1000 * 60 * 39).toISOString()
      }
    ],
    12: [
      {
        id: 1,
        chatId: 12,
        senderId: 303,
        senderName: 'Jelena Vukovic',
        senderRole: 'PASSENGER',
        content: 'My driver is late and not responding.',
        sentAt: new Date(Date.now() - 1000 * 60 * 50).toISOString(),
        isRead: false,
        readAt: null
      }
    ]
  };

  selectedChatId = this.chats[0].id;

  get selectedChat(): ChatResponseDTO {
    return this.chats.find((chat) => chat.id === this.selectedChatId) ?? this.chats[0];
  }

  get selectedMessages(): ChatMessageResponseDTO[] {
    return this.messagesByChatId[this.selectedChatId] ?? [];
  }

  selectChat(chatId: number): void {
    this.selectedChatId = chatId;
    const chat = this.chats.find((item) => item.id === chatId);
    if (chat) {
      chat.unreadCount = 0;
    }
  }

  handleSend(content: string): void {
    const chat = this.selectedChat;
    const list = this.selectedMessages;
    const newMessage: ChatMessageResponseDTO = {
      id: list.length + 1,
      chatId: chat.id,
      senderId: this.currentUserId,
      senderName: 'FleetForge Admin',
      senderRole: 'ADMIN',
      content,
      sentAt: new Date().toISOString(),
      isRead: false,
      readAt: null
    };

    this.messagesByChatId = {
      ...this.messagesByChatId,
      [chat.id]: [...list, newMessage]
    };

    chat.lastMessageAt = newMessage.sentAt;
    chat.lastMessageContent = newMessage.content;
  }

  getInitial(name: string): string {
    return name?.trim()?.charAt(0)?.toUpperCase() || '?';
  }

  formatTime(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '';
    }

    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}
