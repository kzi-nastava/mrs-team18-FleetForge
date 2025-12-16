import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-logo',
  templateUrl: './logo.component.html',
  styleUrls: ['./logo.component.css']
})
export class LogoComponent {
  @Input() imageSrc: string = 'logo.png';
  @Input() altText: string = 'FleetForge logo';
  @Input() link: string = '/'; 
  @Input() ariaLabel: string = 'Go to home page';
}