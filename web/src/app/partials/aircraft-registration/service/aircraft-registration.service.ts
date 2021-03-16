// interface
import { IAircraftRegistration } from '../aircraft-registration.interface';
import { ICountry } from '../../country-management/country-management.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'aircraft-registrations';

export class AircraftRegistrationService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IAircraftRegistration = {
    id: null,
    registration_number: null,
    registration_start_date: null,
    registration_expiry_date: null,
    mtow_override: null,
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
      is_self_care: null,
      account_users: [],
      iata_code: null,
      icao_code: null,
      opr_identifier: null,
      payment_terms: null,
      discount_structure: null,
      tax_profile: null,
      cash_account: null,
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
      account_type_discount: null,
      list_of_events_account_notified: null,
      iata_member: null,
      separate_pax_invoice: null,
      external_accounting_system_identifier: null,
      active: null,
      approved_flight_school_indicator: null,
      nationality: null,
      whitelist_last_activity_date_time: null,
      whitelist_state: null,
      whitelist_inactivity_notice_sent_flag: null,
      whitelist_expiry_notice_sent_flag: null
    },
    aircraft_type: {
      id: null,
      aircraft_type: null,
      aircraft_image: null,
      aircraft_name: null,
      manufacturer: null,
      maximum_takeoff_weight: null,
      wake_turbulence_category: null
    },
    country_of_registration: {
      id: null,
      country_code: null,
      country_name: null,
      aircraft_registration_prefixes: null,
      aerodrome_prefixes: null
    },
    country_override: false,
    created_by_self_care: false,
    coa_expiry_date: null,
    coa_issue_date: null,
    aircraft_service_date: null,
    is_local: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, protected $q: angular.IQService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  // returns a country based on the registration number prefix
  public getCountryByRegistrationNumberPrefix(prefix: string): ng.IPromise<ICountry> {
    return this.restangular.one(`${endpoint}/prefix/${prefix}`).get();
  }

  /**
   * Get valid aircraft type from aircraft registration number and registration date.
   *
   * @param regNumber registration number
   * @param flightDate date of registration
   */
  public getAircraftType(regNumber: string, regDate: Date): ng.IPromise<any> {
    if (regNumber && regDate) {
      return this.restangular.one(`${endpoint}/aircraft-type/${regNumber}/${regDate.toISOString()}`).get();
    } else {
      return this.$q.reject('regNumber and flightDate cannot be null or empty');
    }
  }
}
