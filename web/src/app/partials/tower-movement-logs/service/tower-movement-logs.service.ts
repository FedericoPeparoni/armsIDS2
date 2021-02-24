// interface
import { ITowerMovementLogType } from '../tower-movement-logs.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'tower-movement-log';

export class TowerMovementLogsService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: ITowerMovementLogType = {
    id: null,
    date_of_contact: null,
    flight_id: null,
    registration: null,
    aircraft_type: null,
    operator_name: null,
    departure_aerodrome: null,
    departure_contact_time: null,
    destination_aerodrome: null,
    destination_contact_time: null,
    route: null,
    flight_level: null,
    flight_crew: null,
    passengers: null,
    flight_category: null,
    day_of_flight: null,
    departure_time: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public listFlightCategories(): Array<IStaticType> {
    let listFlightCategories = [
      {
        name: 'Scheduled',
        value: 'sch'
      },
      {
        name: 'Non-scheduled',
        value: 'nonsch'
      }
    ];

    return listFlightCategories;
  }
}
