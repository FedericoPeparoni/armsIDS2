// interfaces
import { IEnrouteAirNavigationCharge } from '../enroute-air-navigation-charges-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export const endpoint: string = 'enroute-airnavigation-charges';

export class EnrouteAirNavigationChargesManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IEnrouteAirNavigationCharge = {
    id: null,
    mtow_category_upper_limit: null,
    w_factor_formula: null,
    enroute_air_navigation_charge_formulas: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

   // validates formulas, returning which formulas are invalid and why
  public validate(enrouteAirNavigationCharge: IEnrouteAirNavigationCharge): ng.IPromise<any> {
    let copy = angular.copy(enrouteAirNavigationCharge); // copy of object sends to back end with id = null because back end doesn't accept id when updating
    copy.id = null;
    return this.restangular.all(`${endpoint}/validate`).customPOST(copy);
  }
}
