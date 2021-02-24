// interface
import { IRegionalCountry } from '../regional-country-management.interface';

// service
import { RegionalCountryManagementService, endpoint } from './regional-country-management.service';

describe('service RegionalCountryManagementService', () => {

    let httpBackend,
        restangular;

    let country: IRegionalCountry = {
        id: null,
        country: {
            id: 4,
            country_code: 'AFG',
            country_name: 'Afganistan',
            aircraft_registration_prefixes: null,
            aerodrome_prefixes: null
        }
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

    it('should be registered', inject((regionalCountryManagementService: RegionalCountryManagementService) => {
        expect(regionalCountryManagementService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of regional countries', inject((regionalCountryManagementService: RegionalCountryManagementService) => {

            let expectedResponse = Array<IRegionalCountry>();

            regionalCountryManagementService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();

        }));
    });

    describe('update', () => {
        it('should be registered', inject((regionalCountryManagementService: RegionalCountryManagementService) => {
            expect(regionalCountryManagementService.update).not.toEqual(null);
        }));

        it('should update the list of regional countries', inject((regionalCountryManagementService: RegionalCountryManagementService) => {

            country.id = 1;

            regionalCountryManagementService.update([country]);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, country);

            httpBackend.flush();

        }));
    });

});










