// interface
import { IUser } from '../users.interface';

// service
import { UsersService } from './users.service';

describe('service users', () => {

  let httpBackend,
    restangular,
    response;

  const user: IUser = {
    email: 'system',
    id: 1,
    login: 'system',
    name: 'system',
    job_title: null,
    permissions: [],
    roles: [],
    contact_information: 'test',
    sms_number: '12345',
    language: 'en',
    billing_center: {
      aerodromes: [],
      hq: null,
      id: null,
      invoice_sequence_number: null,
      name: null,
      prefix_invoice_number: null,
      prefix_receipt_number: null,
      receipt_sequence_number: null,
      users: [],
      external_accounting_system_identifier: null,
      iata_invoice_sequence_number: null,
      receipt_cheque_sequence_number: null,
      receipt_wire_sequence_number: null
    },
    is_selfcare_user: false,
    force_password_change: false,
    registration_status: true
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

  it('should be registered', inject((usersService: UsersService) => {
    expect(usersService).not.toEqual(null);
  }));

  describe('usersService', () => {

    it('getting list of users', inject((usersService: UsersService) => {

      let expectedResponse = [];

      usersService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/users?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));

    it('getting the current user', inject((usersService: UsersService) => {

      usersService.current.then(() => {
        //
      });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/users/current`)
        .respond(200, user);

      httpBackend.flush();
    }));

    it('getting a single user', inject((usersService: UsersService) => {

      let expectedResponse = [];
      let id = 1;

      usersService.get(id);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/users/${id}`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));

    it('saving a single user', inject((usersService: UsersService) => {

      usersService.create(user);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/users`)
        .respond(200, user);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/users/1/roles`)
        .respond(200, user);

      httpBackend.flush();

    }));

    it('updating a single user', inject((usersService: UsersService) => {

      usersService.update(user, user.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/users/${user.id}`)
        .respond(200, user);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/users/${user.id}/roles`)
        .respond(200, user);

      httpBackend.flush();

    }));

    it('deleting a single user', inject((usersService: UsersService) => {

      let expectedResponse = [];

      let id = 1;

      usersService.delete(id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/users/${id}`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));

  });

});
