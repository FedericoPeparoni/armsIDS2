// interfaces
import { ISchedule, IRange } from '../utilities-schedules.interface';

// service
import { UtilitiesSchedulesService, endpoint } from './utilities-schedules.service';

describe('service UtilitiesSchedulesService', () => {

    let httpBackend,
        restangular;

    let schedule: ISchedule = {
        schedule_id: 1,
        schedule_type: 'WATER',
        minimum_charge: 6000,
        utilities_range_bracket: [{
            id: 1,
            schedule_id: 1,
            range_top_end: 500,
            unit_price: 500
        }],
        utilities_water_towns_and_village: [{
            id: 1,
            town_or_village_name: 'Tinseltown',
            water_utility_schedule: null,
            commercial_electricity_utility_schedule: null,
            residential_electricity_utility_schedule: null
        }],
        residential_electricity_utility_schedule: [],
        commercial_electricity_utility_schedule: []
    };

    let range: IRange = {
        id: 1,
        schedule_id: 1,
        range_top_end: 500,
        unit_price: 500
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

    it('should be registered', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {
        expect(utilitiesSchedulesService).not.toEqual(null);
    }));

    describe('list schedules', () => {
        it('should return an array of schedules', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            let expectedResponse = Array<ISchedule>();

            utilitiesSchedulesService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create a schedule', () => {
        it('should create a single schedule', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            utilitiesSchedulesService.create(schedule);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, schedule);

            httpBackend.flush();
        }));
    });

    describe('update a schedule', () => {
        it('should update a single schedule', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            schedule.schedule_id = 1;

            utilitiesSchedulesService.update(schedule, schedule.schedule_id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${schedule.schedule_id}`)
                .respond(200, schedule);

            httpBackend.flush();
        }));
    });

    describe('delete a schedule', () => {
        it('should delete a single aircraft type', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            schedule.schedule_id = 1;

            utilitiesSchedulesService.delete(schedule.schedule_id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${schedule.schedule_id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

    describe('create range brackets', () => {
        it('should create a range bracket', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            utilitiesSchedulesService.createRange(1, range);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/1/range-brackets`)
                .respond(200, range);

            httpBackend.flush();
        }));
    });

    describe('update range brackets', () => {
        it('should update a range bracket', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            utilitiesSchedulesService.updateRange(range);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/range-brackets/1`)
                .respond(200, range);

            httpBackend.flush();
        }));
    });

    describe('delete range brackets', () => {
        it('should delete a range bracket', inject((utilitiesSchedulesService: UtilitiesSchedulesService) => {

            utilitiesSchedulesService.deleteRange(range.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/range-brackets/1`)
                .respond(200, range);

            httpBackend.flush();
        }));
    });


});
