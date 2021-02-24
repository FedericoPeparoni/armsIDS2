// service
import { ScFlightSchedulesService, endpoint } from './sc-flight-schedules.service';

// interface
import { IFlightSchedule } from '../../flight-schedule-management/flight-schedule-management.interface';

describe('service ScFlightSchedulesService', () => {

    let httpBackend,
        restangular,
        response;

    const flightSchedule: IFlightSchedule = {
        id: 25,
        account: null,
        flight_service_number: 'SF25',
        dep_ad: 'FBSK',
        dep_time: '1234',
        dest_ad: 'FBSK',
        dest_time: '1234',
        daily_schedule: 'M',
        self_care: true,
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

    it('should be registered', inject((scFlightSchedulesService: ScFlightSchedulesService) => {
        expect(scFlightSchedulesService).not.toEqual(null);
    }));

    describe('method', () => {
        describe('list', () => {
            it('should return an array of flight schedules for self-care accounts', inject((scFlightSchedulesService: ScFlightSchedulesService) => {

                let response = Array<IFlightSchedule>(flightSchedule);

                scFlightSchedulesService.list();

                httpBackend
                    .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                    .respond(200, response);

                httpBackend.flush();
            }));
        });
        describe('create', () => {
            it('should create a flight schedule, returning that schedule in response', inject((scFlightSchedulesService: ScFlightSchedulesService) => {

                let response = flightSchedule;

                scFlightSchedulesService.create(flightSchedule);

                httpBackend
                    .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                    .respond(200, response);

                httpBackend.flush();
            }));
        });
        describe('update', () => {
            it('should update a flight schedule, returning that flight schedule in response', inject((scFlightSchedulesService: ScFlightSchedulesService) => {

                scFlightSchedulesService.update(flightSchedule, flightSchedule.id);

                httpBackend
                    .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${flightSchedule.id}`)
                    .respond(200, flightSchedule);

                httpBackend.flush();
            }));
        });
        describe('delete', () => {
            it('should delete a flight schedule', inject((scFlightSchedulesService: ScFlightSchedulesService) => {

                scFlightSchedulesService.delete(flightSchedule.id);

                httpBackend
                    .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${flightSchedule.id}`)
                    .respond(200, {});

                httpBackend.flush();
            }));
        });
        describe('upload', () => {
          it('should upload a file', inject((scFlightSchedulesService: ScFlightSchedulesService) => {

            const blob = new Blob();
            let formData = new FormData();
            formData.append('file', blob, 'file_name');

            scFlightSchedulesService.upload('PUT', formData, null, null);

            httpBackend
              .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/upload`)
              .respond(200, {});

            httpBackend.flush();

          }));
        });
    });
});

