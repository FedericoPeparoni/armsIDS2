// interfaces
import { IExchangeRate } from '../currency-exchange-rates.interface';

// service
import { CurrencyExchangeRatesService, endpoint } from './currency-exchange-rates.service';

describe('service CurrencyExchangeRatesService', () => {

    let httpBackend,
        restangular;

    let rate: IExchangeRate = {
        id: 1,
        currency: {
            id: 1
        },
        currency_code: 'CAD',
        exchange_rate: 0.80,
        exchange_rate_valid_from_date: 'test',
        exchange_rate_valid_to_date: 'test',
        target_currency: null
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

    it('should be registered', inject((currencyExchangeRatesService: CurrencyExchangeRatesService) => {
        expect(currencyExchangeRatesService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of currency exchange rates', inject((currencyExchangeRatesService: CurrencyExchangeRatesService) => {

            let expectedResponse = Array<IExchangeRate>();

            currencyExchangeRatesService.getExchangeRatesByCurrencyId(1, null);

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/for-currency/${rate.currency.id}`)
                .respond(200, expectedResponse);

            httpBackend.flush();

        }));
    });

    describe('exchange rate', () => {
        it('should return an exchange rate between two currenies', inject((currencyExchangeRatesService: CurrencyExchangeRatesService) => {
            let expectedResponse: number = 1;

            currencyExchangeRatesService.getExchangeRateByCurrencyId(1, 1);

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/for-currency/${rate.currency.id}/to/${rate.currency.id}`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single currency exchange rate', inject((currencyExchangeRatesService: CurrencyExchangeRatesService) => {

            currencyExchangeRatesService.create(rate);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, rate);

            httpBackend.flush();

        }));

    });

    describe('update', () => {
        it('should update a single currency exchange rate', inject((currencyExchangeRatesService: CurrencyExchangeRatesService) => {

            currencyExchangeRatesService.update(rate, rate.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${rate.id}`)
                .respond(200, rate);

            httpBackend.flush();

        }));
    });

    describe('delete', () => {
        it('should delete a single currency exchange rate', inject((currencyExchangeRatesService: CurrencyExchangeRatesService) => {

            currencyExchangeRatesService.delete(rate.currency.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${rate.id}`)
                .respond(200, null);

            httpBackend.flush();

        }));
    });

});
