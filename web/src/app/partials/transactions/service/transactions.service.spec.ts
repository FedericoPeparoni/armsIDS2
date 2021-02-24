// services
import { TransactionsService, endpoint } from './transactions.service';

// interfaces
import { ITransaction } from '../transactions.interface';
import { ICurrency } from '../../currency-management/currency-management.interface';

describe('service TransactionsService', () => {

  let httpBackend,
    restangular;

  let transaction: ITransaction = {
    id: null,
    kra_receipt_number: 'receipt',
    kra_clerk_name: 'clerk',
    account: null,
    transaction_date_time: null,
    description: 'test',
    transaction_type: {
      id: null,
      name: null
    },
    amount: 10000,
    currency: null,
    exchange_rate_to_usd: 1,
    exchange_rate_to_ansp: 1,
    payment_mechanism: 'credit',
    payment_reference_number: '123',
    balance: 10000,
    exported: false,
    billing_ledger_ids: [2],
    payment_amount: 10000,
    payment_currency: null,
    payment_exchange_rate: 1,
    charges_adjustment: [{
      date: null,
      aerodrome: null,
      flight_id: null,
      charge_description: null,
      charge_amount: null,
      other_description: null,
      transaction_id: null
    }],
    payments_exported: false,
    has_approval_document: false,
    approval_document_name: null,
    approval_document_type: null,
    has_supporting_document: false,
    supporting_document_name: null,
    supporting_document_type: null,
    interest_invoice_error: null,
    transaction_approvals: null
  };

  let currency: ICurrency = {
    currency_code: 'CAD',
    currency_name: 'Canadian Dollar',
    country_code: {
      id: 1,
      country_code: 'test',
      country_name: 'test',
      aircraft_registration_prefixes: null,
      aerodrome_prefixes: null
    },
    allow_updated_from_web: true,
    decimal_places: 2,
    symbol: '$',
    active: true,
    external_accounting_system_identifier: null,
    exchange_rate_target_currency_id: null
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

  it('should be registered', inject((transactionsService: TransactionsService) => {
    expect(transactionsService).not.toEqual(null);
  }));

  describe('list', () => {
    it('getting list of transactions', inject((transactionsService: TransactionsService) => {

      let transactionList: Array<ITransaction> = [];

      transactionList.push(transaction);

      transactionsService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, transactionList);

      httpBackend.flush();

    }));
  });


  describe('list payment mechanisms', () => {
    it('getting list of payment mechanisms', inject((transactionsService: TransactionsService) => {

      let paymentMechanismList: Array<string> = [];

      paymentMechanismList.push('credit');

      transactionsService.getPaymentMechanismList();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/getPaymentMechanismList`)
        .respond(200, paymentMechanismList);

      httpBackend.flush();

    }));
  });

  describe('list currencies', () => {
    it('getting list of currencies by account id', inject((transactionsService: TransactionsService) => {

      let currencies: Array<ICurrency> = [];

      currencies.push(currency);

      transactionsService.getCurrencyListByAccountId(1);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/getCurrencyListByAccountId/1`)
        .respond(200, currencies);

      httpBackend.flush();

    }));
  });

  describe('create', () => {
    it('should create a single transaction', inject((transactionsService: TransactionsService) => {

      transactionsService.create(transaction);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, transaction);

      httpBackend.flush();
    }));
  });

  describe('validate selected invoices', () => {
    it('should send selected invoices and return with amounts', inject((transactionsService: TransactionsService) => {

      transaction.billing_ledger_ids = [1];

      transactionsService.validateSelectedInvoice(transaction);

      let expectedResponse = {'1': '0.00'};

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/calculateAmountForTransactionPayments`)
        .respond(200, expectedResponse);

      httpBackend.flush();
    }));
  });

});
