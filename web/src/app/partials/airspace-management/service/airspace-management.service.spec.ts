// interface
import { IAirspace } from '../airspace-management.interface';

// service
import { AirspaceManagementService, endpoint } from './airspace-management.service';

describe('service AirspaceManagementService', () => {

  let httpBackend,
    restangular;

  let airspace: IAirspace = {
    airspace_name: 'TEST',
    airspace_type: 'FIR',
    airspace_full_name: 'North Pole',
    airspace_included: true,
    airspace_ceiling: 285.00,
    id: 1
  };

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

  it('should be registered', inject((airspaceManagementService: AirspaceManagementService) => {
    expect(airspaceManagementService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of airspace', inject((airspaceManagementService: AirspaceManagementService) => {

      let expectedResponse = Array<IAirspace>();

      airspaceManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete an airspace', inject((airspaceManagementService: AirspaceManagementService) => {

      airspace.id = 1;

      airspaceManagementService.delete(airspace.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${airspace.id}`)
        .respond(200, null);

      httpBackend.flush();
    }));
  });
});
