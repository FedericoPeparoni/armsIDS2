import { ReportsService } from './reports.service';
import { LineItem } from '../../line-item/line-item';

import { endpoint } from './reports.service';
import { ICatalogueServiceChargeType } from '../../catalogue-service-charge/catalogue-service-charge.interface';

describe('service ReportsService', () => {

  let httpBackend,
    restangular;


  const serviceCharge: ICatalogueServiceChargeType = {
    id: null,
    charge_class: null,
    category: null,
    type: null,
    subtype: null,
    description: null,
    charge_basis: 'user',
    minimum_amount: null,
    maximum_amount: null,
    amount: null,
    invoice_category: null,
    external_accounting_system_identifier: null,
    external_charge_category: null,
    currency: null
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

  it('should be registered', inject((reportsService: ReportsService) => {
    expect(reportsService).not.toEqual(null);
  }));

  describe('method getNonInvoicedFlightMovementsByAccount', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.getNonInvoicedFlightMovementsByAccount).toBeDefined();
    }));

  });

  describe('method aviationInvoice', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.aviationInvoice).toBeDefined();
    }));

  });

  describe('method validateFlight', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.validateFlight).toBeDefined();
    }));

  });

  describe('method nonAviationInvoice', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.nonAviationInvoice).toBeDefined();
    }));

    it('should return successfully', inject((reportsService: ReportsService) => {

      reportsService.nonAviationInvoice(1, [], 0, 'pdf', null, false);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/non-aviation-invoice/pos?accountId=1&format=pdf&preview=0&proforma=false`)
        .respond(200, {});

      httpBackend.flush();

    }));

  });

  describe('method nonAviationInvoicePay', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.nonAviationInvoicePay).toBeDefined();
    }));
  });

  describe('method getLineItemsByMonth', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.getLineItemsByMonth).toBeDefined();
    }));

    it('should return a list of line items', inject((reportsService: ReportsService) => {

      let month = 1;
      let year = 2017;
      let accountId = 1;

      let expectedResponse = reportsService.getLineItemsByMonth(month, year, accountId, null);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/non-aviation-invoice/monthly/prepare-line-items?month=${month}&year=${year}&accountId=${accountId}`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));

  });

  describe('method getApplicablePosServiceCharges', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.getApplicablePosServiceCharges).toBeDefined();
    }));

    it('should return a list of service charges that can be used', inject((reportsService: ReportsService) => {

      reportsService.getApplicablePosServiceCharges();

      let items: ICatalogueServiceChargeType[] = [serviceCharge];

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/non-aviation-invoice/pos/service-charges`)
        .respond(200, items);

      httpBackend.flush();

    }));

  });

  describe('method validateInvoiceLineItem', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.validateInvoiceLineItem).toBeDefined();
    }));

    it('should validate an invoice line item', inject((reportsService: ReportsService) => {

      LineItem.service_charge_catalogue.charge_basis = 'user';

      reportsService.validateInvoiceLineItem(1, LineItem, 12.34);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/non-aviation-invoice/pos/validate-line-item?accountId=1`)
        .respond(200, LineItem);

      httpBackend.flush();
    }));

  });

  describe('method validateInvoiceLineItem by month', () => {

    it('should be defined', inject((reportsService: ReportsService) => {
      expect(reportsService.validateInvoiceLineItem).toBeDefined();
    }));

    it('should validate an invoice line item by month', inject((reportsService: ReportsService) => {

      LineItem.service_charge_catalogue.charge_basis = 'user';

      reportsService.validateInvoiceLineItem(1, LineItem, 1, 2017);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/non-aviation-invoice/monthly/validate-line-item?accountId=1&month=1&year=2017`)
        .respond(200, LineItem);

      httpBackend.flush();
    }));

  });

});
