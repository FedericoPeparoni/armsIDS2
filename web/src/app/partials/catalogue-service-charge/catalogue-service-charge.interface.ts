import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IExternalChargeCategory } from '../external-charge-category/external-charge-category.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { ICurrency } from '../currency-management/currency-management.interface';

export interface ICatalogueServiceChargeType {
  id?: number;
  charge_class: string;
  category: string;
  type: string;
  subtype: string;
  description: string;
  charge_basis: string;
  minimum_amount: number;
  maximum_amount: number;
  amount: number;
  invoice_category: string;
  external_accounting_system_identifier: string;
  external_charge_category: IExternalChargeCategory;
  currency: string;
}

export interface ICatalogueServiceChargeScope {
  basisList: Array<IStaticType>;
  categoryList: Array<IStaticType>;
  externalDatabaseList: Array<IStaticType>;
  requireExternalSystemId: boolean;
  refresh: Function;
  textFilter: string;
  pagination: ISpringPageableParams;
  getSortQueryString: () => string;
  filterParameters: object;
  currencies: ICurrency[];
}

export interface ICatalogueServiceChargeTypeSpring {
  content: Array<ICatalogueServiceChargeType>;
}
