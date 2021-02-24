// interface
import { IFlightRouteExemptionType } from '../flight-route-exemptions.interface';

// services
import { FlightRouteExemptionsService, endpoint } from './flight-route-exemptions.service';

describe('service FlightRouteExemptionsService', () => {

  let httpBackend,
    restangular;

  let exemption: IFlightRouteExemptionType = {
    departure_aerodrome: 'BHJB',
    destination_aerodrome: 'NJBJ',
    exemption_in_either_direction: true,
    enroute_fees_are_exempt: 0,
    approach_fees_are_exempt: 0,
    aerodrome_fees_are_exempt: 0,
    late_arrival_fees_are_exempt: 0,
    late_departure_fees_are_exempt: 0,
    parking_fees_are_exempt: 0,
    international_pax: 0,
    domestic_pax: 0,
    extended_hours: 0,
    flight_notes: 'notes',
    exempt_route_floor: 0,
    exempt_route_ceiling: 999
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

  it('should be registered', inject((flightRouteExemptionsService: FlightRouteExemptionsService) => {
    expect(flightRouteExemptionsService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of flight route exemptions', inject((flightRouteExemptionsService: FlightRouteExemptionsService) => {

      let expectedResponse = Array<IFlightRouteExemptionType>();

      flightRouteExemptionsService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('create', () => {
    it('should create a single flight route exemption', inject((flightRouteExemptionsService: FlightRouteExemptionsService) => {

      flightRouteExemptionsService.create(exemption);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, exemption);

      httpBackend.flush();
    }));
  });

  describe('update', () => {
    it('should update a single flight route exemption', inject((flightRouteExemptionsService: FlightRouteExemptionsService) => {

      exemption.id = 1;

      flightRouteExemptionsService.update(exemption, exemption.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${exemption.id}`)
        .respond(200, exemption);

      httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete a single flight route exemption', inject((flightRouteExemptionsService: FlightRouteExemptionsService) => {

      exemption.id = 1;

      flightRouteExemptionsService.delete(exemption.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${exemption.id}`)
        .respond(200, null);

      httpBackend.flush();
    }));
  });

});
