// service
import { ApplicationManagementService, endpoint } from './application-management.service';

// interface
import { IRouteCache } from '../application-management.interface';

describe('service ApplicationManagementService', () => {

    let httpBackend,
        restangular;

    let routeCache: IRouteCache = {
        count: 10,
        number_of_retention: 100
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

    it('should be registered', inject((applicationManagementService: ApplicationManagementService) => {
        expect(applicationManagementService).not.toEqual(null);
    }));

    describe('list entries', () => {
        it('get route cache entries', inject((applicationManagementService: ApplicationManagementService) => {

            applicationManagementService.list(<IRouteCache>{});

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, routeCache);

            httpBackend.flush();
        }));
    });

    describe('list number to retain', () => {
        it('get route cache retention', inject((applicationManagementService: ApplicationManagementService) => {

            applicationManagementService.getNumberToRetain();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/retention`)
                .respond(200, routeCache);

            httpBackend.flush();
        }));
    });

    describe('update number to retain', () => {
        it('should update the route caching retention', inject((applicationManagementService: ApplicationManagementService) => {

            let numberToRetain = 101;

            applicationManagementService.updateNumberToRetain(numberToRetain);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/retention/${numberToRetain}`)
                .respond(200, routeCache);

            httpBackend.flush();

        }));
    });

    describe('clear cache', () => {
        it('should clear the cached entries', inject((applicationManagementService: ApplicationManagementService) => {

            applicationManagementService.clearCurrentCache();

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/reset`)
                .respond(200, routeCache);

            httpBackend.flush();

        }));
    });

});
