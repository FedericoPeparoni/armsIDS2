// interface
import { IScFlightCostCalculation } from '../sc-flight-cost-calculation.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'flight-cost-calculation';

export class ScFlightCostCalculationService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IScFlightCostCalculation = {
    aircraft_type: null,
    registration_number: null,
    speed: null,
    estimated_elapsed_time: null,
    dep_aerodrome: null,
    dest_aerodrome: null,
    route: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IScFlightCostCalculation {
    return angular.copy(this._mod);
  }

  public calculate(editable: IScFlightCostCalculation): ng.IPromise<any> {
    return this.restangular.all(this.endpoint).customPOST(editable);
  }

}
