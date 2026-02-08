export interface SendMessageDTO {
  chatId: number;
  content: string;
}

export interface ChatMessageResponseDTO {
  id: number;
  chatId: number;
  senderId: number;
  senderName: string;
  senderRole: string;
  content: string;
  sentAt: string;
  isRead: boolean;
  readAt?: string | null;
}

export interface ChatResponseDTO {
  id: number;
  userId: number;
  userName: string;
  userRole: string;
  userProfilePicture: string | null;
  createdAt: string;
  lastMessageAt: string;
  unreadCount: number;
  lastMessageContent: string;
}
