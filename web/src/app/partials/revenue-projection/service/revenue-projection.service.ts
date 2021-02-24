// interface
import { IRevenueProjection } from '../revenue-projection.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { EnrouteAirNavigationChargesManagementService } from
  '../../../partials/enroute-air-navigation-charges-management/service/enroute-air-navigation-charges-management.service';

export let endpoint: string = 'stats/revenue-projection';

export class RevenueProjectionService extends CRUDService {
  enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService;

  protected restangular: restangular.IService;

  private _mod: IRevenueProjection = {
    upper_limit: 900,
    domestic_formula: '',
    regional_departure_formula: '',
    regional_arrival_formula: '',
    regional_overflight_formula: '',
    international_departure_formula: '',
    international_arrival_formula: '',
    international_overflight_formula: '',
    w_factor_formula: '',
    domestic_d_factor_formula: '',
    reg_dep_d_factor_formula: '',
    reg_arr_d_factor_formula: '',
    reg_ovr_d_factor_formula: '',
    int_dep_d_factor_formula: '',
    int_arr_d_factor_formula: '',
    int_ovr_d_factor_formula: '',
    charges_passenger: 0,
    charges_approach: 0,
    charges_aerodrome: 0,
    charges_late_arrival: 0,
    charges_late_departure: 0,
    charges_vol_flights: 0,
    charges_vol_passengers: 0,
    time_period: 'MONTH',
    modified_only: 'yes',
    format: 'PDF'
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);

    this.enrouteAirNavigationChargesManagementService = enrouteAirNavigationChargesManagementService;
  }

  public doGenerate(data: IRevenueProjection): ng.IPromise<any> {
    return this.restangular.one(`/stats/revenue-projection`).withHttpConfig({ responseType: 'arraybuffer' }).customPOST(data);
  }

  public validateSingle(data: IRevenueProjection): ng.IPromise<any> {
    return this.restangular.one(`/enroute-air-navigation-charges/validate/new`).customPOST(data);
  }

  public getFormulas(upperLimit: number): ng.IPromise<any> {
    return this.restangular.one(`/enroute-air-navigation-charges/upperlimit`).customGET('', {'upperLimit': upperLimit});
  }
}
