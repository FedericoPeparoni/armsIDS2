
// interfaces
import { IEnrouteAirNavigationCharge } from '../enroute-air-navigation-charges-management.interface';
// service
import { EnrouteAirNavigationChargesManagementService, endpoint } from './enroute-air-navigation-charges-management.service';

describe('service EnrouteAirNavigationChargesManagementService', () => {

  let httpBackend,
    restangular;

  let enrouteAirNavigationCharge: IEnrouteAirNavigationCharge = {
    id: null,
    mtow_category_upper_limit: 1,
    w_factor_formula: null,
    enroute_air_navigation_charge_formulas: null
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

  it('should be registered', inject((enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService) => {
    expect(enrouteAirNavigationChargesManagementService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of Enroute Air Navigation Charges', inject((enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService) => {

      let expectedResponse = Array<IEnrouteAirNavigationCharge>();

      enrouteAirNavigationChargesManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('create', () => {
    it('should create a formula for Enroute Air Navigation Charges', inject((enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService) => {

      enrouteAirNavigationChargesManagementService.create(enrouteAirNavigationCharge);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, enrouteAirNavigationCharge);

      httpBackend.flush();
    }));
  });

  describe('update', () => {
    it('should update a formula for Enroute Air Navigation Charges', inject((enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService) => {

      enrouteAirNavigationCharge.id = 1;

      enrouteAirNavigationChargesManagementService.update(enrouteAirNavigationCharge, enrouteAirNavigationCharge.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${enrouteAirNavigationCharge.id}`)
        .respond(200, enrouteAirNavigationCharge);

      httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete a single a formula for Enroute Air Navigation Charges', inject((enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService) => {

      enrouteAirNavigationCharge.id = 1;

      enrouteAirNavigationChargesManagementService.delete(enrouteAirNavigationCharge.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${enrouteAirNavigationCharge.id}`)
        .respond(200, null);

      httpBackend.flush();
    }));
  });

});
