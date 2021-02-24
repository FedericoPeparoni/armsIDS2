// service
import { AirNavigationChargesService } from './air-navigation-charges.service';

describe('service AirNavigationChargesService', () => {

    let httpBackend,
        restangular;

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

    it('should be registered', inject((airNavigationChargesService: AirNavigationChargesService) => {
        expect(airNavigationChargesService).not.toEqual(null);
    }));

});
