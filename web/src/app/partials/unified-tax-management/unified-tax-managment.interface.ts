
import { ICRUDFormScope } from '../../angular-ids-project/src/helpers/interfaces/crud-form.interface';

export interface IUnifiedTaxManagement {

id?: number;
fromManufactureYear: string;
toManufactureYear: string;
ars: number;
requireExternalSystemId:string;
}

export interface IValidity {
  id?: number;
  fromValidityYear: string;
  toValidityYear: string;
}

export interface ITuRateManagementScope extends ICRUDFormScope<IUnifiedTaxManagement> {
  requireExternalSystemId: boolean;
}
