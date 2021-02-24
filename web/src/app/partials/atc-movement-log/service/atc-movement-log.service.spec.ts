// interface
import { IATCMovementLogType } from '../atc-movement-log.interface';

// service
import { AtcMovementLogService, endpoint } from './atc-movement-log.service';

describe('service AtcMovementLogService', () => {

    let httpBackend,
        restangular;

    let atc_log: IATCMovementLogType = {
      date_of_contact: '2017-Mar-07',
      registration: 'R456',
      operator_identifier: '167',
      route: 'ABCD/DEF',
      flight_id: 'A123',
      aircraft_type: 'A320',
      departure_aerodrome: 'Ottawa',
      destination_aerodrome: 'Ottawa',
      fir_entry_point: 'Z7',
      fir_entry_time: '1010',
      fir_mid_point: 'Z7',
      fir_mid_time: '1010',
      fir_exit_point: 'Z7',
      fir_exit_time: '1010',
      flight_level: '1000',
      wake_turbulence: 'L',
      flight_category: 'sched',
      flight_type: 'normal',
      day_of_flight: '2017-Mar-07',
      departure_time: '1010'
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

    it('should be registered', inject((atcMovementLogService: AtcMovementLogService) => {
        expect(atcMovementLogService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of atc movement logs', inject((atcMovementLogService: AtcMovementLogService) => {

            let expectedResponse = Array<IATCMovementLogType>();

            atcMovementLogService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single atc movement log', inject((atcMovementLogService: AtcMovementLogService) => {

            atcMovementLogService.create(atc_log);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, atc_log);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single atc movement log', inject((atcMovementLogService: AtcMovementLogService) => {

            atc_log.id = 1;

            atcMovementLogService.update(atc_log, atc_log.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${atc_log.id}`)
                .respond(200, atc_log);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single atc movement log', inject((atcMovementLogService: AtcMovementLogService) => {

            atc_log.id = 1;

            atcMovementLogService.delete(atc_log.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${atc_log.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
