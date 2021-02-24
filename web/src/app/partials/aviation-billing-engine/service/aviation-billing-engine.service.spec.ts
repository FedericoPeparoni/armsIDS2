// interface
import { IAviationBillingEngine, IIncompleteFlights, IJobStatus } from '../aviation-billing-engine.interface';

// service
import { AviationBillingEngineService, endpoint } from './aviation-billing-engine.service';

describe('service AviationBillingEngineService', () => {

    let httpBackend,
        restangular;

    let aviationBillingEngine: IAviationBillingEngine = {
        month: '5',
        account_id_list: null,
        year: 2017,
        flights: null,
        incompleteFlights: null,
        iata_status: 'iata',
        sort: 'account,dateTime',
        userBillingCenterOnly: 'false',
        billing_interval: null,
        end_date: null,
        start_date: null,
        processStartDate: '2017-08-01T00:00:00.000Z',
        processEndDate: '2017-08-31T00:00:00.000Z',
        preview: null,
        endDateInclusive: null,
        flightCategory: 0,
        account_type: null
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

    it('should be registered', inject((aviationBillingEngineService: AviationBillingEngineService) => {
        expect(aviationBillingEngineService).not.toEqual(null);
    }));

    describe('validation', () => {
        it('should return an array of incomplete flight information', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse = Array<IIncompleteFlights>();

            aviationBillingEngineService.validate(aviationBillingEngine, aviationBillingEngine.processStartDate, aviationBillingEngine.processEndDate);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/flightmovements/validations/byParams?endDateInclusive=` +
                `${aviationBillingEngine.processEndDate}&flightCategory=${aviationBillingEngine.flightCategory}&iata=true&startDate=${aviationBillingEngine.processStartDate}&userBillingCenterOnly=` +
                `${aviationBillingEngine.userBillingCenterOnly}`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    // tests for bulk reconcile

    describe('execute reconciliation', () => {
        it('should reconcile flight movements selected by period', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse;

            aviationBillingEngineService.executeRecalulation(aviationBillingEngine);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/reconciliation/${aviationBillingEngine.processStartDate}/${aviationBillingEngine.processEndDate}/`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('cancel reconciliation', () => {
        it('should cancel the reconciliation process', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse;

            aviationBillingEngineService.cancelRecalculation();

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/reconciliation`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('get reconciliation status', () => {
        it('should get the job object', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse = Array<IJobStatus>();

            aviationBillingEngineService.getStatusForRecalulations();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/reconciliation`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('execute specific reconciliation', () => {
        it('should reconcile flight movements by selected id', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse;
            let id = '3';

            aviationBillingEngineService.executeSpecificReconciliation(id);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/reconciliation/item/${id}`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('cancel specific reconciliation', () => {
        it('should cancel the reconciliation process', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse;

            aviationBillingEngineService.cancelSpecificReconciliation();

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/reconciliation/item`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('get specific reconciliation status', () => {
        it('should get the job object', inject((aviationBillingEngineService: AviationBillingEngineService) => {

            let expectedResponse = Array<IJobStatus>();

            aviationBillingEngineService.getStatusForSpecificReconciliation();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/reconciliation/item`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });
});
