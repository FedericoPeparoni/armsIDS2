// interfaces
import { IRange, ISchedule } from '../utilities-schedules.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export const endpoint: string = 'utilities-schedules';

export class UtilitiesSchedulesService extends CRUDService {

  protected restangular: restangular.IService;

  translate: any;

  private _mod: ISchedule = {
    schedule_id: null,
    schedule_type: null,
    minimum_charge: null,
    utilities_range_bracket: [{
      id: null,
      schedule_id: null,
      range_top_end: null,
      unit_price: null
    }],
    utilities_water_towns_and_village: [{
      id: null,
      town_or_village_name: null,
      water_utility_schedule: null,
      commercial_electricity_utility_schedule: null,
      residential_electricity_utility_schedule: null
    }],
    residential_electricity_utility_schedule: [{
      id: null,
      town_or_village_name: null,
      water_utility_schedule: null,
      commercial_electricity_utility_schedule: null,
      residential_electricity_utility_schedule: null
    }],
    commercial_electricity_utility_schedule: [{
      id: null,
      town_or_village_name: null,
      water_utility_schedule: null,
      commercial_electricity_utility_schedule: null,
      residential_electricity_utility_schedule: null
    }]
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, $translate: angular.translate.ITranslateService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);

    this.translate = (word: string) => {
    return $translate.instant(word);
    };

  }

  public listScheduleTypes(): Array<IStaticType> {
    return [
      {
        name: this.translate('Water'),
        value: 'WATER'
      },
      {
        name: this.translate('Commercial Electrical'),
        value: 'ELECTRIC_COMM'
      },
      {
        name: this.translate('Residential Electrical'),
        value: 'ELECTRIC_RES'
      }
    ];
  }

  public createRange(scheduleId: number, range: IRange): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/${scheduleId}/range-brackets`).customPOST(range);
  }

  public updateRange(range: IRange): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/range-brackets/${range.id}`).customPUT(range);
  }

  public deleteRange(id: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/range-brackets/${id}`).remove(id);
  }

}
