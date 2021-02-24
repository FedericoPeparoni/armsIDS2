import { ScLoginService } from './sc-login.service';

describe('service ScLoginService', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;
      });
  });

  it('should be registered', inject((scLoginService: ScLoginService) => {
      expect(scLoginService).not.toEqual(null);
  }));
});
