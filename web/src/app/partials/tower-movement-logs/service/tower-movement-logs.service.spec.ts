// interface
import { ITowerMovementLogType } from '../tower-movement-logs.interface';

// service
import { TowerMovementLogsService, endpoint } from './tower-movement-logs.service';

describe('service TowerMovementLogsService', () => {

    let httpBackend,
        restangular;

    let towerLog: ITowerMovementLogType = {
      date_of_contact: '2016-11-11',
      flight_id: '100',
      registration: 'AFMN',
      aircraft_type: '100D',
      operator_name: 'FXBR',
      departure_aerodrome: 'ABCD',
      departure_contact_time: '1234',
      destination_aerodrome: 'ABCD',
      destination_contact_time: '1234',
      route: 'ABDGDGD',
      flight_level: '15000',
      flight_crew: 14,
      passengers: 200,
      flight_category: 'sch',
      day_of_flight: '2016-11-11',
      departure_time: '1234'
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

    it('should be registered', inject((towerMovementLogsService: TowerMovementLogsService) => {
        expect(towerMovementLogsService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of tower movement logs', inject((towerMovementLogsService: TowerMovementLogsService) => {

            let expectedResponse = Array<ITowerMovementLogType>();

            towerMovementLogsService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single tower movement log', inject((towerMovementLogsService: TowerMovementLogsService) => {

            towerMovementLogsService.create(towerLog);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, towerLog);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single tower movement log', inject((towerMovementLogsService: TowerMovementLogsService) => {

            towerLog.id = 1;

            towerMovementLogsService.update(towerLog, towerLog.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${towerLog.id}`)
                .respond(200, towerLog);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single tower movement log', inject((towerMovementLogsService: TowerMovementLogsService) => {

            towerLog.id = 1;

            towerMovementLogsService.delete(towerLog.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${towerLog.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
