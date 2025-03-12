import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevelopmentListComponent } from './development-list.component';

describe('DevelopmentListComponent', () => {
  let component: DevelopmentListComponent;
  let fixture: ComponentFixture<DevelopmentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DevelopmentListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevelopmentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
