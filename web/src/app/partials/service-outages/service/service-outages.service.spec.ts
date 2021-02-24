// service
import { ServiceOutagesService, endpoint } from './service-outages.service';

// interface
import { IServiceOutages } from '../service-outages.interface';

describe('service ServiceOutagesService', () => {

    let httpBackend, restangular;

    const aerodromeServiceOutage: IServiceOutages = {
        aerodrome: null,
        aerodrome_service_type: null,
        start_date_time: null,
        end_date_time: null,
        approach_discount_type: 'percentage',
        approach_discount_amount: 50,
        aerodrome_discount_type: 'percentage',
        aerodrome_discount_amount: 50,
        flight_notes: 'Test'
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

  it('should be registered', inject((serviceOutagesService: ServiceOutagesService) => {
        expect(serviceOutagesService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of aerodrome service outages', inject((serviceOutagesService: ServiceOutagesService) => {

        let expectedResponse = Array<IServiceOutages>();

        serviceOutagesService.list();

        httpBackend
            .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
            .respond(200, expectedResponse);

        httpBackend.flush();
    }));
  });

  describe('create', () => {
    it('should create a single aerodrome service outage', inject((serviceOutagesService: ServiceOutagesService) => {

        serviceOutagesService.create(aerodromeServiceOutage);

        httpBackend
            .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
            .respond(200, aerodromeServiceOutage);

        httpBackend.flush();
    }));
  });

  describe('update', () => {
    it('should update a single aerodrome service outage', inject((serviceOutagesService: ServiceOutagesService) => {

        aerodromeServiceOutage.id = 1;

        serviceOutagesService.update(aerodromeServiceOutage, aerodromeServiceOutage.id);

        httpBackend
            .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${aerodromeServiceOutage.id}`)
            .respond(200, aerodromeServiceOutage);

        httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete a single aerodrome service outage', inject((serviceOutagesService: ServiceOutagesService) => {

        aerodromeServiceOutage.id = 1;

        serviceOutagesService.delete(aerodromeServiceOutage.id);

        httpBackend
            .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${aerodromeServiceOutage.id}`)
            .respond(200, null);

        httpBackend.flush();
    }));
  });
});
