import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserInformationDTO } from '../../shared/dtos/users.dtos';
import { UsersListService } from '../service/users-list/users-list.service';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css'],
})
export class UsersListComponent {
  searchEmail = '';
  searchFirstName = '';
  searchLastName = '';
  blockReason = '';

  users = signal<UserInformationDTO[]>([]);
  isLoading = signal(false);
  currentPage = signal(0);
  totalPages = signal(0);
  isBlockDialogOpen = signal(false);
  isBlocking = signal(false);
  selectedUserToBlock = signal<UserInformationDTO | null>(null);

  blockedUserIds = signal<Set<number>>(new Set<number>());

  constructor(private usersListService: UsersListService) {}

  searchUsers(): void {
    this.isLoading.set(true);
    this.currentPage.set(0);

    this.usersListService.getUsers(this.searchEmail.trim(), 0).subscribe({
      next: (response) => {
        for(const user of response.content) {
          if(user.blocked) {
            this.blockedUserIds.update((ids) => {
              const updated = new Set(ids);
              updated.add(user.id);
              return updated;
            });
          }
        }
        this.users.set(response.content);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);

      },
      error: () => {
        this.users.set([]);
        this.isLoading.set(false);
      },
    });
  }

  blockUser(user: UserInformationDTO): void {
    if (this.isBlocked(user.id)) {
      return;
    }

    this.selectedUserToBlock.set(user);
    this.blockReason = '';
    this.isBlockDialogOpen.set(true);
  }

  closeBlockDialog(): void {
    if (this.isBlocking()) {
      return;
    }

    this.isBlockDialogOpen.set(false);
    this.selectedUserToBlock.set(null);
    this.blockReason = '';
  }

  confirmBlockUser(): void {
    const user = this.selectedUserToBlock();
    const reason = this.blockReason.trim();

    if (!user || !reason || this.isBlocked(user.id) || this.isBlocking()) {
      return;
    }

    this.isBlocking.set(true);

    this.usersListService.blockUser(user.id, reason).subscribe({
      next: () => {
        this.blockedUserIds.update((ids) => {
          const updated = new Set(ids);
          updated.add(user.id);
          return updated;
        });

        this.isBlocking.set(false);
        this.closeBlockDialog();
      },
      error: () => {
        this.isBlocking.set(false);
      },
    });
  }

  isBlocked(userId: number): boolean {
    return this.blockedUserIds().has(userId);
  }

  loadPage(page: number): void {
    this.isLoading.set(true);

    this.usersListService.getUsers(
        this.searchEmail.trim(),
        page
    ).subscribe({
        next: (response) => {
            this.users.set(response.content ?? []);
            this.totalPages.set(response.totalPages);
            this.currentPage.set(page);
            this.isLoading.set(false);
        },
        error: () => {
            this.users.set([]);
            this.isLoading.set(false);
        }
    });
}
  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
        this.loadPage(this.currentPage() + 1);
    }
}

prevPage(): void {
    if (this.currentPage() > 0) {
        this.loadPage(this.currentPage() - 1);
    }
}
  ngOnInit(): void {
    this.searchUsers();
  }
}
