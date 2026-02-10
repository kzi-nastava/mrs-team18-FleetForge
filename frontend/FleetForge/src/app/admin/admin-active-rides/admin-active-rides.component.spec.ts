import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminActiveRidesComponent } from './admin-active-rides.component';

describe('AdminActiveRidesComponent', () => {
  let component: AdminActiveRidesComponent;
  let fixture: ComponentFixture<AdminActiveRidesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminActiveRidesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminActiveRidesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
