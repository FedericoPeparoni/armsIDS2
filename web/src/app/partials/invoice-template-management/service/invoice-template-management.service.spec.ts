// interface
import { IInvoiceTemplate } from '../invoice-template-management.interface';

// service
import { InvoiceTemplateManagementService, endpoint } from './invoice-template-management.service';

describe('service InvoiceTemplateManagementService', () => {

    let httpBackend,
        restangular;

    let invoiceTpl: IInvoiceTemplate = {
      invoice_template_name: 'test',
      invoice_category: 'iata-avi',
      template_document: null
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

    it('should be registered', inject((invoiceTemplateManagementService: InvoiceTemplateManagementService) => {
        expect(invoiceTemplateManagementService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of invoice templates', inject((invoiceTemplateManagementService: InvoiceTemplateManagementService) => {

            let expectedResponse = Array<IInvoiceTemplate>();

            invoiceTemplateManagementService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single invoice template', inject((invoiceTemplateManagementService: InvoiceTemplateManagementService) => {

            invoiceTemplateManagementService.create(invoiceTpl);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, invoiceTpl);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single invoice template', inject((invoiceTemplateManagementService: InvoiceTemplateManagementService) => {

            invoiceTpl.id = 1;

            invoiceTemplateManagementService.delete(invoiceTpl.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${invoiceTpl.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
