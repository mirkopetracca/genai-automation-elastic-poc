import { TestBed } from '@angular/core/testing';

import { DevelopmentDocumentsService } from '../developments/development-documents.service';

describe('DevelopmentDocumentsService', () => {
  let service: DevelopmentDocumentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DevelopmentDocumentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
