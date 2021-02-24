import { PasswordChangeService } from './password-change.service';

describe('service PasswordChangeService', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;
      });
  });

  it('should be registered', inject((passwordChangeService: PasswordChangeService) => {
      expect(passwordChangeService).not.toEqual(null);
  }));
});
