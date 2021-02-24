// interfaces
import { IAerodrome } from '../aerodromes.interface';

// services
import { AerodromesService, endpoint } from './aerodromes.service';

describe('service AerodromesService', () => {

  let httpBackend,
    restangular;

  let aerodrome: IAerodrome = {
    id: 1,
    aerodrome_name: 'test',
    extended_aerodrome_name: 'test',
    aixm_flag: true,
    geometry: {
      type: 'Point',
      coordinates: [
        55.5, 60.5
      ]
    },
    is_default_billing_center: false,
    billing_center: {
      aerodromes: [],
      hq: null,
      id: null,
      invoice_sequence_number: null,
      name: null,
      prefix_invoice_number: null,
      prefix_receipt_number: null,
      receipt_sequence_number: null,
      users: [],
      external_accounting_system_identifier: null,
      iata_invoice_sequence_number: null,
      receipt_cheque_sequence_number: null,
      receipt_wire_sequence_number: null
    },
    aerodrome_category: {
      id: 1,
      category_name: 'test',
      international_passenger_fee_adult: 1,
      international_passenger_fee_child: 1,
      domestic_passenger_fee_adult: 1,
      domestic_passenger_fee_child: 1,
      domestic_fees_currency: {
        id: null,
        currency_code: null,
        currency_name: null,
        country_code: null,
        decimal_places: null,
        allow_updated_from_web: null,
        symbol: null,
        active: null,
        external_accounting_system_identifier: null,
        exchange_rate_target_currency_id: null
      },
      international_fees_currency: {
        id: null,
        currency_code: null,
        currency_name: null,
        country_code: null,
        decimal_places: null,
        allow_updated_from_web: null,
        symbol: null,
        active: null,
        external_accounting_system_identifier: null,
        exchange_rate_target_currency_id: null
      }
    },
    external_accounting_system_identifier: null,
    aerodrome_services: []
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

  it('should be registered', inject((aerodromesService: AerodromesService) => {
    expect(aerodromesService).not.toEqual(null);
  }));

  describe('listing', () => {
    it('should return a list of aerodromes', inject((aerodromesService: AerodromesService) => {

      let list: Array<IAerodrome> = [];

      list.push(aerodrome);

      aerodromesService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}${endpoint}?size=20`)
        .respond(200, list);

      httpBackend.flush();

    }));
  });

  describe('creating a aerodrome', () => {
    it('should work if the sent data is correct', inject((aerodromesService: AerodromesService) => {

      aerodromesService.create(aerodrome);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}${endpoint}`)
        .respond(200, aerodrome);

      httpBackend.flush();

    }));
  });

  describe('updating a aerodrome', () => {
    it('should work if the sent data is correct', inject((aerodromesService: AerodromesService) => {

      aerodrome.id = 1;

      aerodromesService.update(aerodrome, aerodrome.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}${endpoint}/${aerodrome.id}`)
        .respond(200, aerodrome);

      httpBackend.flush();

    }));
  });

  describe('deleting a aerodrome', () => {
    it('should work if the sent data is correct', inject((aerodromesService: AerodromesService) => {

      let id = 1;

      aerodromesService.delete(id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}${endpoint}/${id}`)
        .respond(200, {});

      httpBackend.flush();
    }));
  });

});
