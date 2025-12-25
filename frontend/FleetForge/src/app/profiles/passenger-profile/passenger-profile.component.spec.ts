import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerProfileComponent } from './passenger-profile.component';

describe('PassengerProfileComponent', () => {
  let component: PassengerProfileComponent;
  let fixture: ComponentFixture<PassengerProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerProfileComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
