import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notification-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-popup.component.html',
  styleUrls: ['./notification-popup.component.css'],
})
export class NotificationPopupComponent {
  @Input() visible = false;              
  @Input() title: string = '';           
  @Input() message: string = '';         
  @Input() success: boolean = true;      
  @Input() okButtonText: string = 'OK';  

  @Output() closed = new EventEmitter<void>();

  close() {
    this.closed.emit();
  }
}
