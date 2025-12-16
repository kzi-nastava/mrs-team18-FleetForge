import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

export interface NavItem {
  label: string;
  path: string;
  exact: boolean;
}

@Component({
  selector: 'app-navbar',
  imports: [RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  standalone: true,
})
export class NavbarComponent {
  navItems: NavItem[] = [
    { label: 'Home', path: '/', exact: true },
    { label: 'Map', path: '/map', exact: false },
    { label: 'Contact', path: '/contact', exact: false },
  ];
}
