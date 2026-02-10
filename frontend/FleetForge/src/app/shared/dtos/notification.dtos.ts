import { NotificationType } from '../enums/notification-type.enum';

export interface NotificationDTO {
  id: number;
  type: NotificationType;
  message: string;
  isRead: boolean;
  createdAt: string;
  rideId: number | null;
}
