// interfaces
import { IAerodromeCategory } from '../aerodrome-category-management.interface';

// services
import { AerodromeCategoryManagementService, endpoint } from './aerodrome-category-management.service';

describe('service AerodromeCategoryManagementService', () => {

  let httpBackend,
      restangular;

  let aerodromeCategory: IAerodromeCategory = {
    id: 1,
    category_name: 'test',
    international_passenger_fee_adult: 1.1,
    international_passenger_fee_child: 1.2,
    domestic_passenger_fee_adult: 1.3,
    domestic_passenger_fee_child: 1.4,
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

  it('should be registered', inject((aerodromeCategoryManagementService: AerodromeCategoryManagementService) => {
      expect(aerodromeCategoryManagementService).not.toEqual(null);
  }));

  describe('listing', () => {
    it('should return a list of aerodromes', inject((aerodromeCategoryManagementService: AerodromeCategoryManagementService) => {

      let list: Array<IAerodromeCategory> = [];

      list.push(aerodromeCategory);

      aerodromeCategoryManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, list);

      httpBackend.flush();

    }));
  });

  describe('creating an aerodrome category', () => {
    it('should work if the sent data is correct', inject((aerodromeCategoryManagementService: AerodromeCategoryManagementService) => {

      aerodromeCategoryManagementService.create(aerodromeCategory);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, aerodromeCategory);

      httpBackend.flush();

    }));
  });

  describe('updating an aerodrome category', () => {
    it('should work if the sent data is correct', inject((aerodromeCategoryManagementService: AerodromeCategoryManagementService) => {

      aerodromeCategory.id = 1;

      aerodromeCategoryManagementService.update(aerodromeCategory, aerodromeCategory.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${aerodromeCategory.id}`)
        .respond(200, aerodromeCategory);

      httpBackend.flush();

    }));
  });

  describe('deleting an aerodrome category', () => {
    it('should work if the sent data is correct', inject((aerodromeCategoryManagementService: AerodromeCategoryManagementService) => {

      let id = 1;

      aerodromeCategoryManagementService.delete(id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${id}`)
        .respond(200, {});

      httpBackend.flush();
    }));
  });

});
