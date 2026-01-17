import { Component } from '@angular/core';

@Component({
  selector: 'app-register',
  imports: [],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  ngOnInit() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }
}
