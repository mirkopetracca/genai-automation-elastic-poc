import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevelopmentDetailComponent } from './development-detail.component';

describe('DevelopmentDetailComponent', () => {
  let component: DevelopmentDetailComponent;
  let fixture: ComponentFixture<DevelopmentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DevelopmentDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevelopmentDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
