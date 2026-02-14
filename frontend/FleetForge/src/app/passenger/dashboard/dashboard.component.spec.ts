import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerDashboardComponent } from './dashboard.component';

describe('PassengerDashboardComponent', () => {
  let component: PassengerDashboardComponent;
  let fixture: ComponentFixture<PassengerDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerDashboardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
