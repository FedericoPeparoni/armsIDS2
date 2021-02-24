// service
import { NominalRoutesService, endpoint } from './nominal-routes.service';

// interface
import { INominalType } from '../nominal-routes.interface';

describe('service NominalRoutesService', () => {

    let httpBackend,
        restangular;

    let nominalRoute: INominalType = {
        type: 'FIR / FIR',
        pointa: 'PointA',
        pointb: 'PointB',
        nominal_distance: 155.50,
        bi_directional: true,
        nominal_route_floor: 0,
        nominal_route_ceiling: 999
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

    it('should be registered', inject((nominalRoutesService: NominalRoutesService) => {
        expect(nominalRoutesService).not.toEqual(null);
    }));

    describe('list', () => {
            it('should return an array of nominal routes', inject((nominalRoutesService: NominalRoutesService) => {

                let expectedResponse = Array<INominalType>();

                nominalRoutesService.list();

                httpBackend
                    .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                    .respond(200, expectedResponse);

                httpBackend.flush();
            }));
    });

    describe('create', () => {
          it('should create a single nominal route', inject((nominalRoutesService: NominalRoutesService) => {

              nominalRoutesService.create(nominalRoute);

              httpBackend
                  .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                  .respond(200, nominalRoute);

              httpBackend.flush();
          }));
    });

    describe('update', () => {
          it('should update a single nominal route', inject((nominalRoutesService: NominalRoutesService) => {

              nominalRoute.id = 1;

              nominalRoutesService.update(nominalRoute, nominalRoute.id);

              httpBackend
                  .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${nominalRoute.id}`)
                  .respond(200, nominalRoute);

              httpBackend.flush();
          }));
    });

    describe('delete', () => {
          it('should delete a single nominal route', inject((nominalRoutesService: NominalRoutesService) => {

              nominalRoute.id = 1;

              nominalRoutesService.delete(nominalRoute.id);

              httpBackend
                  .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${nominalRoute.id}`)
                  .respond(200, null);

              httpBackend.flush();
          }));
    });
});
