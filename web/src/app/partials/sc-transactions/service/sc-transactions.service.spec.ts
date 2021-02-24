// services
import { ScTransactionsService, endpoint } from './sc-transactions.service';

// interfaces
import { ITransaction } from '../../transactions/transactions.interface';

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

  it('should be registered', inject((scTransactionsService: ScTransactionsService) => {
    expect(scTransactionsService).not.toEqual(null);
  }));

  describe('list', () => {
    it('getting list of transactions', inject((scTransactionsService: ScTransactionsService) => {

      let transactionList: Array<ITransaction> = [];

      transactionList.push(transaction);

      scTransactionsService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, transactionList);

      httpBackend.flush();

    }));
  });
});
