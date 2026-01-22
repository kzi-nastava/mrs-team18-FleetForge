import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerFavoriteRoutesComponent } from './passenger-favorite-routes.component';

describe('PassengerFavoriteRoutesComponent', () => {
  let component: PassengerFavoriteRoutesComponent;
  let fixture: ComponentFixture<PassengerFavoriteRoutesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerFavoriteRoutesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerFavoriteRoutesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
