import { FlightScheduleManagementService } from './flight-schedule-management.service';
import { IFlightSchedule } from '../flight-schedule-management.interface';

describe('service FlightScheduleManagementService', () => {
    let httpBackend, restangular, response;
    const endpoint: string = 'flight-schedules';
    const flightSchedule: IFlightSchedule = {
        id: 19,
        account: null,
        flight_service_number: '1092',
        dep_ad: 'FBSK',
        dep_time: '1234',
        dest_ad: 'FBSK',
        dest_time: '1234',
        daily_schedule: 'M',
        self_care: false,
        active_indicator: 'active',
        start_date: null,
        end_date: null,
        missing_flight_movements: null,
        unexpected_flights: null
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

    it('should be registered', inject((flightScheduleManagementService: FlightScheduleManagementService) => {
        expect(flightScheduleManagementService).not.toEqual(null);
    }));

    describe('method', () => {
        describe('list', () => {
            it('should be registered', inject((flightScheduleManagementService: FlightScheduleManagementService) => {
                expect(flightScheduleManagementService.list).not.toEqual(null);
            }));
            it('should return an array of flight schedules', inject((flightScheduleManagementService: FlightScheduleManagementService) => {

                let response = Array<IFlightSchedule>(flightSchedule);

                flightScheduleManagementService.list();

                httpBackend
                    .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                    .respond(200, response);

                httpBackend.flush();
            }));
        });
        describe('create', () => {
            it('should be registered', inject((flightScheduleManagementService: FlightScheduleManagementService) => {
                expect(flightScheduleManagementService.create).not.toEqual(null);
            }));
            it('should create a flight schedule, returning that schedule in response', inject((flightScheduleManagementService: FlightScheduleManagementService) => {

                let response = flightSchedule;

                flightScheduleManagementService.create(flightSchedule);

                httpBackend
                    .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                    .respond(200, response);

                httpBackend.flush();
            }));
        });
        describe('update', () => {
            it('should be registered', inject((flightScheduleManagementService: FlightScheduleManagementService) => {
                expect(flightScheduleManagementService.update).not.toEqual(null);
            }));
            it('should update a flight schedule, returning that flight schedule in response', inject((flightScheduleManagementService: FlightScheduleManagementService) => {

                flightScheduleManagementService.update(flightSchedule, flightSchedule.id);

                httpBackend
                    .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${flightSchedule.id}`)
                    .respond(200, flightSchedule);

                httpBackend.flush();
            }));
        });
        describe('delete', () => {
            it('should be registered', inject((flightScheduleManagementService: FlightScheduleManagementService) => {
                expect(flightScheduleManagementService.delete).not.toEqual(null);
            }));
            it('should delete a flight schedule', inject((flightScheduleManagementService: FlightScheduleManagementService) => {

                flightScheduleManagementService.delete(flightSchedule.id);

                httpBackend
                    .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${flightSchedule.id}`)
                    .respond(200, {});

                httpBackend.flush();
            }));
        });
    });
});
