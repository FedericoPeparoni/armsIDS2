import { AccountsService, endpoint } from './accounts.service';
import { IAccount, IAccountRefresh } from '../accounts.interface';

describe('service accounts', () => {

  let httpBackend,
    restangular;

  let account: IAccount = {
    name: 'Air Canada3',
    alias: 'AC',
    aviation_billing_contact_person_name: 'John Smith',
    aviation_billing_phone_number: '1234567890',
    aviation_billing_mailing_address: 'asd@asda.com',
    aviation_billing_email_address: 'john@test.com',
    aviation_billing_sms_number: '1234567890',
    non_aviation_billing_contact_person_name: 'test',
    non_aviation_billing_phone_number: 'test',
    non_aviation_billing_mailing_address: 'test',
    non_aviation_billing_email_address: 'test',
    non_aviation_billing_sms_number: 'test',
    is_self_care: false,
    iata_code: 'Ae',
    icao_code: 'Ae',
    opr_identifier: 'test',
    payment_terms: 7,
    discount_structure: 7,
    tax_profile: 'test',
    percentage_of_passenger_fee_payable: 7,
    invoice_delivery_format: 'test',
    invoice_delivery_method: 'test',
    invoice_currency: {
      id: null,
      currency_code: null,
      currency_name: null,
      country_code: null,
      decimal_places: null,
      allow_updated_from_web: null,
      symbol: null,
      active: null,
      external_accounting_system_identifier: null,
      exchange_rate_target_currency_id: null
    },
    monthly_overdue_penalty_rate: 7,
    notes: 'test',
    black_listed_indicator: true,
    black_listed_override: true,
    credit_limit: 7,
    cash_account: null,
    aircraft_parking_exemption: 7,
    account_type: {
      id: 1,
      name: 'aviation'
    },
    list_of_events_account_notified: null,
    iata_member: true,
    separate_pax_invoice: true,
    external_accounting_system_identifier: 'test',
    active: true,
    approved_flight_school_indicator: false,
    account_users: [],
    nationality: 'NATIONAL',
    whitelist_last_activity_date_time: null,
    whitelist_state: null,
    whitelist_inactivity_notice_sent_flag: null,
    whitelist_expiry_notice_sent_flag: null
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

  it('should be registered', inject(() => {
    expect(AccountsService).not.toEqual(null);
  }));

  describe('get method', () => {
    it('should be registered', inject((accountsService: AccountsService) => {
      expect(accountsService.get).not.toBe(null);
    }));

    it('should return a single account', inject((accountsService: AccountsService) => {

      account.id = 1;

      accountsService.get(account.id);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/${account.id}`)
        .respond(200, account);

      httpBackend.flush();

    }));
  });

  describe('create account', () => {
    it('should create account', inject((accountsService: AccountsService) => {

      accountsService.create(account);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, account);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/1/account_events`)
        .respond(200, account);

      httpBackend.flush();

    }));
  });

  describe('update account', () => {
    it('should update account', inject((accountsService: AccountsService) => {

      account.id = 1;

      accountsService.update(account, account.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${account.id}`)
        .respond(200, account);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${account.id}/account_events`)
        .respond(200, account);

      httpBackend.flush();

    }));
  });

  describe('remove account', () => {
    it('should remove account', inject((accountsService: AccountsService) => {

      account.id = 1;

      accountsService.delete(account.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${account.id}`)
        .respond(200);

      httpBackend.flush();

    }));
  });

  describe('filter dropdown', () => {

    it('should make an API call with passed query string that includes `invoices` if `outstanding` is picked', inject((accountsService: AccountsService) => {

      accountsService.list(<IAccountRefresh>{ invoices: 'outstanding' });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?invoices=outstanding&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));

    it('should make an API call with passed query string that includes `invoices` if `overdue` is picked', inject((accountsService: AccountsService) => {

      accountsService.list(<IAccountRefresh>{ invoices: 'overdue' });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?invoices=overdue&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));


    it('should make an API call with passed query string that includes `credit` if `credit limit` is picked', inject((accountsService: AccountsService) => {

      accountsService.list(<IAccountRefresh>{ credit: true });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?credit=true&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));

  });

});
