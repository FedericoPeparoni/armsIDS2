// interfaces
import { IAerodromeCategory } from '../aerodrome-category-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoints
export let endpoint = 'aerodromecategories';

export class AerodromeCategoryManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAerodromeCategory = {
    id: null,
    category_name: null,
    international_passenger_fee_adult: null,
    international_passenger_fee_child: null,
    domestic_passenger_fee_adult: null,
    domestic_passenger_fee_child: null,
    domestic_fees_currency: null,
    international_fees_currency: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
