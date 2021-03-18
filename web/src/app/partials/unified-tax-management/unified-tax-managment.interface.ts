
import { ICRUDFormScope } from '../../angular-ids-project/src/helpers/interfaces/crud-form.interface';

export interface IUnifiedTaxManagement {

id?: number;
from_manufacture_year: string;
to_manufacture_year: string;
rate: number;
validity: IValidity;
requireExternalSystemId:string;
}

export interface IValidity {
  id?: number;
  from_validity_year: string;
  to_validity_year: string;
}

export interface ITuRateManagementScope extends ICRUDFormScope<IUnifiedTaxManagement> {
  requireExternalSystemId: boolean;
}
