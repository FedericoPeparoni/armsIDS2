// interface
import { IFlightMovement } from '../flight-movement-management.interface';

// service
import { FlightMovementManagementService, endpoint } from './flight-movement-management.service';

describe('service FlightMovementManagementService', () => {

  let httpBackend,
    restangular;

  let flightMovement: IFlightMovement = {
    'id': null,
    'actual_departure_time': null,
    'actual_mtow': null,
    'aircraft_type': 'AT45',
    'arrival_ad': null,
    'arrival_time': null,
    'associated_aircraft': null,
    'associated_account': null,
    'associated_account_black_listed_indicator': null,
    'associated_account_black_listed_override': null,
    'associated_account_id': null,
    'atc_crossing_distance': null,
    'atc_crossing_distance_cost': null,
    'average_mass_factor': null,
    'crew_members': null,
    'delta_flight': null,
    'date_of_flight': '2016-07-13T00:00:00Z',
    'dep_ad': 'FBSK',
    'dep_time': '0415',
    'dest_ad': 'FAOR',
    'domestic_passenger_charges': null,
    'enroute_charges': null,
    'enroute_charges_basis': null,
    'enroute_charges_status': null,
    'enroute_invoice_id': null,
    'entry_time': null,
    'exempt_approach_charges': 1,
    'exempt_dep_charges': 1,
    'exempt_domestic_passenger_charges': 1,
    'exempt_enroute_charges': 1,
    'exempt_international_passenger_charges': 1,
    'exempt_parking_charges': 1,
    'exit_time': null,
    'flight_id': 'BOT209',
    'flight_level': 'F330',
    'flight_notes': null,
    'flight_rules': 'I',
    'flight_type': 'S',
    'flight_category_nationality': null,
    'flight_category_scope': null,
    'flight_category_type': null,
    'flightmovement_category_name': null,
    'fpl_crossing_cost': null,
    'fpl_crossing_distance': null,
    'fpl_route': 'DCT GSV UG653 NESEK DCT JSV DCT',
    'initial_fpl_data': '(FPL-BOT209-IS\r\n-01AT45/M-SDFGR/C\r\n-FBSK0415\r\n-N0250F190 DCT GSV UG653 NESEK DCT JSV DCT\r\n-FAOR0050 FBSK\r\n-PBN/B4 NAV/GPS DOF/160713 REG/A2ABN EET/FAJA0010 OPR/AIR BOTSWANA +2673688573 RMK/SARNML)\r\n',
    'international_passenger_charges': 1,
    'item18_rmk': 'string',
    'item18_dep': null,
    'item18_dest': null,
    'item18_reg_num': 'A2ABN',
    'item18_status': null,
    'item18_aircraft_type': null,
    'item18_operator': null,
    'movement_type': 'PENDING',
    'nominal_crossing_cost': null,
    'nominal_crossing_distance': null,
    'other_info': 'PBN/B4 NAV/GPS DOF/160713 REG/A2ABN EET/FAJA0010 OPR/AIR BOTSWANA +2673688573 RMK/SARNML',
    'other_charges_status': null,
    'other_invoice_id': null,
    'parking_charges': null,
    'parking_time': null,
    'passenger_charges_status': null,
    'passenger_invoice_id': 1,
    'passengers_chargeable_domestic': null,
    'passengers_chargeable_intern': null,
    'passengers_joining_adult': null,
    'passengers_transit_adult': null,
    'passengers_child': null,
    'prepaid_amount': null,
    'radar_crossing_cost': null,
    'radar_crossing_distance': null,
    'resolution_errors': null,
    'spatia_fpl_object_id': 191983,
    'status': 'PENDING',
    'source': null,
    'total_charges': null,
    'tower_crossing_distance': 1,
    'tower_crossing_distance_cost': 1,
    'user_crossing_distance': 1,
    'user_crossing_distance_cost': 1,
    'wake_turb': 'M',
    'estimated_elapsed_time': '1234',
    'manually_changed_fields': '',
    'radar_route_text': 'test',
    'atc_log_route_text': 'test',
    'tower_log_route_text': 'test',
    'tasp_charge': 1,
    'cruising_speed_or_mach_number': 'N123',
    'aerodrome_charges': 123,
    'approach_charges': 123,
    'late_arrival_charges': 123,
    'late_departure_charges': 123,
    'exempt_late_departure_charges': 123,
    'exempt_late_arrival_charges': 123,
    'exempt_aerodrome_charges': 123,
    'adhoc_charge_required': false,
    'flightCategory': 0,
    'enroute_result_currency': {
      id: 17,
      currency_code: 'VEF',
      currency_name: 'Bolivars',
      country_code: {
        id: 17,
        country_code: 'VEN',
        country_name: 'Venezuela',
        aircraft_registration_prefixes: [],
        aerodrome_prefixes: [],
        aircraft_registration_prefixes_input: [],
        aerodrome_prefixes_input: []
      },
      decimal_places: 2,
      symbol: 'VEF',
      active: true,
      allow_updated_from_web: true,
      external_accounting_system_identifier: '',
      exchange_rate_target_currency_id: 2
    },
    'enroute_invoice_currency': {
        id: 17,
        currency_code: 'VEF',
        currency_name: 'Bolivars',
        country_code: {
          id: 17,
          country_code: 'VEN',
          country_name: 'Venezuela',
          aircraft_registration_prefixes: [],
          aerodrome_prefixes: [],
          aircraft_registration_prefixes_input: [],
          aerodrome_prefixes_input: []
        },
        decimal_places: 2,
        symbol: 'VEF',
        active: true,
        allow_updated_from_web: true,
        external_accounting_system_identifier: '',
        exchange_rate_target_currency_id: 2
    },
    'arriving_pax_domestic_airport': null,
    'landing_pax_domestic_airport': null,
    'transfer_pax_domestic_airport': null,
    'departing_pax_domestic_airport': null,
    'arriving_child_domestic_airport': null,
    'landing_child_domestic_airport': null,
    'transfer_child_domestic_airport': null,
    'departing_child_domestic_airport': null,
    'exempt_arriving_pax_domestic_airport': null,
    'exempt_landing_pax_domestic_airport': null,
    'exempt_transfer_pax_domestic_airport': null,
    'exempt_departing_pax_domestic_airport': null,
    'loaded_goods': null,
    'discharged_goods': null,
    'loaded_mail': null,
    'discharged_mail': null,
    'status_notes': null,
    'extended_hours_surcharge_currency': null,
    'extended_hours_surcharge': null,
    'tasp_charge_currency': null
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

  it('should be registered', inject((flightMovementManagementService: FlightMovementManagementService) => {
    expect(flightMovementManagementService).not.toEqual(null);
  }));

  describe('list', () => {
    it('should return an array of flight movements', inject((flightMovementManagementService: FlightMovementManagementService) => {

      let expectedResponse = Array<IFlightMovement>();

      flightMovementManagementService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
        .respond(200, expectedResponse);

      httpBackend.flush();

    }));
  });

  describe('create', () => {
    it('should create a single flight movement', inject((flightMovementManagementService: FlightMovementManagementService) => {

      flightMovementManagementService.create(flightMovement);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, flightMovement);

      httpBackend.flush();

    }));
  });

  describe('update', () => {
    it('should be registered', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.update).not.toEqual(null);
    }));

    it('should update a single flight movement', inject((flightMovementManagementService: FlightMovementManagementService) => {

      flightMovement.id = 1;

      flightMovementManagementService.update(flightMovement, flightMovement.id);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${flightMovement.id}`)
        .respond(200, flightMovement);

      httpBackend.flush();

    }));
  });

  // recalculate
  describe('recalculate method', () => {

    it('should be registered', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.recalculate).not.toEqual(null);
    }));

    it('should expect 1 parameter', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.recalculate.length).toEqual(1);
    }));

    it('should recalculate flight movements', inject((flightMovementManagementService: FlightMovementManagementService) => {

      let flightMovements = [1, 2, 3];

      flightMovementManagementService.recalculate(flightMovements);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/recalculate`, flightMovements)
        .respond(200);

      httpBackend.flush();
    }));

  });

  // reconcile
  describe('reconcile method', () => {

    it('should be registered', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.reconcile).not.toEqual(null);
    }));

    it('should expect 1 parameter', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.reconcile.length).toEqual(1);
    }));

    it('should reconcile flight movements', inject((flightMovementManagementService: FlightMovementManagementService) => {

      let flightMovements = [1, 2, 3];

      flightMovementManagementService.reconcile(flightMovements);

      httpBackend
        .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/reconcile`, flightMovements)
        .respond(200);

      httpBackend.flush();
    }));

  });

  // generate invoices
  describe('generate invoices method', () => {

    it('should be registered', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.generateInvoice).not.toEqual(null);
    }));

    it('should expect 1 parameter', inject((flightMovementManagementService: FlightMovementManagementService) => {
      expect(flightMovementManagementService.generateInvoice.length).toEqual(1);
    }));

    it('should generate invoices for flight movements', inject((flightMovementManagementService: FlightMovementManagementService) => {

      let flightMovements = [1, 2, 3];

      flightMovementManagementService.generateInvoice(flightMovements);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}/generate-invoices`, flightMovements)
        .respond(200);

      httpBackend.flush();
    }));

  });

  // tests for getAircraftTypeByLatestRegistrationNumber
  describe('getAircraftTypeByLatestRegistrationNumber method', () => {

    let service, registrationNumber = null;

    beforeEach(() => {

      inject((flightMovementManagementService: FlightMovementManagementService) => {
        service = flightMovementManagementService;
      });

      registrationNumber = 1;

    });

    it('should be registered', () => {
      expect(service.getAircraftTypeByLatestRegistrationNumber).not.toEqual(null);
    });

    it('should expect 1 parameters', () => {
      expect(service.getAircraftTypeByLatestRegistrationNumber.length).toEqual(1);
    });

    it('tracks if any call was made at all', () => {

      spyOn(service, 'getAircraftTypeByLatestRegistrationNumber');

      expect(service.getAircraftTypeByLatestRegistrationNumber.calls.any()).toEqual(false);
      service.getAircraftTypeByLatestRegistrationNumber(registrationNumber);
      expect(service.getAircraftTypeByLatestRegistrationNumber.calls.any()).toEqual(true);
    });

    it('tracks that the method was called', () => {

      spyOn(service, 'getAircraftTypeByLatestRegistrationNumber');

      service.getAircraftTypeByLatestRegistrationNumber(registrationNumber);
      expect(service.getAircraftTypeByLatestRegistrationNumber).toHaveBeenCalled();
    });

    it('tracks all the arguments of its calls', () => {

      spyOn(service, 'getAircraftTypeByLatestRegistrationNumber');

      service.getAircraftTypeByLatestRegistrationNumber(registrationNumber);
      service.getAircraftTypeByLatestRegistrationNumber(registrationNumber);

      expect(service.getAircraftTypeByLatestRegistrationNumber).toHaveBeenCalledWith(registrationNumber);
      expect(service.getAircraftTypeByLatestRegistrationNumber).toHaveBeenCalledWith(registrationNumber);
    });

    it('should be called only once', () => {

      spyOn(service, 'getAircraftTypeByLatestRegistrationNumber');

      service.getAircraftTypeByLatestRegistrationNumber(registrationNumber);
      expect(service.getAircraftTypeByLatestRegistrationNumber.calls.count()).toBe(1);
    });

    it('should respond with status code 200', inject((flightMovementManagementService: FlightMovementManagementService) => {

      let uri = `${endpoint}/aircraftTypeByLatestRegistrationNumber/1`;

      service.getAircraftTypeByLatestRegistrationNumber(registrationNumber);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${uri}`)
        .respond(200);

      httpBackend.flush();
    }));

  });

  // tests for findAllFlightMovementsByAssociatedInvoiceId()
  describe('findAllFlightMovementsByAssociatedInvoiceId method', () => {

    let service, invoiceId = null;

    beforeEach(() => {

      inject((flightMovementManagementService: FlightMovementManagementService) => {
        service = flightMovementManagementService;
      });

      invoiceId = 1;

    });

    it('should be registered', () => {
      expect(service.findAllFlightMovementsByAssociatedInvoiceId).not.toEqual(null);
    });

    it('should expect 1 parameters', () => {
      expect(service.findAllFlightMovementsByAssociatedInvoiceId.length).toEqual(3);
    });

    it('tracks if any call was made at all', () => {

      spyOn(service, 'findAllFlightMovementsByAssociatedInvoiceId');

      expect(service.findAllFlightMovementsByAssociatedInvoiceId.calls.any()).toEqual(false);
      service.findAllFlightMovementsByAssociatedInvoiceId(invoiceId, null);
      expect(service.findAllFlightMovementsByAssociatedInvoiceId.calls.any()).toEqual(true);
    });

    it('tracks that the method was called', () => {

      spyOn(service, 'findAllFlightMovementsByAssociatedInvoiceId');

      service.findAllFlightMovementsByAssociatedInvoiceId(invoiceId);
      expect(service.findAllFlightMovementsByAssociatedInvoiceId).toHaveBeenCalled();
    });

    it('tracks all the arguments of its calls', () => {

      spyOn(service, 'findAllFlightMovementsByAssociatedInvoiceId');

      service.findAllFlightMovementsByAssociatedInvoiceId(invoiceId);
      service.findAllFlightMovementsByAssociatedInvoiceId(invoiceId);

      expect(service.findAllFlightMovementsByAssociatedInvoiceId).toHaveBeenCalledWith(invoiceId);
      expect(service.findAllFlightMovementsByAssociatedInvoiceId).toHaveBeenCalledWith(invoiceId);
    });

    it('should be called only once', () => {

      spyOn(service, 'findAllFlightMovementsByAssociatedInvoiceId');

      service.findAllFlightMovementsByAssociatedInvoiceId(invoiceId);
      expect(service.findAllFlightMovementsByAssociatedInvoiceId.calls.count()).toBe(1);
    });

    it('should respond with status code 200', inject((flightMovementManagementService: FlightMovementManagementService) => {

      let uri = `${endpoint}/invoices/1`;

      service.findAllFlightMovementsByAssociatedInvoiceId(invoiceId);

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}/${uri}?size=20`)
        .respond(200);

      httpBackend.flush();
    }));

  });

  describe('findAllFlightMovementsByAssociatedInvoiceId response', () => {

    let service, response = null;

    beforeEach(() => {

      inject((flightMovementManagementService: FlightMovementManagementService) => {
        service = flightMovementManagementService;
        spyOn(service, 'findAllFlightMovementsByAssociatedInvoiceId').and.returnValue('Response');
      });

      response = service.findAllFlightMovementsByAssociatedInvoiceId(1);

    });

    it('when called returns the requested value', () => {
      expect(response).toEqual('Response');
    });

  });

});
