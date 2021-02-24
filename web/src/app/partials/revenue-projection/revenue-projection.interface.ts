export interface IRevenueProjection {
  upper_limit: number;
  domestic_formula: string;
  regional_departure_formula: string;
  regional_arrival_formula: string;
  regional_overflight_formula: string;
  international_departure_formula: string;
  international_arrival_formula: string;
  international_overflight_formula: string;
  w_factor_formula: string;
  domestic_d_factor_formula: string;
  reg_dep_d_factor_formula: string;
  reg_arr_d_factor_formula: string;
  reg_ovr_d_factor_formula: string;
  int_dep_d_factor_formula: string;
  int_arr_d_factor_formula: string;
  int_ovr_d_factor_formula: string;
  charges_passenger: number;
  charges_approach: number;
  charges_aerodrome: number;
  charges_late_arrival: number;
  charges_late_departure: number;
  charges_vol_flights: number;
  charges_vol_passengers: number;
  time_period: string;
  modified_only: string;
  format: string;
}

export interface IRevenueProjectionEnabled {
  upper_limit_enabled?: boolean;
  domestic_formula_enabled?: boolean;
  regional_departure_formula_enabled?: boolean;
  regional_arrival_formula_enabled?: boolean;
  regional_overflight_formula_enabled?: boolean;
  international_departure_formula_enabled?: boolean;
  international_arrival_formula_enabled?: boolean;
  international_overflight_formula_enabled?: boolean;
  w_factor_formula_enabled?: boolean;
  domestic_d_factor_formula_enabled?: boolean;
  reg_dep_d_factor_formula_enabled?: boolean;
  reg_arr_d_factor_formula_enabled?: boolean;
  reg_ovr_d_factor_formula_enabled?: boolean;
  int_dep_d_factor_formula_enabled?: boolean;
  int_arr_d_factor_formula_enabled?: boolean;
  int_ovr_d_factor_formula_enabled?: boolean;
  charges_passenger_enabled?: boolean;
  charges_approach_enabled?: boolean;
  charges_aerodrome_enabled?: boolean;
  charges_late_arrival_enabled?: boolean;
  charges_late_departure_enabled?: boolean;
  charges_vol_flights_enabled?: boolean;
  charges_vol_passengers_enabled?: boolean;
}

export interface IDataValidation {
  formula: string;
  formula_valid: boolean;
  issue: string;
  name: string;
  id_navigation_billing_formula: string;

}
export interface IRevenueProjectionScope {
  editable: IRevenueProjection;
  enabled: IRevenueProjectionEnabled;
  changeStatus: Function;
  doGenerate: Function;
  clearForm: Function;
  addToFormula: Function;
  getTrustedHtml: Function;
  setFocusInput: Function;
  validate: Function;
  getFormulas: Function;
  canGenerate: boolean;
  processing: boolean;
  validated: boolean;
  noFormulas: boolean;
  formula: any;
  formulaError: string;
  data_results: IDataValidation;
}
