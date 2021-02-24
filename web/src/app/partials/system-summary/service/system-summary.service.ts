// interfaces
import { ISystemSummary } from '../system-summary.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'system-summary';

export class SystemSummaryService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ISystemSummary = {
    flight_movement_aircraft_type: null,
    flight_movement_all: null,
    flight_movement_blacklisted_account: null,
    flight_movement_blacklisted_movement: null,
    flight_movement_categories: null,
    flight_movement_domestic_active_account: null,
    flight_movement_international_active_account: null,
    flight_movement_latest: null,
    flight_movement_inside: null,
    flight_movement_outside: null,
    flight_movement_parking_time_domestic: null,
    flight_movement_parking_time_internationa_arrivals: null,
    flight_movement_parking_time_total: null,
    flight_movement_rejected: null,
    flight_movement_unknown_aircraft_type: null,
    outstanding_bill: null,
    overdue_bill: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
