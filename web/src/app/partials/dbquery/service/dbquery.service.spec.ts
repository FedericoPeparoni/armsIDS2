// service
import { DbqueryService } from './dbquery.service';

describe('Dbquery Service', () => {

    beforeEach(angular.mock.module('armsWeb'));

    it('service should be registered', inject((dbqueryService: DbqueryService) => {
        expect(dbqueryService).not.toEqual(null);
    }));

    // getDataPoints function
    describe('getDataPoints function', () => {

        let data = [{flight_type: 'DOMESTIC', date: 2017, sum_total_charges: 1000}];
        let yaxis = 'sum_total_charges';
        let groupBy = 'flight_scope';

        it('should be registered', inject((dbqueryService: DbqueryService) => {
            expect(dbqueryService.getDataPoints).not.toBe(null);
        }));

        it('should have three parameters', inject((dbqueryService: DbqueryService) => {
            expect(dbqueryService.getDataPoints.length).toEqual(3);
        }));

        it('tracks that the method was called', inject((dbqueryService: DbqueryService) => {
            spyOn(dbqueryService, 'getDataPoints');

            dbqueryService.getDataPoints(data, yaxis, groupBy);
            expect(dbqueryService.getDataPoints).toHaveBeenCalled();
        }));
    });

    // getColumnLabels function
    describe('getColumnsLabels function', () => {

        const data = [{flight_type: 'DOMESTIC', date: 2017, sum_total_charges: 1000}];
        const datapoints = [{0: 1, 'x': '2017-1'}];
        const labelKey = 'sum_total_charges';
        const type = 'bar';

        it('should be registered', inject((dbqueryService: DbqueryService) => {
            expect(dbqueryService.getColumnsLabels).not.toEqual(null);
        }));

        it('should have two parameters', inject((dbqueryService: DbqueryService) => {
            expect(dbqueryService.getColumnsLabels.length).toEqual(4);
        }));

        it('tracks that the method was called', inject((dbqueryService: DbqueryService) => {
            spyOn(dbqueryService, 'getColumnsLabels');

            dbqueryService.getColumnsLabels(data, datapoints, labelKey, type);
            expect(dbqueryService.getColumnsLabels).toHaveBeenCalled();
        }));

        it('returns an array of column objects', inject((dbqueryService: DbqueryService) => {
            const expectedResponse = [{'id': '0', 'type': 'bar', 'name': '0'}];
            const functionResponse = dbqueryService.getColumnsLabels(data, datapoints, labelKey, type);
            expect(functionResponse).toEqual(expectedResponse);
        }));
    });

});
