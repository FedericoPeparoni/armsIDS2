import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { ISCFlightSearch } from '../sc-flight-search.interface';

export let endpoint: string = 'sc-flight-search';

export class ScFlightSearchService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ISCFlightSearch = {
    id: null,
    account: null,
    item18_reg_num: null,
    flight_id: null,
    date_of_flight: null,
    status: null,
    dep_time: null,
    dep_ad: null,
    dest_ad: null,
    total_charges_usd: null,
    amount_prepaid: null,
    flight_notes: null
};

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
