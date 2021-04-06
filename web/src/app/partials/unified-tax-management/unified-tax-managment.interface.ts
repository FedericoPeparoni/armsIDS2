
import { ICRUDFormScope } from '../../angular-ids-project/src/helpers/interfaces/crud-form.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IUnifiedTaxManagement {

id?: number;
from_manufacture_year: any;
to_manufacture_year: any;
charge_formula: string;
validity: IValidity;
requireExternalSystemId:string;
w_factor_formula: string;
unified_tax_formulas: Array<IUnifiedTaxFormulas>;
}

export interface IValidity {
  id?: number;
  from_validity_year: any;
  to_validity_year: any;
}

export interface ITuRateManagementScope extends ICRUDFormScope<IUnifiedTaxManagement> {
  requireExternalSystemId: boolean;
}


export interface IUnifiedTaxFormulas {
  id?: number;
  enroute_charge_category?: number;
  flightmovement_category: number;
  formula: any;
  d_factor_formula: string;
}



export interface IUnifiedTaxManagementScope extends ng.IScope {
  ifValidate: boolean;
  reset: () => void;
  isValidate: () => void;
  validate: (unifiedTax: IUnifiedTaxManagement ) => void;
  error: IExtendableError;
  showDWFactor: boolean;
  mtowUnitOfMeasure: string | number;
  formula: string;
}
