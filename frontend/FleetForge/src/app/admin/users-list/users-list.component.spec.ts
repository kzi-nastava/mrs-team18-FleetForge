import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { UsersListComponent } from './users-list.component';
import { UsersListService } from '../service/users-list/users-list.service';

describe('UsersListComponent', () => {
  let component: UsersListComponent;
  let fixture: ComponentFixture<UsersListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersListComponent],
      providers: [
        {
          provide: UsersListService,
          useValue: {
            getUsers: () => of({ content: [] }),
            blockUser: () => of({}),
          },
        },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
