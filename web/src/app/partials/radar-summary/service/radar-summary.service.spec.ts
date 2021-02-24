import { RadarSummaryService, endpoint } from './radar-summary.service';

import { IRadarSummaryType } from '../radar-summary.interface';

describe('service RadarSummaryService', () => {

  let httpBackend,
      restangular;

  let radarSummary: IRadarSummaryType = {
      date: '2017-03-12',
      flight_identifier: 'A67',
      day_of_flight: '2017-03-12',
      departure_time: '1000',
      registration: 'T5',
      aircraft_type: 'A345',
      departure_aero_drome: 'Ottawa',
      destination_aero_drome: 'Toronto',
      route: 'test',
      fir_entry_point : 'Y6',
      fir_entry_time: '1200',
      fir_exit_point: 'F6',
      fir_exit_time: '1302',
      flight_rule: 'VFR',
      flight_travel_category: 'Departure',
      entry_coordinate: null,
      exit_coordinate: null
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

  it('should be registered', inject((radarSummaryService: RadarSummaryService) => {
      expect(radarSummaryService).not.toEqual(null);
  }));

  describe('list', () => {

    it('should return an array of radar summaries', inject((radarSummaryService: RadarSummaryService) => {

      let expectedResponse = Array<IRadarSummaryType>();

      radarSummaryService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));
  });

  describe('create', () => {
    it('should create a single radar summary', inject((radarSummaryService: RadarSummaryService) => {

      radarSummaryService.create(radarSummary);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, radarSummary);

      httpBackend.flush();

    }));
  });

  describe('update', () => {
    it('should be registered', inject((radarSummaryService: RadarSummaryService) => {
      expect(radarSummaryService.update).not.toEqual(null);
    }));

    it('should update a single radar summary', inject((radarSummaryService: RadarSummaryService) => {

      radarSummary.id = 1;

      radarSummaryService.update(radarSummary, radarSummary.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${radarSummary.id}`)
        .respond(200, radarSummary);

      httpBackend.flush();

    }));
  });

  describe('delete', () => {
    it('should delete a single radar summary', inject((radarSummaryService: RadarSummaryService) => {

      radarSummary.id = 1;

      radarSummaryService.delete(radarSummary.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${radarSummary.id}`)
        .respond(200, null);

      httpBackend.flush();

    }));
  });

});
