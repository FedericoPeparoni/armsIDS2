// service
import { UnspecifiedDepDestLocationsService, endpoint } from './unspecified-dep-dest-locations.service';

// interface
import { IUnspecifiedDepartureLocationType } from '../unspecified-dep-dest-locations.interface';

describe('service UnspecifiedDepDestLocationsService', () => {

  let httpBackend,
      restangular;

  let uddlocation: IUnspecifiedDepartureLocationType = {
    id: null,
    text_identifier: null,
    name: null,
    maintained: null,
    aerodrome_identifier: null,
    latitude: null,
    longitude: null,
    status: null
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

  it('should be registered', inject((unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService) => {
      expect(unspecifiedDepDestLocationsService).not.toEqual(null);
  }));

  describe('list', () => {
      it('should return an array of unspecified departure/destination locations', inject((unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService) => {

          let expectedResponse = Array<IUnspecifiedDepartureLocationType>();

          unspecifiedDepDestLocationsService.list();

          httpBackend
              .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
              .respond(200, expectedResponse);

          httpBackend.flush();
      }));
  });

  describe('create', () => {
      it('should create a single unspecified departure/destination location', inject((unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService) => {

          unspecifiedDepDestLocationsService.create(uddlocation);

          httpBackend
              .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
              .respond(200, uddlocation);

          httpBackend.flush();
      }));
  });

  describe('update', () => {
      it('should update a single unspecified departure/destination location', inject((unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService) => {

          uddlocation.id = 1;

          unspecifiedDepDestLocationsService.update(uddlocation, uddlocation.id);

          httpBackend
              .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${uddlocation.id}`)
              .respond(200, uddlocation);

          httpBackend.flush();
      }));
  });

  describe('delete', () => {
      it('should delete a single unspecified departure/destination location', inject((unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService) => {

          uddlocation.id = 1;

          unspecifiedDepDestLocationsService.delete(uddlocation.id);

          httpBackend
              .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${uddlocation.id}`)
              .respond(200, null);

          httpBackend.flush();
      }));
  });

});
