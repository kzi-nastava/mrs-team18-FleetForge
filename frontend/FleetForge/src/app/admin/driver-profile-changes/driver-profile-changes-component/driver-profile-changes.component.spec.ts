import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverProfileChangesComponent } from './driver-profile-changes.component';

describe('DriverProfileChangesComponent', () => {
  let component: DriverProfileChangesComponent;
  let fixture: ComponentFixture<DriverProfileChangesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverProfileChangesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverProfileChangesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
