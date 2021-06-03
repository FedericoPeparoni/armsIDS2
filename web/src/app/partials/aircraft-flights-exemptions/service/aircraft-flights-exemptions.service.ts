// interface
import { IAircraftFlightsExemptions } from '../aircraft-flights-exemptions.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'exempt-aircraft-flights';

export class AircraftFlightsExemptionsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAircraftFlightsExemptions = {
  id: null,
  aircraft_registration: null,
  flight_id: null,
  enroute_fees_exempt: 0,
  approach_fees_exempt: 0,
  aerodrome_fees_exempt: 0,
  late_arrival_fees_exempt: 0,
  late_departure_fees_exempt: 0,
  parking_fees_exempt: 0,
  international_pax: 0,
  domestic_pax: 0,
  unified_tax: 0,
  extended_hours: 0,
  flight_notes: null,
  exemption_start_date: null,
  exemption_end_date: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
