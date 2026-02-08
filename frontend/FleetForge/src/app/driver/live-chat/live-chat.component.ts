import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatWindowComponent } from '../../shared/chat/chat-window/chat-window.component';
import { ChatMessageResponseDTO, ChatResponseDTO } from '../../shared/dtos/chat.dtos';

@Component({
  selector: 'app-driver-live-chat',
  standalone: true,
  imports: [CommonModule, ChatWindowComponent],
  templateUrl: './live-chat.component.html',
  styleUrl: './live-chat.component.css'
})
export class DriverLiveChatComponent {
  currentUserId = 202;

  chat: ChatResponseDTO = {
    id: 2,
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
      chatId: 2,
      senderId: 999,
      senderName: 'FleetForge Admin',
      senderRole: 'ADMIN',
      content: 'Hi driver! Need help with a ride?',
      sentAt: new Date(Date.now() - 1000 * 60 * 8).toISOString(),
      isRead: true,
      readAt: new Date(Date.now() - 1000 * 60 * 7).toISOString()
    },
    {
      id: 2,
      chatId: 2,
      senderId: 202,
      senderName: 'Driver',
      senderRole: 'DRIVER',
      content: 'Yes, I have a question about a pickup location.',
      sentAt: new Date(Date.now() - 1000 * 60 * 6).toISOString(),
      isRead: true,
      readAt: new Date(Date.now() - 1000 * 60 * 5).toISOString()
    },
    {
      id: 3,
      chatId: 2,
      senderId: 999,
      senderName: 'FleetForge Admin',
      senderRole: 'ADMIN',
      content: 'Share the ride ID and we will check it.',
      sentAt: new Date(Date.now() - 1000 * 60 * 4).toISOString(),
      isRead: true,
      readAt: new Date(Date.now() - 1000 * 60 * 3).toISOString()
    }
  ];

  handleSend(content: string): void {
    const newMessage: ChatMessageResponseDTO = {
      id: this.messages.length + 1,
      chatId: this.chat.id,
      senderId: this.currentUserId,
      senderName: 'Driver',
      senderRole: 'DRIVER',
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
