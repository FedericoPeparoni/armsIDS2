import { SystemConfigurationService, endpoint } from './system-configuration.service';
import { ISystemConfiguration, ISystemConfigurationSpring } from '../system-configuration.interface';

describe('service SystemConfigurationService', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;

          $httpBackend.when('GET', (url: string): any => {
            if ('http://localhost:8080/api/system-configurations/noauth') {
              return true;
            };
          }).respond('ok');
      });
  });

  it('should be registered', inject((systemConfigurationService: SystemConfigurationService) => {
      expect(systemConfigurationService).not.toEqual(null);
  }));

  describe('list method', () => {

    it('should be registered', inject((systemConfigurationService: SystemConfigurationService) => {
      expect(systemConfigurationService.list).not.toEqual(null);
    }));

    it('should return system configuration settings', inject((systemConfigurationService: SystemConfigurationService) => {

      let expectedResponse: ISystemConfigurationSpring = {
        content: []
      };

      systemConfigurationService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=-1`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));

  });

  describe('update method', () => {

    it('should be registered', inject((systemConfigurationService: SystemConfigurationService) => {
      expect(systemConfigurationService.update).not.toEqual(null);
    }));

    it('should update system configuration settings', inject((systemConfigurationService: SystemConfigurationService) => {

      let expectedResponse = Array<ISystemConfiguration>();

      systemConfigurationService.update(expectedResponse, null);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));
  });

});
