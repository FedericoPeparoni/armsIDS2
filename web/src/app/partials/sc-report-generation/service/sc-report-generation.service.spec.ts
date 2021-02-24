import { ScReportGenerationService } from './sc-report-generation.service';

describe('service ScReportGenerationService', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;
      });
  });

  it('should be registered', inject((scReportGenerationService: ScReportGenerationService) => {
      expect(scReportGenerationService).not.toEqual(null);
  }));
});
