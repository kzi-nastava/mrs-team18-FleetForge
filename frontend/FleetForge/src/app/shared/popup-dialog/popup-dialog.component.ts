import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-popup-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './popup-dialog.component.html',
  styleUrls: ['./popup-dialog.component.css'],
})
export class PopupDialogComponent {
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
