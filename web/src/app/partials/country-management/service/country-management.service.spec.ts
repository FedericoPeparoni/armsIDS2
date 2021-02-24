import { CountryManagementService } from './country-management.service';
import { ICountry } from '../country-management.interface';

describe('service CountryManagementService', () => {

    let httpBackend, restangular, response;

    const endpoint = 'countries';
    const country: ICountry = {
        id: 1,
        country_code: 'CAD',
        country_name: 'Canada',
        aircraft_registration_prefixes: null,
        aerodrome_prefixes: null
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

            response = null;
        });
    });

    it('should be registered', inject((countryManagementService: CountryManagementService) => {
        expect(countryManagementService).not.toEqual(null);
    }));

    describe('method', () => {
        describe('list', () => {
            it('should be registered', inject((countryManagementService: CountryManagementService) => {
                expect(countryManagementService.list).not.toEqual(null);
            }));
            it('should return list of countries', inject((countryManagementService: CountryManagementService) => {

                let response = Array<ICountry>(country);

                countryManagementService.list();

                httpBackend
                    .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                    .respond(200, response);

                httpBackend.flush();
            }));
        });
        describe('create', () => {
            it('should be registered', inject((countryManagementService: CountryManagementService) => {
                expect(countryManagementService.create).not.toEqual(null);
            }));
            it('should add a country, returning that country in response', inject((countryManagementService: CountryManagementService) => {

                let response = country;

                countryManagementService.create(country);

                httpBackend
                    .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                    .respond(200, response);

                httpBackend.flush();
            }));
        });
        describe('update', () => {
            it('should be registered', inject((countryManagementService: CountryManagementService) => {
                expect(countryManagementService.update).not.toEqual(null);
            }));
            it('should update a country, returning that country in response', inject((countryManagementService: CountryManagementService) => {

                countryManagementService.update(country, country.id);

                httpBackend
                    .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${country.id}`)
                    .respond(200, country);

                httpBackend.flush();
            }));
        });
        describe('delete', () => {
            it('should be registered', inject((countryManagementService: CountryManagementService) => {
                expect(countryManagementService.delete).not.toEqual(null);
            }));
            it('should delete a country by country id', inject((countryManagementService: CountryManagementService) => {

                countryManagementService.delete(country.id);

                httpBackend
                    .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${country.id}`)
                    .respond(200, {});

                httpBackend.flush();
            }));
        });
    });
});
