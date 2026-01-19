import { Component, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogoComponent } from '../../shared/logo/logo.component';
import { AuthService } from '../service/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-activate-account',
  standalone: true,
  imports: [LogoComponent, CommonModule],
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.css',
})
export class ActivateAccountComponent {
  token: string | null = null;

  loading = true;
  success = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');

    this.token = this.route.snapshot.queryParamMap.get('token');

    if (!this.token) {
      this.loading = false;
      this.success = false;
      this.errorMessage = 'Invalid or missing activation token.';
      return;
    }

    this.activate();
  }

  activate() {
    this.authService.activateAccount(this.token!).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        this.success = false;
        this.errorMessage =
          err?.error?.message || 'Activation failed. Token may be invalid or expired.';
        this.cdr.detectChanges();
      },
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
