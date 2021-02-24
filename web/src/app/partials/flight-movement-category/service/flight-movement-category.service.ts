// interfaces
import { IFlightMovementCategory } from '../flight-movement-category.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export const endpoint: string = 'flightmovement-categories';

export class FlightMovementCategoryService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IFlightMovementCategory = {
    id: null,
    name: null,
    sort_order: null,
    short_name: null,
    enroute_currency_calculated: null,
    enroute_currency_invoice: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Return all flight movement categories except OTHER
   */
  public getFlightMovementCategoryForAviationInvoice(): ng.IPromise<Array<IFlightMovementCategory>> {
    return this.restangular.one(`${endpoint}/aviation-invoice`).get();
  }
}
