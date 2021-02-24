// service
import { ScFlightSearchService, endpoint } from './sc-flight-search.service';

// interface
import { ISCFlightSearch } from '../sc-flight-search.interface';

describe('service ScFlightSearchService', () => {

    let httpBackend,
        restangular;

    const flight: ISCFlightSearch = {
        id: null,
        account: 'Test',
        item18_reg_num: 'Test',
        flight_id: 'Test',
        date_of_flight: '2020-05-07',
        status: 'PENDING',
        dep_time: '2020',
        dep_ad: 'TEST',
        dest_ad: 'TEST',
        total_charges_usd: null,
        amount_prepaid: null,
        flight_notes: null
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

    it('should be registered', inject((scFlightSearchService: ScFlightSearchService) => {
        expect(scFlightSearchService).not.toEqual(null);
    }));

    describe('list', () => {
        it('getting list of flights', inject((scFlightSearchService: ScFlightSearchService) => {

            let flightsList: Array<ISCFlightSearch> = [];

            flightsList.push(flight);

            scFlightSearchService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, flightsList);

            httpBackend.flush();

        }));
    });
});
