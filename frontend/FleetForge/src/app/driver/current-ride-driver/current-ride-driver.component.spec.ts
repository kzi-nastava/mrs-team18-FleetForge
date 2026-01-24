import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentRideDriverComponent } from './current-ride-driver.component';

describe('CurrentRideDriverComponent', () => {
  let component: CurrentRideDriverComponent;
  let fixture: ComponentFixture<CurrentRideDriverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrentRideDriverComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentRideDriverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
