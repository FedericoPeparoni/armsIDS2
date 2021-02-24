// interface
import { IAccount, IAccountRefresh } from '../../accounts/accounts.interface';

// service
import { ScAccountManagementService, endpoint } from './sc-account-management.service';

describe('service ScAccountManagementService', () => {

  let httpBackend,
      restangular;

  let scAccount: IAccount = {
    name: 'Self Care Account',
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
    account_users: [],
    is_self_care: true,
    iata_code: null,
    icao_code: null,
    opr_identifier: null,
    payment_terms: null,
    discount_structure: null,
    tax_profile: null,
    percentage_of_passenger_fee_payable: null,
    invoice_delivery_format: 'test',
    invoice_delivery_method: 'test',
    invoice_currency: null,
    monthly_overdue_penalty_rate: 7,
    notes: 'test',
    black_listed_indicator: true,
    black_listed_override: true,
    credit_limit: 7,
    cash_account: null,
    aircraft_parking_exemption: 7,
    account_type: {
      id: 2,
      name: 'GeneralAviation'
    },
    list_of_events_account_notified: null,
    iata_member: true,
    separate_pax_invoice: true,
    external_accounting_system_identifier: null,
    active: true,
    approved_flight_school_indicator: false,
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

  it('should be registered', inject((scAccountManagementService: ScAccountManagementService) => {
      expect(scAccountManagementService).not.toEqual(null);
  }));

  describe('get method', () => {
    it('should be registered', inject((scAccountManagementService: ScAccountManagementService) => {
      expect(scAccountManagementService.get).not.toBe(null);
    }));

    it('should return a single self-care account', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccount.id = 1;

      scAccountManagementService.get(scAccount.id);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}/${scAccount.id}`)
        .respond(200, scAccount);

      httpBackend.flush();

    }));
  });

  describe('create a self-care account', () => {
    it('should create account', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccountManagementService.create(scAccount);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, scAccount);


      httpBackend.flush();

    }));
  });

  describe('update a self-care account', () => {
    it('should update account', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccount.id = 1;

      scAccountManagementService.update(scAccount, scAccount.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${scAccount.id}`)
        .respond(200, scAccount);

      httpBackend.flush();

    }));
  });

  describe('remove a self-care account', () => {
    it('should remove account', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccount.id = 1;

      scAccountManagementService.delete(scAccount.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${scAccount.id}`)
        .respond(200);

      httpBackend.flush();

    }));
  });

  describe('filter dropdown', () => {

    it('should make an API call with passed query string that includes `invoices` if `outstanding` is picked', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccountManagementService.list(<IAccountRefresh>{ invoices: 'outstanding' });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?invoices=outstanding&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));

    it('should make an API call with passed query string that includes `invoices` if `overdue` is picked', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccountManagementService.list(<IAccountRefresh>{ invoices: 'overdue' });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?invoices=overdue&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));


    it('should make an API call with passed query string that includes `credit` if `credit limit` is picked', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccountManagementService.list(<IAccountRefresh>{ credit: true });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?credit=true&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));

    it('should make an API call with passed query string that includes `selfCareAccounts` if `Self Care Accounts` is picked', inject((scAccountManagementService: ScAccountManagementService) => {

      scAccountManagementService.list(<IAccountRefresh>{ selfCareAccounts: true });

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?selfCareAccounts=true&size=20`)
        .respond(200, {});

      httpBackend.flush();
    }));
  });
});










