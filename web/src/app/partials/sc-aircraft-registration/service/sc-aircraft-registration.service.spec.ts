// service
import { ScAircraftRegistrationService, endpoint } from './sc-aircraft-registration.service';

// interface
import { IAircraftRegistration } from '../../aircraft-registration/aircraft-registration.interface';

describe('service ScAircraftRegistrationService', () => {

  let httpBackend,
    restangular,
    response;

  const scAircraft: IAircraftRegistration = {
    id: 10,
    registration_number: 'SC56',
    registration_start_date: '2018-05-01',
    registration_expiry_date: '2018-12-25',
    mtow_override: 3.0,
    account: null,
    aircraft_type: null,
    country_of_registration: null,
    country_override: null,
    created_by_self_care: true,
    coa_expiry_date: null,
    coa_issue_date: null,
    aircraft_service_date: null,
    is_local: null
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

  it('should be registered', inject((scAircraftRegistrationService: ScAircraftRegistrationService) => {
    expect(scAircraftRegistrationService).not.toEqual(null);
  }));

  describe('method', () => {

    describe('list', () => {
      it('should return an array of self-care aircrafts', inject((scAircraftRegistrationService: ScAircraftRegistrationService) => {

        let response = Array<IAircraftRegistration>(scAircraft);

        scAircraftRegistrationService.list();

        httpBackend
          .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
          .respond(200, response);

        httpBackend.flush();

      }));
    });

    describe('create', () => {
      it('should create a self-care aircraft, returning that aircraft in response', inject((scAircraftRegistrationService: ScAircraftRegistrationService) => {

        scAircraftRegistrationService.create(scAircraft);

        httpBackend
          .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
          .respond(200, scAircraft);

        httpBackend.flush();

      }));
    });

    describe('update', () => {
      it('should update a self-care aircraft, returning that aircraft in response', inject((scAircraftRegistrationService: ScAircraftRegistrationService) => {

        scAircraftRegistrationService.update(scAircraft, scAircraft.id);

        httpBackend
          .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${scAircraft.id}`)
          .respond(200, scAircraft);

        httpBackend.flush();

      }));
    });

    describe('delete', () => {
      it('should delete a self-care aircraft, returning that aircraft in response', inject((scAircraftRegistrationService: ScAircraftRegistrationService) => {

        scAircraftRegistrationService.delete(scAircraft.id);

        httpBackend
          .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${scAircraft.id}`)
          .respond(200, {});

        httpBackend.flush();

      }));
    });

    describe('upload', () => {
      it('should upload a file', inject((scAircraftRegistrationService: ScAircraftRegistrationService) => {

        const blob = new Blob();
        let formData = new FormData();
        formData.append('file', blob, 'file_name');

        scAircraftRegistrationService.upload('PUT', formData, null, null);

        httpBackend
          .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/upload`)
          .respond(200, {});

        httpBackend.flush();

      }));
    });

  });
});
