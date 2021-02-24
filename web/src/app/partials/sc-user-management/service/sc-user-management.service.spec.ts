// interface
import { IUser } from '../../users/users.interface';

// services
import { ScUserManagementService, endpoint } from './sc-user-management.service';

describe('service ScUserManagementService', () => {

  let httpBackend,
      restangular,
      response;

  const scUser: IUser = {
    email: 'self_care@selfcare.com',
    id: 1,
    login: 'selfcare',
    name: 'selfcare',
    job_title: null,
    permissions: [],
    roles: [],
    contact_information: 'test',
    sms_number: null,
    language: 'en',
    billing_center: null,
    is_selfcare_user: true,
    force_password_change: false,
    registration_status: false
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

  it('should be registered', inject((scUserManagementService: ScUserManagementService) => {
    expect(scUserManagementService).not.toEqual(null);
  }));

  describe('usersService', () => {

    it('getting list of users', inject((scUserManagementService: ScUserManagementService) => {

      let expectedResponse = [];

      scUserManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));

    it('getting a self-care user', inject((scUserManagementService: ScUserManagementService) => {

      let expectedResponse = [];

      scUserManagementService.get(scUser.id);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/${scUser.id}`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));

    it('updating a self-care user', inject((scUserManagementService: ScUserManagementService) => {

      scUserManagementService.update(scUser, scUser.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${scUser.id}`)
        .respond(200, scUser);

      httpBackend.flush();

    }));

    it('deleting a self-care user', inject((scUserManagementService: ScUserManagementService) => {

      let expectedResponse = [];

      scUserManagementService.delete(scUser.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${scUser.id}`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));
  });
});
