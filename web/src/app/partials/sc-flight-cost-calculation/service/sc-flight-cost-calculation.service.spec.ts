// interface
import { IScFlightCostCalculation } from '../sc-flight-cost-calculation.interface';

// service
import { ScFlightCostCalculationService, endpoint } from './sc-flight-cost-calculation.service';

describe('service ScFlightCostCalculationService', () => {

  let httpBackend,
    restangular;

  let flight: IScFlightCostCalculation = {
    aircraft_type: 'A105',
    registration_number: 'BOT6767',
    speed: 'N1515',
    estimated_elapsed_time: '0100',
    dep_aerodrome: 'FALA',
    dest_aerodrome: 'HKJK',
    route: 'DCT'
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

  it('should be registered', inject((scFlightCostCalculationService: ScFlightCostCalculationService) => {
    expect(scFlightCostCalculationService).not.toEqual(null);
  }));

  describe('calculate flight cost', () => {
    it('should calculate flight cost', inject((scFlightCostCalculationService: ScFlightCostCalculationService) => {

      scFlightCostCalculationService.calculate(flight);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, flight);

      httpBackend.flush();
    }));
  });
});
