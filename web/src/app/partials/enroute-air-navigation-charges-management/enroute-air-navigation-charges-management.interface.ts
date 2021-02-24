import { IExtendableError, IError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IEnrouteAirNavigationCharge {
  id?: number;
  mtow_category_upper_limit: number;
  w_factor_formula: string;
  enroute_air_navigation_charge_formulas: Array<IEnrouteAirNavigationFormulas>;
}

export interface IEnrouteAirNavigationFormulas {
  id?: number;
  enroute_charge_category?: number;
  flightmovement_category: number;
  formula: any;
  d_factor_formula: string;
}

export interface IEnrouteAirNavigationChargeScope extends ng.IScope {
  ifValidate: boolean;
  reset: () => void;
  isValidate: () => void;
  validate: (enrouteCharge: IEnrouteAirNavigationCharge) => void;
  error: IExtendableError;
  showDWFactor: boolean;
  mtowUnitOfMeasure: string | number;
  formula: string;
}

