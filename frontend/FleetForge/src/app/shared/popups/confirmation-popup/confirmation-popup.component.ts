import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ConfirmButtonColor = 'red' | 'green';

@Component({
  selector: 'app-confirmation-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmation-popup.component.html',
  styleUrls: ['./confirmation-popup.component.css'],
})
export class ConfirmationPopupComponent {
  @Input() visible = false;

  @Input() title: string = 'Confirm Action';
  @Input() message: string = '';

  @Input() cancelButtonText: string = 'Cancel';
  @Input() confirmButtonText: string = 'Confirm';

  @Input() confirmButtonColor: ConfirmButtonColor = 'green';

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  onConfirm() {
    this.confirmed.emit();
  }

  onCancel() {
    this.cancelled.emit();
  }
}
