import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminHistoryComponent } from './admin-history.component';

describe('AdminHistoryComponent', () => {
  let component: AdminHistoryComponent;
  let fixture: ComponentFixture<AdminHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminHistoryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
