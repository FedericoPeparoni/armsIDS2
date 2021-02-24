// interface
import { IRevenueData } from '../revenue-data.interface';

// service
import { RevenueDataService, endpoint } from './revenue-data.service';

describe('service RevenueDataService', () => {

    let httpBackend,
        restangular;

    let data: IRevenueData = {
        start_date: '01-01-2012',
        end_date: '01-01-2018',
        analysis_type: null,
        billing_centres: null,
        accounts: null,
        aerodromes: 'FGKB',
        payment_mode: 'test',
        charge_class: 'test',
        charge_category: 'test',
        charge_type: null,
        temporal_group: 'year',
        group_by: null,
        sort: 'flight Type',
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

    it('should be registered', inject((revenueDataService: RevenueDataService) => {
        expect(revenueDataService).not.toEqual(null);
    }));

    describe('Generate', () => {
        it('should send parameters and receive flight data', inject((revenueDataService: RevenueDataService) => {
            revenueDataService.create(data);
            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, data);
            httpBackend.flush();
        }));
    });
});
