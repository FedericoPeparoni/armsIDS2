// service
import { ScInvoicesService, endpoint } from './sc-invoices.service';

// interface
import { IInvoice } from '../../invoices/invoices.interface';

describe('service ScInvoicesService', () => {

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

    it('should be registered', inject((scInvoicesService: ScInvoicesService) => {
        expect(scInvoicesService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of invoices', inject((scInvoicesService: ScInvoicesService) => {

            let expectedResponse = Array<IInvoice>();

            scInvoicesService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });
});
