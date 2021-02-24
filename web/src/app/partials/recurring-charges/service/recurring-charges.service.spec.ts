import { RecurringChargesService, endpoint } from './recurring-charges.service';
import { IRecurringCharge } from '../recurring-charges.interface';

describe('service RecurringChargesService', () => {

  let httpBackend,
      restangular;


  let recurringCharge: IRecurringCharge = {
    id: null,
    service_charge_catalogue: {
      id: null,
      charge_class: null,
      category: null,
      type: null,
      subtype: null,
      description: null,
      charge_basis: null,
      minimum_amount: null,
      maximum_amount: null,
      amount: null,
      invoice_category: null,
      external_accounting_system_identifier: null,
      external_charge_category: null,
      currency: null
    },
    account: {
      id: null,
      name: null,
      alias: null,
      aviation_billing_contact_person_name: null,
      aviation_billing_phone_number: null,
      aviation_billing_mailing_address: null,
      aviation_billing_email_address: null,
      aviation_billing_sms_number: null,
      non_aviation_billing_contact_person_name: null,
      non_aviation_billing_phone_number: null,
      non_aviation_billing_mailing_address: null,
      non_aviation_billing_email_address: null,
      non_aviation_billing_sms_number: null,
      account_users: [],
      is_self_care: null,
      iata_code: null,
      icao_code: null,
      opr_identifier: null,
      payment_terms: null,
      discount_structure: null,
      tax_profile: null,
      percentage_of_passenger_fee_payable: null,
      invoice_delivery_format: null,
      invoice_delivery_method: null,
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
      monthly_overdue_penalty_rate: null,
      notes: null,
      black_listed_indicator: null,
      black_listed_override: null,
      credit_limit: null,
      aircraft_parking_exemption: null,
      account_type: null,
      list_of_events_account_notified: null,
      iata_member: null,
      separate_pax_invoice: null,
      external_accounting_system_identifier: null,
      active: null,
      cash_account: null,
      approved_flight_school_indicator: null,
      nationality: 'NATIONAL',
      whitelist_last_activity_date_time: null,
      whitelist_state: null,
      whitelist_inactivity_notice_sent_flag: null,
      whitelist_expiry_notice_sent_flag: null
    },
    start_date: null,
    end_date: null
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

  it('should be registered', inject((recurringChargesService: RecurringChargesService) => {
      expect(recurringChargesService).not.toEqual(null);
  }));


  describe('list', () => {

    it('should return an array of recurring charges', inject((recurringChargesService: RecurringChargesService) => {

      let expectedResponse = Array<IRecurringCharge>();

      recurringChargesService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));
  });

  describe('create', () => {
    it('should create a single recurring charge', inject((recurringChargesService: RecurringChargesService) => {

      recurringChargesService.create(recurringCharge);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, recurringCharge);

      httpBackend.flush();

    }));
  });

  describe('update', () => {
    it('should be registered', inject((recurringChargesService: RecurringChargesService) => {
      expect(recurringChargesService.update).not.toEqual(null);
    }));

    it('should update a single recurring charge', inject((recurringChargesService: RecurringChargesService) => {

      recurringCharge.id = 1;

      recurringChargesService.update(recurringCharge, recurringCharge.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${recurringCharge.id}`)
        .respond(200, recurringCharge);

      httpBackend.flush();

    }));
  });

  describe('delete', () => {
    it('should delete a single recurring charge', inject((recurringChargesService: RecurringChargesService) => {

      recurringCharge.id = 1;

      recurringChargesService.delete(recurringCharge.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${recurringCharge.id}`)
        .respond(200, null);

      httpBackend.flush();

    }));
  });

  describe('getByAccountId', () => {

    it('should be registered', inject((recurringChargesService: RecurringChargesService) => {
      expect(recurringChargesService.getByAccountId).not.toEqual(null);
    }));

    it('should return recurring charges based on account id', inject((recurringChargesService: RecurringChargesService) => {

      recurringCharge.account.id = 1;
      recurringCharge.id = 1;

      recurringChargesService.delete(recurringCharge.id);

      httpBackend
        .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${recurringCharge.id}`)
        .respond(200, null);

      httpBackend.flush();

    }));
  });

});
