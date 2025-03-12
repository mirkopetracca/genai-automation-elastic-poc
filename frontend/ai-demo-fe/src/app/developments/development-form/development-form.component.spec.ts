import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevelopmentFormComponent } from './development-form.component';

describe('DevelopmentFormComponent', () => {
  let component: DevelopmentFormComponent;
  let fixture: ComponentFixture<DevelopmentFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DevelopmentFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevelopmentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
