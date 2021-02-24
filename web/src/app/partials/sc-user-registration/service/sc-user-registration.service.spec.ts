// interface
import { IScUserRegistration } from '../sc-user-registration.interface';

// services
import { ScUserRegistrationService, endpoint } from './sc-user-registration.service';

describe('service ScUserRegistrationService', () => {

  let httpBackend,
      restangular;

  const scUser: IScUserRegistration = {
    id: null,
    email: 'self_care@selfcare.com',
    login: 'selfcare',
    job_title: null,
    name: null,
    permissions: [],
    roles: [],
    contact_information: null,
    sms_number: null,
    billing_center: null,
    language: null,
    is_selfcare_user: true,
    force_password_change: false,
    registration_status: false,
    url: null
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

  it('should be registered', inject((scUserRegistrationService: ScUserRegistrationService) => {
    expect(scUserRegistrationService).not.toEqual(null);
  }));

  describe('scUserRegistrationService', () => {

    it('saving a self-care user', inject((scUserRegistrationService: ScUserRegistrationService) => {

      scUserRegistrationService.create(scUser);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, scUser);

      httpBackend.flush();

    }));
  });
});

