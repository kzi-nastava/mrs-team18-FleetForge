import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { DriverDashboardService, UpcomingRideDTO, DailyStatsDTO } from '../service/driver-dashboard/driver-dashboard.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-driver-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-dashboard.component.html',
  styleUrls: ['./driver-dashboard.component.css']
})
export class DriverDashboardComponent implements OnInit {
  isAvailable: boolean = true;
  upcomingRides: (UpcomingRideDTO & { canStart: boolean })[] = [];
  dailyStats: DailyStatsDTO = {
    totalEarnings: 0,
    hoursWorked: 0,
    ridesCompleted: 0
  };
  private hasLoaded = false;

  constructor(
    private router: Router,
    private dashboardService: DriverDashboardService,
    private cdr: ChangeDetectorRef
  ) {
    // Refetch when navigating back to this route
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe((e) => {
        const ev = e as NavigationEnd;
        if (ev.urlAfterRedirects.includes('/driver/dashboard')) {
          if (this.hasLoaded) {
            this.loadDashboardData();
          }
        }
      });
  }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.dashboardService.getDashboardData().subscribe({
      next: ({ upcomingRides, dailyStats }) => {
        this.dailyStats = dailyStats;
        this.upcomingRides = upcomingRides.map(ride => ({
          ...ride,
          canStart: this.canStartRide(ride.startTime)
        }));
        this.hasLoaded = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load dashboard data:', err);
      }
    });
  }

  canStartRide(startTime: string): boolean {
    const rideStart = new Date(startTime);
    const now = new Date();
    const diffMinutes = (rideStart.getTime() - now.getTime()) / (1000 * 60);
    return diffMinutes <= 10;
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  }

  getTimeUntilRide(startTime: string): string {
    const rideStart = new Date(startTime);
    const now = new Date();
    const diffMinutes = Math.floor((rideStart.getTime() - now.getTime()) / (1000 * 60));
    
    if (diffMinutes < 0) return 'Starting now';
    if (diffMinutes === 0) return 'Starting now';
    if (diffMinutes < 60) return `in ${diffMinutes} min`;
    
    const hours = Math.floor(diffMinutes / 60);
    const mins = diffMinutes % 60;
    return `in ${hours}h ${mins}m`;
  }

  goToCurrentRide(): void {
    this.router.navigate(['/driver/current-ride']);
  }

  toggleAvailability(): void {
    this.isAvailable = !this.isAvailable;
    // TODO: Call API to update driver availability
  }
}
