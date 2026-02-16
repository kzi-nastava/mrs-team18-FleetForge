import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverReportsComponent } from './driver-reports.component';

describe('DriverReportsComponent', () => {
  let component: DriverReportsComponent;
  let fixture: ComponentFixture<DriverReportsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverReportsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverReportsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
