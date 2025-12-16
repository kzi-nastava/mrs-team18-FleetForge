import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileDriverComponent } from './user-profile-driver.component';

describe('UserProfileDriverComponent', () => {
  let component: UserProfileDriverComponent;
  let fixture: ComponentFixture<UserProfileDriverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserProfileDriverComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileDriverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
