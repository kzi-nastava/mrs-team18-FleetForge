import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '../../shared/chat/chat-window/chat-window.component';
import { ChatMessageResponseDTO, ChatResponseDTO } from '../../shared/dtos/chat.dtos';

@Component({
  selector: 'app-passenger-live-chat',
  standalone: true,
  imports: [CommonModule, ChatWindowComponent],
  templateUrl: './live-chat.component.html',
  styleUrl: './live-chat.component.css'
})
export class PassengerLiveChatComponent {
  currentUserId = 101;

  chat: ChatResponseDTO = {
    id: 1,
    userId: 999,
    userName: 'FleetForge Admin',
    userRole: 'ADMIN',
    userProfilePicture: null,
    createdAt: new Date().toISOString(),
    lastMessageAt: new Date().toISOString(),
    unreadCount: 0,
    lastMessageContent: 'Welcome to live support.'
  };

  messages: ChatMessageResponseDTO[] = [
    {
      id: 1,
      chatId: 1,
      senderId: 999,
      senderName: 'FleetForge Admin',
      senderRole: 'ADMIN',
      content: 'Hello! How can we help you today?',
      sentAt: new Date(Date.now() - 1000 * 60 * 6).toISOString(),
      isRead: true,
      readAt: new Date(Date.now() - 1000 * 60 * 5).toISOString()
    },
    {
      id: 2,
      chatId: 1,
      senderId: 101,
      senderName: 'Passenger',
      senderRole: 'PASSENGER',
      content: 'I have a question about my last ride.',
      sentAt: new Date(Date.now() - 1000 * 60 * 4).toISOString(),
      isRead: true,
      readAt: new Date(Date.now() - 1000 * 60 * 3).toISOString()
    },
    {
      id: 3,
      chatId: 1,
      senderId: 999,
      senderName: 'FleetForge Admin',
      senderRole: 'ADMIN',
      content: 'Sure. Can you share the ride date and time?',
      sentAt: new Date(Date.now() - 1000 * 60 * 2).toISOString(),
      isRead: true,
      readAt: new Date(Date.now() - 1000 * 60).toISOString()
    }
  ];

  handleSend(content: string): void {
    const newMessage: ChatMessageResponseDTO = {
      id: this.messages.length + 1,
      chatId: this.chat.id,
      senderId: this.currentUserId,
      senderName: 'Passenger',
      senderRole: 'PASSENGER',
      content,
      sentAt: new Date().toISOString(),
      isRead: false,
      readAt: null
    };

    this.messages = [...this.messages, newMessage];
    this.chat.lastMessageAt = newMessage.sentAt;
    this.chat.lastMessageContent = newMessage.content;
  }
}
