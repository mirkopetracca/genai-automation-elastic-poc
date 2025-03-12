import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevelopmentCodeComponent } from './development-code.component';

describe('DevelopmentCodeComponent', () => {
  let component: DevelopmentCodeComponent;
  let fixture: ComponentFixture<DevelopmentCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DevelopmentCodeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevelopmentCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
