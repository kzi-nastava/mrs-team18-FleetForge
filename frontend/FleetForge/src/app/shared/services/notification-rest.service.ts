import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NotificationDTO } from '../dtos/notification.dtos';

@Injectable({
  providedIn: 'root'
})
export class NotificationRestService {
  private apiUrl = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient) {}

  /**
   * Get all notifications for the current user
   */
  getNotifications(): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(this.apiUrl);
  }

  /**
   * Get unread notification count
   */
  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`);
  }

  /**
   * Mark a single notification as read
   */
  markAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${notificationId}/read`, {});
  }

  /**
   * Mark all notifications as read
   */
  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/read-all`, {});
  }
}
