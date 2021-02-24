// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// interfaces
import { IAerodromeOperationalHours } from '../aerodrome-operational-hours.interface';

export const endpoint: string = 'aerodrome-operational-hours';

export class AerodromeOperationalHoursService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAerodromeOperationalHours = {
    id: null,
    aerodrome: null,
    operational_hours_monday: null,
    operational_hours_tuesday: null,
    operational_hours_wednesday: null,
    operational_hours_thursday: null,
    operational_hours_friday: null,
    operational_hours_saturday: null,
    operational_hours_sunday: null,
    operational_hours_holidays1: null,
    operational_hours_holidays2: null,
    holiday_dates_holidays1: null,
    holiday_dates_holidays2: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
