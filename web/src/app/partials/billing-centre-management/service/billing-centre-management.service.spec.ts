// interface
import { IBillingCentre } from '../billing-centre-management.interface';

// service
import { BillingCentreManagementService, endpoint } from './billing-centre-management.service';

describe('service BillingCentreManagementService', () => {

  let httpBackend,
    restangular;

  let billingCentre: IBillingCentre = {
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

  it('should be registered', inject((billingCentreManagementService: BillingCentreManagementService) => {
    expect(billingCentreManagementService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of billing centres', inject((billingCentreManagementService: BillingCentreManagementService) => {

      let expectedResponse = Array<IBillingCentre>();

      billingCentreManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

  describe('create', () => {
    it('should create a single billing centre', inject((billingCentreManagementService: BillingCentreManagementService) => {

      billingCentreManagementService.create(billingCentre);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, billingCentre);

      httpBackend.flush();
    }));
  });

  describe('update', () => {
    it('should update a single billing centre', inject((billingCentreManagementService: BillingCentreManagementService) => {

      billingCentre.id = 1;

      billingCentreManagementService.update(billingCentre, billingCentre.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${billingCentre.id}`)
        .respond(200, billingCentre);

      httpBackend.flush();
    }));
  });

  describe('delete', () => {
    it('should delete a single billing centre', inject((billingCentreManagementService: BillingCentreManagementService) => {

      billingCentre.id = 1;

      billingCentreManagementService.delete(billingCentre.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${billingCentre.id}`)
        .respond(200, null);

      httpBackend.flush();
    }));
  });


});
