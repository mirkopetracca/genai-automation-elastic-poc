import { TestBed } from '@angular/core/testing';

import { DevelopmentsService } from './developments.service';

describe('DevelopmentsService', () => {
  let service: DevelopmentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DevelopmentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
