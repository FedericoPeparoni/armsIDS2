// service
import { InterestRatesService, endpoint } from './interest-rates.service';

// interface
import { IInterestRate } from '../interest-rates.interface';

describe('service InterestRatesService', () => {

    let httpBackend, restangular;

    const interestRate: IInterestRate = {
        default_interest_specification: 'MONTHLY',
        default_interest_application: 'MONTHLY',
        default_interest_grace_period: 0,
        default_foreign_interest_specified_percentage: 15.0,
        default_national_interest_specified_percentage: 25.0,
        default_foreign_interest_applied_percentage: 0.15,
        default_national_interest_applied_percentage: 0.25,
        punitive_interest_specification: 'MONTHLY',
        punitive_interest_application: 'MONTHLY',
        punitive_interest_grace_period: 10,
        punitive_interest_specified_percentage: 35.0,
        punitive_interest_applied_percentage: 0.35,
        start_date: null,
        end_date: null
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

    it('should be registered', inject((interestRatesService: InterestRatesService) => {
        expect(interestRatesService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of interest rates', inject((interestRatesService: InterestRatesService) => {

            let expectedResponse = Array<IInterestRate>();
            interestRatesService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single interest rate', inject((interestRatesService: InterestRatesService) => {

            interestRatesService.create(interestRate);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, interestRate);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single interest rate', inject((interestRatesService: InterestRatesService) => {

            interestRate.id = 1;
            interestRatesService.update(interestRate, interestRate.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${interestRate.id}`)
                .respond(200, interestRate);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single interest rate', inject((interestRatesService: InterestRatesService) => {

            interestRate.id = 1;
            interestRatesService.delete(interestRate.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${interestRate.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });
});
