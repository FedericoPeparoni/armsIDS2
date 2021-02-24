// interface
import { IAirTrafficData } from '../air-traffic-data.interface';

// service
import { AirTrafficDataService, endpoint } from './air-traffic-data.service';

describe('service AirTrafficDataService', () => {

    let httpBackend,
        restangular;

    let data: IAirTrafficData = {
        start_date: '01-01-2012',
        end_date: '01-01-2018',
        aerodromes: 'FGKB',
        aircraft_types: null,
        mtow_categories: null,
        temporal_group: null,
        billing_centres: null,
        accounts: null,
        flight_types: ['DOMESTIC', 'INTERNATIONAL'],
        flight_scopes: null,
        flight_categories: null,
        flight_rules: null,
        flight_levels: null,
        routes: null,
        sort: 'flight_type',
        group_by: null,
        fiscal_year: false
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

    it('should be registered', inject((airTrafficDataService: AirTrafficDataService) => {
        expect(airTrafficDataService).not.toEqual(null);
    }));

    describe('Generate', () => {
        it('should send parameters and receive flight data', inject((airTrafficDataService: AirTrafficDataService) => {

            airTrafficDataService.create(data);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, data);

            httpBackend.flush();
        }));
    });
});
