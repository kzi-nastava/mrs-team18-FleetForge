import {
  AfterViewChecked,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatMessageResponseDTO } from '../../dtos/chat.dtos';

@Component({
  selector: 'app-chat-window',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent implements AfterViewChecked {
  @Input({ required: true }) title = '';
  @Input() subtitle = '';
  @Input() isOnline = false;
  @Input() placeholder = 'Type your message here...';
  @Input() messages: ChatMessageResponseDTO[] = [];
  @Input() currentUserId = 0;

  @Output() sendMessage = new EventEmitter<string>();

  @ViewChild('messagesContainer')
  private messagesContainer?: ElementRef<HTMLDivElement>;

  draft = '';
  private lastMessageCount = 0;

  ngAfterViewChecked(): void {
    if (this.messages.length !== this.lastMessageCount) {
      this.lastMessageCount = this.messages.length;
      this.scrollToBottom();
    }
  }

  onSend(event?: Event): void {
    if (event) {
      event.preventDefault();
    }

    const trimmed = this.draft.trim();
    if (!trimmed) {
      return;
    }

    this.sendMessage.emit(trimmed);
    this.draft = '';
  }

  isOwn(message: ChatMessageResponseDTO): boolean {
    return message.senderId === this.currentUserId;
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

  trackByMessageId(_: number, message: ChatMessageResponseDTO): number {
    return message.id;
  }

  private scrollToBottom(): void {
    if (!this.messagesContainer) {
      return;
    }

    const element = this.messagesContainer.nativeElement;
    element.scrollTop = element.scrollHeight;
  }
}
