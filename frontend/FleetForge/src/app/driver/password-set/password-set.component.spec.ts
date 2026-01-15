import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordSetComponent } from './password-set.component';

describe('PasswordSetComponent', () => {
  let component: PasswordSetComponent;
  let fixture: ComponentFixture<PasswordSetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PasswordSetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PasswordSetComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
