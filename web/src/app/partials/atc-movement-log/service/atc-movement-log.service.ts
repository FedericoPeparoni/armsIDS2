// interface
import { IATCMovementLogType } from '../atc-movement-log.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'atc-movement-log';

export class AtcMovementLogService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IATCMovementLogType = {
    id: null,
    date_of_contact: null,
    registration: null,
    operator_identifier: null,
    route: null,
    flight_id: null,
    aircraft_type: null,
    departure_aerodrome: null,
    destination_aerodrome: null,
    fir_entry_point: null,
    fir_entry_time: null,
    fir_mid_point: null,
    fir_mid_time: null,
    fir_exit_point: null,
    fir_exit_time: null,
    flight_level: null,
    wake_turbulence: null,
    flight_category: null,
    flight_type: null,
    day_of_flight: null,
    departure_time: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public listFlightTypes(): Array<IStaticType> {
    return [
      {
        name: 'Normal',
        value: 'normal'
      },
      {
        name: 'Delta',
        value: 'delta'
      }
    ];
  }

  public listFlightCategories(): Array<IStaticType> {
    return [
      {
        name: 'Scheduled',
        value: 'sch'
      },
      {
        name: 'Non-scheduled',
        value: 'nonsch'
      }
    ];
  }

}
