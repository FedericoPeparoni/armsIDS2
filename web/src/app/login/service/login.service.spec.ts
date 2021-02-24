import { LoginService } from './login.service';

describe('service login', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
    inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
      httpBackend = $httpBackend;
      restangular = Restangular;
    });
  });

  it('should be registered', inject((loginService: LoginService) => {
    expect(loginService).not.toEqual(null);
  }));

  describe('submit function', () => {
    it('should return data', inject((loginService: LoginService) => {

      /*let expectedResponse = {};

      loginService.login('admin', 'admin');

      httpBackend
        .expect('POST', restangular.configuration.baseUrl + '/authentication/login')
        .respond(200, expectedResponse );

      httpBackend.flush();*/

    }));

  });

});
