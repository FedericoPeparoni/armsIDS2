// interface
import { IFlightRouteExemptionType } from '../flight-route-exemptions.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'exempt-flight-routes';

export class FlightRouteExemptionsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IFlightRouteExemptionType = {
    id: null,
    departure_aerodrome: null,
    destination_aerodrome: null,
    exemption_in_either_direction: false,
    enroute_fees_are_exempt: 0,
    approach_fees_are_exempt: 0,
    aerodrome_fees_are_exempt: 0,
    late_arrival_fees_are_exempt: 0,
    late_departure_fees_are_exempt: 0,
    parking_fees_are_exempt: 0,
    international_pax: 0,
    domestic_pax: 0,
    extended_hours: 0,
    flight_notes: null,
    exempt_route_floor: 0,
    exempt_route_ceiling: 999
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
