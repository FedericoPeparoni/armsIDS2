// interfaces
import { ICurrency } from '../currency-management.interface';

// service
import { CurrencyManagementService, endpoint } from './currency-management.service';

describe('service CurrencyManagementService', () => {

  let httpBackend,
    restangular;

  let currency: ICurrency = {
    currency_code: 'CAD',
    currency_name: 'Canadian Dollar',
    country_code: {
      id: 1,
      country_code: 'test',
      country_name: 'test',
      aircraft_registration_prefixes: null,
      aerodrome_prefixes: null
    },
    allow_updated_from_web: true,
    decimal_places: 2,
    symbol: '$',
    active: true,
    external_accounting_system_identifier: null,
    exchange_rate_target_currency_id: 1
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

  it('should be registered', inject((currencyManagementService: CurrencyManagementService) => {
    expect(currencyManagementService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should be registered', inject((currencyManagementService: CurrencyManagementService) => {
      expect(currencyManagementService.list).not.toEqual(null);
    }));

    it('should return an array of currencies', inject((currencyManagementService: CurrencyManagementService) => {

      let expectedResponse = Array<ICurrency>();

      currencyManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));
  });

  describe('create', () => {
    it('should be registered', inject((currencyManagementService: CurrencyManagementService) => {
      expect(currencyManagementService.create).not.toEqual(null);
    }));

    it('should create a single currency', inject((currencyManagementService: CurrencyManagementService) => {

      currencyManagementService.create(currency);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, currency);

      httpBackend.flush();

    }));

  });

  describe('update', () => {
    it('should be registered', inject((currencyManagementService: CurrencyManagementService) => {
      expect(currencyManagementService.update).not.toEqual(null);
    }));

    it('should update a single currency', inject((currencyManagementService: CurrencyManagementService) => {

      currency.id = 1;

      currencyManagementService.update(currency, currency.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${currency.id}`)
        .respond(200, currency);

      httpBackend.flush();

    }));
  });

  describe('delete', () => {
    it('should be registered', inject((currencyManagementService: CurrencyManagementService) => {
      expect(currencyManagementService.delete).not.toEqual(null);
    }));

    it('should delete a single aircraft type', inject((currencyManagementService: CurrencyManagementService) => {

      currency.id = 1;

      currencyManagementService.delete(currency.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${currency.id}`)
        .respond(200, null);

      httpBackend.flush();

    }));
  });

});
