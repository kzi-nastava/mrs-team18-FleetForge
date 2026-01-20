import { TestBed } from '@angular/core/testing';
import { CurrentRideComponent } from './current-ride.component';

describe('CurrentRideComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrentRideComponent]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(CurrentRideComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
