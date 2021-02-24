// interfaces
import { IFlightMovement, IFlightMovementSpring } from '../../flight-movement-management/flight-movement-management.interface';
import { ICatalogueServiceChargeType } from '../../catalogue-service-charge/catalogue-service-charge.interface';
import { IAviationInvoice } from '../../invoice-generation/invoice-generation.interface';
import { IInvoiceLineItem } from '../../line-item/line-item.interface';
import { IPayment } from '../../../components/directives/payment/payment.interface';
import { IAdhocFee } from '../../../components/directives/external-database-input/external-database-input.directive';
import { INonAviationInvoicePayload } from '../repots.interface';

export let endpoint: string = 'reports';

// named poorly this way because there already is a reports endpoint
export class ReportsService {


  protected restangular: restangular.IService;

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
  }

  //
  //  - Aviation
  //

  /**
   * Generate an aviation invoice with the status "new" from Point Of Sale Invoice Generation
   *
   * @param accountId
   * @param flightIdList
   * @param format
   * @param preview
   * @param tempFlightMovementList
   * @param flightCategory
   * @returns {IPromise<any>}
   */
  generateAviationInvoice(accountId: number, flightIdList: number[], invoice_permits: IAdhocFee[], format: string, preview: number,
    tempFlightMovementList: IFlightMovement[], flightCategory: number, invoiceCurrency: string): ng.IPromise<any> {
      let url = `${endpoint}/aviation-invoice?accountId=${accountId}&flightIdList=${flightIdList}&format=${format}&preview=${preview}&invoiceCurrency=${invoiceCurrency}`;
      if (flightCategory) {
        url = `${url}&flightCategory=${flightCategory}`;
      }

    return this.Restangular.one(url).customPOST({
      'flight_items': tempFlightMovementList,
      'invoice_permits': invoice_permits
    });
  }

  /**
   * Generate an aviation invoice with the status "new" from Flight Movement
   *
   * @param accountId
   * @param flightIdList
   * @param pointOfSale - indicated from what page it's called (Point Of Sale or Flight Movement)
   * @returns {IPromise<any>}
   */
  generateAviationInvoiceFM(accountId: number, flightIdList: number[], flightCategory: number): ng.IPromise<any> {
    const param = `/aviation-invoice?accountId=${accountId}&flightIdList=${flightIdList}&pointOfSale=false`;
    if (flightCategory) {
      return this.Restangular.one(`${endpoint}${param}&flightCategory=${flightCategory}`).get();
    } else {
      return this.Restangular.one(`${endpoint}${param}`).get();
    }
  }

  /**
   * Preview/Generate an invoice.
   * Used for calculating an invoice
   *
   * @param accountId
   * @param flightIdListArr
   * @param preview
   * @param format
   * @returns {IPromise<any>}
   */
  aviationInvoice(accountId: number = null,
                  flightIdListArr: number[] = [],
                  flight_items: IFlightMovement[],
                  invoice_permits: IAdhocFee[],
                  flightCategory: number,
                  preview: number = 0,
                  format: string = 'json',
                  invoiceCurrency: string): ng.IPromise<IAviationInvoice> {

    const flightIdList = flightIdListArr.toString();

    return this.Restangular.one(`${endpoint}/aviation-invoice`).customPOST({ flight_items, invoice_permits }, null, {
      accountId,
      invoiceCurrency,
      flightIdList,
      flightCategory,
      preview,
      format,
      pay: 0
    });
  }

  // returns billable flights for a single account
  getNonInvoicedFlightMovementsByAccount(accountId: number, userBillingCenterOnly: boolean, page: number,
    queryString: string = '', flightCategory: number): ng.IPromise<IFlightMovementSpring> {
    return this.Restangular.one(`${endpoint}/aviation-invoice/non-invoiced-flights-for-account?${queryString}`).get({
      accountId,
      page,
      userBillingCenterOnly,
      flightCategory
    });
  }

  // generates and pays invoice
  aviationInvoicePay(accountId: number = null,
                    flightIdListArr: number[] = [],
                    flight_items: IFlightMovement[],
                    payment: IPayment,
                    invoice_permits: IAdhocFee[],
                    invoiceCurrency: string): ng.IPromise<any> {

    const flightIdList = flightIdListArr.toString();

    return this.Restangular.one(`${endpoint}/aviation-invoice`).customPOST({ flight_items, payment, invoice_permits }, null, {
      accountId,
      invoiceCurrency,
      flightIdList,
      preview: 0,
      format: 'pdf',
      pay: 1
    });
  }

  /**
   * Validates a flight movement (for the reports service)
   *
   * @param accountId
   * @param flightMovement
   * @returns {IPromise<any>}
   */
  validateFlight(accountId: number, flightMovement: IFlightMovement, temporaryFlights: IFlightMovement[]): ng.IPromise<any> {
    return this.Restangular.one(`${endpoint}/aviation-invoice/validate-flight`)
    .customPOST({
      flight: flightMovement,
      temporary_flights: temporaryFlights
    }, null, { accountId: accountId });
  }

  //
  //  - Non-Aviation
  //

  /**
   * Generate a non-aviation invoice with the status "new" from Point Of Sale Invoice Generation
   *
   * @param accountId
   * @param preview
   * @param format
   * @param lineItems
   * @param invoiceCurrency
   * @returns {IPromise<any>}
   */
  generateNonAviationInvoicePOS(accountId: number,
                                preview: number,
                                format: string,
                                lineItems: Array<IInvoiceLineItem>,
                                invoiceCurrency: string,
                                proforma: boolean): ng.IPromise<any> {
    let body: INonAviationInvoicePayload = this.nonAviationInvoicePayload(lineItems);
    return this.Restangular.one(`${endpoint}/non-aviation-invoice/pos`).customPOST(body, null, { accountId, invoiceCurrency, preview, format, proforma });
  }

  /**
   * Generate a non-aviation invoice with the status "new" from Non-Aviation Billing
   *
   * @param format
   * @param preview
   * @param accountId
   * @param year
   * @param month
   * @param lineItems
   * @returns {IPromise<any>}
   */
  generateNonAviationInvoice(format: string, preview: number, accountId: Array<number>, year: object,
    month: object, lineItems: Array<IInvoiceLineItem>, kraClerkName?: string, kraReceiptNumber?: string): ng.IPromise<any> {
    const url = `${endpoint}/non-aviation-invoice/monthly?accountId=${accountId}&format=${format}&preview=${preview}&month=${month}` +
      `&year=${year}&kraClerkName=${kraClerkName}&kraReceiptNumber=${kraReceiptNumber}`;
    return this.Restangular.one(url).customPOST(lineItems);
  }

  /**
   * Preview/Generate a non-aviation invoice.  NOTE: no option to pay, is structured differently
   * Used for calculating an invoice
   *
   * @param accountId
   * @param lineItem
   * @param preview
   * @param format
   * @param invoiceCurrency
   * @returns {IPromise<any>}
   */
  nonAviationInvoice(accountId: number = null,
                    lineItems: IInvoiceLineItem[] = [],
                    preview: number = 0,
                    format: string = 'pdf',
                    invoiceCurrency: string = null,
                    proforma: boolean): ng.IPromise<any> {
    let body: INonAviationInvoicePayload = this.nonAviationInvoicePayload(lineItems);
    return this.Restangular.one(`${endpoint}/non-aviation-invoice/pos`).customPOST(body, null, { accountId, invoiceCurrency, preview, format, proforma });
  }

  // same as above `nonAviationInvoice` method except it is structured differently to pay it
  nonAviationInvoicePay(accountId: number = null, lineItems: IInvoiceLineItem[] = [], payment: IPayment, invoiceCurrency: string = null): ng.IPromise<any> {
    let body: INonAviationInvoicePayload = this.nonAviationInvoicePayload(lineItems, payment);
    return this.Restangular.one(`${endpoint}/non-aviation-invoice/pos`).customPOST(body, null, { accountId, invoiceCurrency, preview: 0, format: 'pdf', pay: 1 });
  }

  /**
   * Gets line items for non-aviation billing engine
   *
   * @param  {number} month
   * @param  {number} year
   * @param  {number} accountId
   * @returns {IPromise<any>}
   */
  getLineItemsByMonth(month: number, year: number, accountId: number, externalChargeCategoryId: number): ng.IPromise<IInvoiceLineItem[]> {

    let route: string = `${endpoint}/non-aviation-invoice/monthly/prepare-line-items?month=${month}&year=${year}&accountId=${accountId}`;
    if (externalChargeCategoryId) {
      route += `&externalChargeCategoryId=${externalChargeCategoryId}`;
    }

    return this.Restangular.one(route).customPOST();
  }

  // returns a list of service charges that can be used
  getApplicablePosServiceCharges(): ng.IPromise<ICatalogueServiceChargeType[]> {
    return this.Restangular.one(`${endpoint}/non-aviation-invoice/pos/service-charges`).get();
  }

  /**
   * Validates a line item, returns line item with more fields filled out if successful
   *
   * @param lineItem
   * @returns {IPromise<IInvoiceLineItem>}
   */
  validateInvoiceLineItem(accountId: number, lineItem: IInvoiceLineItem, month: number = null, year: number = null, invoiceCurrency: string = null): ng.IPromise<IInvoiceLineItem> {

    switch (lineItem.service_charge_catalogue.charge_basis) {
      case 'percentage':
        lineItem.user_markup_amount = lineItem.user_unit_amount;
        break;
      case 'user':
        lineItem.user_price = lineItem.user_unit_amount;
        break;
      case 'commercial-electric':
      case 'residential-electric':
        lineItem.user_electricity_meter_reading = lineItem.user_unit_amount;
        break;
      case 'water':
        lineItem.user_water_meter_reading = lineItem.user_unit_amount;
        break;
      case 'discount':
        lineItem.user_discount_percentage = lineItem.user_unit_amount;
        break;
      default:
        console.error('Could not match the `charge_basis`');
    }

    if (month && year) {
      // used for Non-Aviation Invoices
      return this.Restangular.one(`${endpoint}/non-aviation-invoice/monthly/validate-line-item`).customPOST(lineItem, null, {
        accountId,
        year,
        month
      });
    }

    // used for Point of Sale Non-Aviation Invoices
    return this.Restangular.one(`${endpoint}/non-aviation-invoice/pos/validate-line-item`).customPOST(lineItem, null, { accountId, invoiceCurrency });
  }

  //
  //  - IATA
  //

  /**
   * Generate an IATA invoice with the status "new"
   *
   * @param billingInterval
   * @param startDate
   * @param endDateInclusive
   * @param preview
   * @param accountIdList
   * @param sort
   * @param userBillingCenterOnly
   * @returns {IPromise<any>}
   */
  generateIATAInvoice(billingInterval: number | string, startDate: string, endDateInclusive: string,
    preview: number, accountIdList: Array<number>, sort: string, userBillingCenterOnly: string): ng.IPromise<any> {
    const url = `${endpoint}/iata-invoice?billingInterval=${billingInterval}&startDate=${startDate}&endDateInclusive=${endDateInclusive}&preview=${preview}` +
      `&sort=${sort}&userBillingCenterOnly=${userBillingCenterOnly}`;
    const body = { account_id_list: accountIdList };
    return this.Restangular.one(url).customPOST(body);
  }

  //
  //  - NON-IATA
  //

  /**
   * Generate an NON-IATA invoice with the status "new"
   *
   * @param billingInterval
   * @param startDate
   * @param endDateInclusive
   * @param preview
   * @param accountIdList
   * @param sort
   * @param userBillingCenterOnly
   * @returns {IPromise<any>}
   */
  generateNonIATAInvoice(billingInterval: number, startDate: string, endDateInclusive: string,
    preview: number, accountIdList: Array<number>, sort: string, userBillingCenterOnly: string, flightCategory: number): ng.IPromise<any> {
    const baseUrl = `${endpoint}/aviation-invoice?billingInterval=${billingInterval}&startDate=${startDate}&endDateInclusive=${endDateInclusive}` +
      `&preview=${preview}&sort=${sort}&userBillingCenterOnly=${userBillingCenterOnly}`;
    const url = flightCategory ? `${baseUrl}&flightCategory=${flightCategory}` : baseUrl;
    const body = { account_id_list: accountIdList };
    return this.Restangular.one(url).customPOST(body);
  }

  nonAviationInvoicePayload(lineItems: Array<IInvoiceLineItem> = [], payment: IPayment = null): INonAviationInvoicePayload {
    const requisitions = lineItems.filter((item: any) =>
      item.requisiton !== null && item.requisition.req_number !== null).map((item: any) => item.requisition);

    const invoicePermits = lineItems.filter((item: any) =>
      item.invoice_permit !== null && item.invoice_permit.invoice_permit_number !== null).map((item: any) => item.invoice_permit);

    return <INonAviationInvoicePayload> {
      line_items: lineItems.length ? lineItems : null,
      payment: payment,
      permit_numbers: invoicePermits.length ? invoicePermits : null,
      requisition_numbers: requisitions.length ? requisitions : null
    };
  }
}
