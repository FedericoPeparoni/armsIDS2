// interfaces
import { ICRUDFormScope } from '../../angular-ids-project/src/helpers/interfaces/crud-form.interface';
import { ICurrency } from '../currency-management/currency-management.interface';

export interface IBankAccount {
  id?: number;
  name: string;
  number: string;
  version: number;
  currency: ICurrency;
  external_accounting_system_id: string;
}

export interface IBankAccountScope extends ICRUDFormScope<IBankAccount> {
  requireExternalSystemId: boolean;
}
