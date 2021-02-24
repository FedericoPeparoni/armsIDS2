// interface
import { ISCInactivityExpiryNotices } from '../sc-inactivity-expiry-notice.interface';

// service
import { ScInactivityExpiryNoticeService, endpoint } from './sc-inactivity-expiry-notice.service';

describe('service ScInactivityExpiryNoticeService', () => {

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

    it('should be registered', inject((scInactivityExpiryNoticeService: ScInactivityExpiryNoticeService) => {
        expect(scInactivityExpiryNoticeService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of inactivityExpiryNotice', inject((scInactivityExpiryNoticeService: ScInactivityExpiryNoticeService) => {

            let expectedResponse = Array<ISCInactivityExpiryNotices>();

            scInactivityExpiryNoticeService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });
});
