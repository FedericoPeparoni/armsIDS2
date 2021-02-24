// interfaces
import { IPassengerServiceChargeReturn } from '../sc-passenger-service-charge-return.interface';

// service
import { ScPassengerServiceChargeReturnService, endpoint } from './sc-passenger-service-charge-return.service';

describe('service ScPassengerServiceChargeReturnService', () => {

  let httpBackend,
      restangular;

  let passengerServiceChargeReturn: IPassengerServiceChargeReturn = {
    flight_id: 'test',
    day_of_flight: 'test',
    departure_time: 'test',
    transit_passengers: 1,
    joining_passengers: 1,
    children: 1,
    chargeable_itl_passengers: 1,
    chargeable_domestic_passengers: 1,
    document_filename: '',
    mime_type: '',
    document: null,
    document_filename2: null,
    arriving_pax_domestic_airport: null,
    landing_pax_domestic_airport: null,
    transfer_pax_domestic_airport: null,
    departing_pax_domestic_airport: null,
    arriving_child_domestic_airport: null,
    landing_child_domestic_airport: null,
    transfer_child_domestic_airport: null,
    departing_child_domestic_airport: null,
    exempt_arriving_pax_domestic_airport: null,
    exempt_landing_pax_domestic_airport: null,
    exempt_transfer_pax_domestic_airport: null,
    exempt_departing_pax_domestic_airport: null,
    loaded_goods: null,
    discharged_goods: null,
    loaded_mail: null,
    discharged_mail: null,
    account: null,
    account_id: null
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

  it('should be registered', inject((scPassengerServiceChargeReturnService: ScPassengerServiceChargeReturnService) => {
      expect(scPassengerServiceChargeReturnService).not.toEqual(null);
  }));


    describe('list', () => {
        it('should return an array of passenger service charge return', inject((scPassengerServiceChargeReturnService: ScPassengerServiceChargeReturnService) => {

            let expectedResponse = Array<IPassengerServiceChargeReturn>();

            scPassengerServiceChargeReturnService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

  describe('create', () => {
    it('should create a single passenger service charge return', inject((scPassengerServiceChargeReturnService: ScPassengerServiceChargeReturnService) => {

      scPassengerServiceChargeReturnService.create(passengerServiceChargeReturn);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, passengerServiceChargeReturn);

      httpBackend.flush();

    }));
  });

  describe('update', () => {
    it('should be registered', inject((scPassengerServiceChargeReturnService: ScPassengerServiceChargeReturnService) => {
      expect(scPassengerServiceChargeReturnService.update).not.toEqual(null);
    }));

    it('should update a passenger service charge return', inject((scPassengerServiceChargeReturnService: ScPassengerServiceChargeReturnService) => {

      passengerServiceChargeReturn.id = 1;

      scPassengerServiceChargeReturnService.update(passengerServiceChargeReturn, passengerServiceChargeReturn.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${passengerServiceChargeReturn.id}`)
        .respond(200, passengerServiceChargeReturn);

      httpBackend.flush();

    }));
  });

  describe('delete', () => {
    it('should delete a single passenger service charge', inject((scPassengerServiceChargeReturnService: ScPassengerServiceChargeReturnService) => {

      passengerServiceChargeReturn.id = 1;

      scPassengerServiceChargeReturnService.delete(passengerServiceChargeReturn.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${passengerServiceChargeReturn.id}`)
        .respond(200, null);

      httpBackend.flush();

    }));
  });

});
