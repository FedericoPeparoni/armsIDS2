// service
import { AircraftExemptManagementService, endpoint } from './aircraft-exempt-management.service';

// interface
import { IAircraftExemptType } from '../aircraft-exempt-management.interface';

describe('service AircraftExemptManagementService', () => {

  let httpBackend,
    restangular;

  let exemptAircraft: IAircraftExemptType = {
    aircraft_type: 1,
    enroute_fees_exempt: null,
    late_arrival_fees_exempt: null,
    late_departure_fees_exempt: null,
    parking_fees_exempt: null,
    flight_notes: 'test',
    approach_fees_exempt: null,
    aerodrome_fees_exempt: null,
    international_pax: null,
    domestic_pax: null,
    extended_hours_fees_exempt: null
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

  it('should be registered', inject((aircraftExemptManagementService: AircraftExemptManagementService) => {
    expect(aircraftExemptManagementService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of exempt aircrafts', inject((aircraftExemptManagementService: AircraftExemptManagementService) => {

      let expectedResponse = Array<IAircraftExemptType>();

      aircraftExemptManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('create', () => {
    it('should create a single exempt aircraft', inject((aircraftExemptManagementService: AircraftExemptManagementService) => {

      aircraftExemptManagementService.create(exemptAircraft);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, exemptAircraft);

      httpBackend.flush();
    }));
  });

  describe('update', () => {
    it('should update a single exempt aircraft', inject((aircraftExemptManagementService: AircraftExemptManagementService) => {

      exemptAircraft.id = 1;

      aircraftExemptManagementService.update(exemptAircraft, exemptAircraft.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${exemptAircraft.id}`)
        .respond(200, exemptAircraft);

      httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete a single exempt aircraft', inject((aircraftExemptManagementService: AircraftExemptManagementService) => {

      exemptAircraft.id = 1;

      aircraftExemptManagementService.delete(exemptAircraft.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${exemptAircraft.id}`)
        .respond(200, null);

      httpBackend.flush();
    }));
  });

});


