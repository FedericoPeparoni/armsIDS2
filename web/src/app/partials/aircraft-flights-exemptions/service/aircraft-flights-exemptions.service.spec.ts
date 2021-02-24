// service
import { AircraftFlightsExemptionsService, endpoint } from './aircraft-flights-exemptions.service';

// interface
import { IAircraftFlightsExemptions } from '../aircraft-flights-exemptions.interface';

describe('service AircraftFlightsExemptionsService', () => {

  let httpBackend,
    restangular;

  const exemptAircraft: IAircraftFlightsExemptions = {
    aircraft_registration: 'TEST123',
    flight_id: null,
    enroute_fees_exempt: 0,
    approach_fees_exempt: 0,
    aerodrome_fees_exempt: 0,
    late_arrival_fees_exempt: 0,
    late_departure_fees_exempt: 0,
    parking_fees_exempt: 0,
    international_pax: 0,
    domestic_pax: 0,
    extended_hours: 0,
    flight_notes: 'test',
    exemption_start_date: null,
    exemption_end_date: null
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

  it('should be registered', inject((aircraftFlightsExemptionsService: AircraftFlightsExemptionsService) => {
    expect(aircraftFlightsExemptionsService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of exempt aircraft and flights', inject((aircraftFlightsExemptionsService: AircraftFlightsExemptionsService) => {

      let expectedResponse = Array<IAircraftFlightsExemptions>();

      aircraftFlightsExemptionsService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('create', () => {
    it('should create a single exempt aircraft and flights', inject((aircraftFlightsExemptionsService: AircraftFlightsExemptionsService) => {

      aircraftFlightsExemptionsService.create(exemptAircraft);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, exemptAircraft);

      httpBackend.flush();
    }));
  });

  describe('update', () => {
    it('should update a single exempt aircraft and flights', inject((aircraftFlightsExemptionsService: AircraftFlightsExemptionsService) => {

      exemptAircraft.id = 1;

      aircraftFlightsExemptionsService.update(exemptAircraft, exemptAircraft.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${exemptAircraft.id}`)
        .respond(200, exemptAircraft);

      httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete a single exempt aircraft and flights', inject((aircraftFlightsExemptionsService: AircraftFlightsExemptionsService) => {

      exemptAircraft.id = 1;

      aircraftFlightsExemptionsService.delete(exemptAircraft.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${exemptAircraft.id}`)
        .respond(200, null);

      httpBackend.flush();
    }));
  });

});



