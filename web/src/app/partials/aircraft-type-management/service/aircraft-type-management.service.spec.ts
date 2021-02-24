// service
import { AircraftTypeManagementService } from './aircraft-type-management.service';

// interface
import { IAircraftType } from '../aircraft-type-management.interface';

describe('service AircraftTypeManagementService', () => {

  let httpBackend,
    restangular,
    response;

  const endpoint: string = '/aircraft-types';

  const aircraftTypeEx = {
    id: 1,
    aircraft_type: 'jet',
    aircraft_name: '747',
    manufacturer: 'Boeing',
    wake_turbulence_category: 'L',
    maximum_takeoff_weight: 100
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

      response = null;
    });
  });

  it('should be registered', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {
    expect(aircraftTypeManagementService).not.toEqual(null);
  }));

  describe('methods', () => {

    describe('list', () => {
      it('should be registered', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {
        expect(aircraftTypeManagementService.list).not.toEqual(null);
      }));

      it('should return an array of aircraft types', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {

        let expectedResponse = Array<IAircraftType>();

        aircraftTypeManagementService.list();

        httpBackend
          .expect('GET', `${restangular.configuration.baseUrl}${endpoint}?size=20`)
          .respond(200, expectedResponse);

        httpBackend.flush();

      }));
    });

    describe('create', () => {
      it('should be registered', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {
        expect(aircraftTypeManagementService.create).not.toEqual(null);
      }));

      it('should create a single aircraft type', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {

        let expectedResponse = aircraftTypeEx;

        aircraftTypeManagementService.create(aircraftTypeEx);

        httpBackend
          .expect('POST', `${restangular.configuration.baseUrl}${endpoint}`)
          .respond(200, expectedResponse);

        httpBackend.flush();

      }));

    });

    describe('update', () => {
      it('should be registered', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {
        expect(aircraftTypeManagementService.update).not.toEqual(null);
      }));

      it('should update a single aircraft type', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {

        let expectedResponse = aircraftTypeEx;

        aircraftTypeManagementService.update(aircraftTypeEx, aircraftTypeEx.id);

        httpBackend
          .expect('PUT', `${restangular.configuration.baseUrl}${endpoint}/${aircraftTypeEx.id}`)
          .respond(200, expectedResponse);

        httpBackend.flush();

      }));
    });

    describe('delete', () => {
      it('should be registered', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {
        expect(aircraftTypeManagementService.delete).not.toEqual(null);
      }));

      it('should delete a single aircraft type', inject((aircraftTypeManagementService: AircraftTypeManagementService) => {

        let expectedResponse = aircraftTypeEx;

        aircraftTypeManagementService.delete(aircraftTypeEx.id);

        httpBackend
          .expect('DELETE', `${restangular.configuration.baseUrl}${endpoint}/${aircraftTypeEx.id}`)
          .respond(200, expectedResponse);

        httpBackend.flush();

      }));
    });

  });

});
