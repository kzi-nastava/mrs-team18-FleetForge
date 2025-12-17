import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../navigation/navbar/navbar.component';
import { SidebarComponent } from '../../navigation/sidebar/sidebar.component';

@Component({
  standalone: true,
  selector: 'app-main-layout',
  imports: [RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <app-sidebar></app-sidebar>
    <app-navbar></app-navbar>
    <router-outlet></router-outlet>
  `
})
export class MainLayoutComponent {}
