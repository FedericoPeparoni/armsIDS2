// interface
import { ISystemSummary, ISystemSummaryValue, ISystemSummaryFlightCategiriesValue } from '../system-summary.interface';

import { SystemSummaryService, endpoint } from './system-summary.service';

describe('service SystemSummaryService', () => {

    let httpBackend,
        restangular;

    const systemSummaryValue: ISystemSummaryValue = {
        total: 10,
        val: 4,
        percent: 40,
        name: 'International Arrivals',
        date: null
    };

    const systemSummaryFlightCategoriesValue: ISystemSummaryFlightCategiriesValue = {
      category_name: 'DOMESTIC/NATIONAL',
      flight_movement_vo: systemSummaryValue
  };

    const systemSummary: ISystemSummary = {
        flight_movement_aircraft_type: systemSummaryValue,
        flight_movement_all: systemSummaryValue,
        flight_movement_blacklisted_account: systemSummaryValue,
        flight_movement_blacklisted_movement: systemSummaryValue,
        flight_movement_categories: systemSummaryFlightCategoriesValue,
        flight_movement_domestic_active_account: systemSummaryValue,
        flight_movement_international_active_account: systemSummaryValue,
        flight_movement_latest: systemSummaryValue,
        flight_movement_inside: systemSummaryValue,
        flight_movement_outside: systemSummaryValue,
        flight_movement_parking_time_domestic: systemSummaryValue,
        flight_movement_parking_time_internationa_arrivals: systemSummaryValue,
        flight_movement_parking_time_total: systemSummaryValue,
        flight_movement_rejected: systemSummaryValue,
        flight_movement_unknown_aircraft_type: systemSummaryValue,
        outstanding_bill: systemSummaryValue,
        overdue_bill: systemSummaryValue
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

    it('should be registered', inject((systemSummaryService: SystemSummaryService) => {
        expect(systemSummaryService).not.toEqual(null);
    }));


    describe('list', () => {
        it('should return an object of system summary values', inject((systemSummaryService: SystemSummaryService) => {

            let expectedResponse = [systemSummary];

            systemSummaryService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

});

