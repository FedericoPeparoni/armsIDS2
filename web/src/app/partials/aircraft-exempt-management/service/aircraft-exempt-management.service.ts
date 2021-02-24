// interface
import { IAircraftExemptType } from '../aircraft-exempt-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'aircraft-type-exemptions';

export class AircraftExemptManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAircraftExemptType = {
  id: null,
  aircraft_type: null,
  enroute_fees_exempt: 0,
  late_arrival_fees_exempt: 0,
  late_departure_fees_exempt: 0,
  parking_fees_exempt: 0,
  approach_fees_exempt: 0,
  aerodrome_fees_exempt: 0,
  international_pax: 0,
  domestic_pax: 0,
  extended_hours_fees_exempt: 0,
  flight_notes: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
