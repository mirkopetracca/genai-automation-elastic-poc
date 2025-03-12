import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DevelopmentDocumentUploadComponent } from './development-document-upload.component';

describe('DevelopmentDocumentUploadComponent', () => {
  let component: DevelopmentDocumentUploadComponent;
  let fixture: ComponentFixture<DevelopmentDocumentUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DevelopmentDocumentUploadComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DevelopmentDocumentUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
