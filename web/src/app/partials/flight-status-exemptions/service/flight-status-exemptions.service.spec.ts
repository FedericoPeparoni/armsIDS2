// service
import { FlightStatusExemptionsService, endpoint } from './flight-status-exemptions.service';

// interface
import { IFlightStatusExemptionType } from '../flight-status-exemptions.interface';

describe('service FlightStatusExemptionsService', () => {

  let httpBackend,
    restangular;

  let flightStatusExemption: IFlightStatusExemptionType = {
    id: 3,
    flight_item_type: 'Item 18 - Statuses',
    flight_item_value: 'Navaid Calibration',
    enroute_fees_are_exempt: 100,
    late_arrival_fees_exempt: 100,
    late_departure_fees_exempt: 50,
    parking_fees_are_exempt: 50,
    international_pax: 100,
    domestic_pax: 100,
    approach_fees_exempt: 0,
    aerodrome_fees_exempt: 0,
    extended_hours: 0,
    flight_notes: 'test notes'
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

  it('should be registered', inject((flightStatusExemptionsService: FlightStatusExemptionsService) => {
    expect(flightStatusExemptionsService).not.toEqual(null);
  }));

  describe('list', () => {

    it('should return an array of accounts with exemptions', inject((flightStatusExemptionsService: FlightStatusExemptionsService) => {

      let expectedResponse = Array<IFlightStatusExemptionType>();

      flightStatusExemptionsService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('create', () => {

    it('should create a single exempt account record', inject((flightStatusExemptionsService: FlightStatusExemptionsService) => {

      flightStatusExemptionsService.create(flightStatusExemption);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, flightStatusExemption);

      httpBackend.flush();

    }));
  });

  describe('update', () => {
    it('should be registered', inject((flightStatusExemptionsService: FlightStatusExemptionsService) => {
      expect(flightStatusExemptionsService.update).not.toEqual(null);
    }));

    it('should update a single exempt account record', inject((flightStatusExemptionsService: FlightStatusExemptionsService) => {

      flightStatusExemption.id = 1;

      flightStatusExemptionsService.update(flightStatusExemption, flightStatusExemption.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${flightStatusExemption.id}`)
        .respond(200, flightStatusExemption);

      httpBackend.flush();

    }));
  });

  describe('delete', () => {

    it('should delete a single exempt account record', inject((flightStatusExemptionsService: FlightStatusExemptionsService) => {

      flightStatusExemption.id = 1;

      flightStatusExemptionsService.delete(flightStatusExemption.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${flightStatusExemption.id}`)
        .respond(200, null);

      httpBackend.flush();

    }));
  });
});
