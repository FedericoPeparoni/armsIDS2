// service
import { InvoicesService, endpoint } from './invoices.service';

// interface
import { IInvoice } from '../invoices.interface';
import { IInvoiceLineItem } from '../../line-item/line-item.interface';

describe('service InvoicesService', () => {

    let httpBackend,
        restangular;

    let lineItem: IInvoiceLineItem = {
        aerodrome: null,
        service_charge_catalogue: null,
        recurring_charge: null,
        amount: null,
        user_electricity_charge_type: null,
        user_discount_percentage: null,
        user_town: null,
        price_per_unit: null,
        any_aerodrome: null,
        invoice_permit: {
            invoice_permit_number: null,
            external_database_for_charge: null,
            adhoc_total_fee_payment_amount: null
        },
        requisition: {
            req_number: null,
            external_database_for_charge: null,
            req_currency: null,
            req_total_amount: null,
            req_ar_id: null,
            req_country_id: null,
            req_id: null,
            req_maninfo_id: null
        }
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

    it('should be registered', inject((invoicesService: InvoicesService) => {
        expect(invoicesService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of invoices', inject((invoicesService: InvoicesService) => {

            let expectedResponse = Array<IInvoice>();

            invoicesService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('getLineItemsByInvoiceId', () => {
        it('should return an array of lineItems', inject((invoicesService: InvoicesService) => {

            let expectedResponse: Array<IInvoiceLineItem> = [];
            expectedResponse.push(lineItem);

            invoicesService.getLineItemsByInvoiceId(1);

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/getLineItemsByInvoiceId/${1}`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });
});
