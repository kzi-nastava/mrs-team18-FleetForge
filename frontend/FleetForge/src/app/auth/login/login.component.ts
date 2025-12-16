import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { LogoComponent } from '../../shared/logo/logo.component';
import { UserService } from '../../profiles/service/user-service';

@Component({
  selector: 'app-login',
  imports: [LogoComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  constructor(private userService: UserService, private router: Router) {}

  goToProfile(): void {
    const user = this.userService.user();
    if (user?.id === 2) {
      this.router.navigate(['profile-driver']);
    } else {
      this.router.navigate(['profile']);
    }
  }
}
