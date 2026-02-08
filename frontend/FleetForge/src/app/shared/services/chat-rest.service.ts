import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatMessageResponseDTO, ChatResponseDTO } from '../dtos/chat.dtos';

@Injectable({
  providedIn: 'root'
})
export class ChatRestService {
  private apiUrl = 'http://localhost:8080/api/chats';

  constructor(private http: HttpClient) {}

  /**
   * Get or create the current user's chat with admin.
   * Used by passenger and driver to get their chat ID.
   */
  getMyChatId(): Observable<{ chatId: number }> {
    return this.http.get<{ chatId: number }>(`${this.apiUrl}/my-chat`);
  }

  /**
   * Get all chats for admin with conversation metadata.
   * Admin only - returns list of chats with unread counts and last message info.
   */
  getAllChats(): Observable<ChatResponseDTO[]> {
    return this.http.get<ChatResponseDTO[]>(this.apiUrl);
  }

  /**
   * Get chat history for a specific chat.
   * Returns all messages in the chat ordered by time.
   */
  getChatHistory(chatId: number): Observable<ChatMessageResponseDTO[]> {
    return this.http.get<ChatMessageResponseDTO[]>(`${this.apiUrl}/${chatId}/messages`);
  }

  /**
   * Get unread message count for a chat.
   * Useful for displaying unread badges.
   */
  getUnreadCount(chatId: number): Observable<{ unreadCount: number }> {
    return this.http.get<{ unreadCount: number }>(`${this.apiUrl}/${chatId}/unread-count`);
  }

  /**
   * Mark all messages in a chat as read.
   * Called when user opens a chat conversation.
   */
  markChatAsRead(chatId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${chatId}/mark-as-read`, {});
  }

  /**
   * Get a specific user's chat (admin only).
   * Used to fetch a chat by user ID.
   */
  getChatByUserId(userId: number): Observable<{ chatId: number }> {
    return this.http.get<{ chatId: number }>(`${this.apiUrl}/user/${userId}`);
  }

  /**
   * Get current authenticated user's ID.
   * Returns userId from JWT token validation.
   */
  getUserId(): Observable<{ userId: number }> {
    return this.http.get<{ userId: number }>(`${this.apiUrl}/userId`);
  }
}
