import { AircraftRegistrationService } from './aircraft-registration.service';
import { IAircraftRegistration } from '../aircraft-registration.interface';

describe('service AircraftRegistrationService', () => {

  let httpBackend,
    restangular,
    response;

  const endpoint: string = 'aircraft-registrations';

  const aircraft: IAircraftRegistration = {
    id: 8,
    registration_number: '54',
    registration_start_date: '2016-09-19',
    registration_expiry_date: '2016-09-22',
    mtow_override: 3.0,
    account:  {
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
      list_of_events_account_notified: null,
      iata_member: null,
      separate_pax_invoice: null,
      external_accounting_system_identifier: null,
      active: null,
      approved_flight_school_indicator: null,
      nationality: 'NATIONAL',
      whitelist_last_activity_date_time: null,
      whitelist_state: null,
      whitelist_inactivity_notice_sent_flag: null,
      whitelist_expiry_notice_sent_flag: null
    },
    aircraft_type: {
      id: null,
      aircraft_type: null,
      aircraft_name: null,
      aircraft_image: null,
      manufacturer: null,
      maximum_takeoff_weight: null,
      wake_turbulence_category: {
        id: null,
        name: null
      }
    },
    country_of_registration: {
      id: null,
      country_code: null,
      country_name: null,
      aircraft_registration_prefixes: null,
      aerodrome_prefixes: null
    },
    country_override: null,
    created_by_self_care: false,
    coa_expiry_date: null,
    coa_issue_date: null,
    aircraft_service_date: null,
    is_local: false
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

      response = null;
    });
  });

  it('should be registered', inject((aircraftRegistrationService: AircraftRegistrationService) => {
    expect(aircraftRegistrationService).not.toEqual(null);
  }));

  describe('method', () => {

    describe('list', () => {
      it('should be registered', inject((aircraftRegistrationService: AircraftRegistrationService) => {
        expect(aircraftRegistrationService.list).not.toEqual(null);
      }));

      it('should return an array of aircraft', inject((aircraftRegistrationService: AircraftRegistrationService) => {

        let response = Array<IAircraftRegistration>(aircraft);

        aircraftRegistrationService.list();

        httpBackend
          .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
          .respond(200, response);

        httpBackend.flush();

      }));
    });

    describe('create', () => {
      it('should be registered', inject((aircraftRegistrationService: AircraftRegistrationService) => {
        expect(aircraftRegistrationService.create).not.toEqual(null);
      }));

      it('should create an aircraft, returning that aircraft in response', inject((aircraftRegistrationService: AircraftRegistrationService) => {

        let response = aircraft;

        aircraftRegistrationService.create(aircraft);

        httpBackend
          .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
          .respond(200, response);

        httpBackend.flush();

      }));
    });

    describe('update', () => {
      it('should be registered', inject((aircraftRegistrationService: AircraftRegistrationService) => {
        expect(aircraftRegistrationService.update).not.toEqual(null);
      }));

      it('should update an aircraft, returning that aircraft in response', inject((aircraftRegistrationService: AircraftRegistrationService) => {

        aircraftRegistrationService.update(aircraft, aircraft.id);

        httpBackend
          .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${aircraft.id}`)
          .respond(200, aircraft);

        httpBackend.flush();

      }));
    });

    describe('delete', () => {
      it('should be registered', inject((aircraftRegistrationService: AircraftRegistrationService) => {
        expect(aircraftRegistrationService.delete).not.toEqual(null);
      }));

      it('should delete an aircraft, returning that aircraft in response', inject((aircraftRegistrationService: AircraftRegistrationService) => {

        aircraftRegistrationService.delete(1);

        httpBackend
          .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/1`)
          .respond(200, {});

        httpBackend.flush();

      }));
    });

    describe('upload', () => {
      it('should be registered', inject((aircraftRegistrationService: AircraftRegistrationService) => {
        expect(aircraftRegistrationService.upload).not.toEqual(null);
      }));

      it('should upload a file', inject((aircraftRegistrationService: AircraftRegistrationService) => {

        const blob = new Blob();
        let formData = new FormData();
        formData.append('file', blob, 'file_name');

        aircraftRegistrationService.upload('PUT', formData, null, null);

        httpBackend
          .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/upload`)
          .respond(200, {});

        httpBackend.flush();

      }));
    });

  });
});
