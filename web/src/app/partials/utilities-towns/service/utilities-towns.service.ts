// interfaces
import { ITown } from '../utilities-towns.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'utilities-towns-and-villages';

export class UtilitiesTownsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ITown = {
    id: null,
    town_or_village_name: null,
    water_utility_schedule: null,
    residential_electricity_utility_schedule: null,
    commercial_electricity_utility_schedule: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
