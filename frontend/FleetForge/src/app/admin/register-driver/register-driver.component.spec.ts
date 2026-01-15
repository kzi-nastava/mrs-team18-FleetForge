import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterDriverComponent } from './register-driver.component';

describe('RegisterDriverComponent', () => {
  let component: RegisterDriverComponent;
  let fixture: ComponentFixture<RegisterDriverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterDriverComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterDriverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
