import { ScCreditPaymentService, endpoint } from './sc-credit-payment.service';
import { IScCreditPayment } from '../sc-credit-payment.interface';

describe('service ScCreditPaymentService', () => {

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

    it('should be registered', inject((scCreditPaymentService: ScCreditPaymentService) => {
        expect(scCreditPaymentService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of credit payment transactions', inject((scCreditPaymentService: ScCreditPaymentService) => {

            let expectedResponse = Array<IScCreditPayment>();

            scCreditPaymentService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });
});
