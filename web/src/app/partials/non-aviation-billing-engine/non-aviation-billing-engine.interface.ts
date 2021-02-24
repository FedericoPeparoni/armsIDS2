// interfaces
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { IExternalChargeCategory } from '../external-charge-category/external-charge-category.interface';

export interface INonAviationBillingEngine {
  month: number;
  year: number;
  accountId: number;
  externalChargeCategory: IExternalChargeCategory;
  currencyCode: string;
}

export interface INonAviationBillingEngineScope extends ng.IScope {
  getLineItems: Function;
  validateInvoiceLineItem: Function;
  edit: Function;
  enableButtons: boolean;
  lineItem: IInvoiceLineItem;
  lineItems: Array<IInvoiceLineItem>;
  error: IExtendableError;
  isWater: boolean;
  isElectric: boolean;
  aerodromeList: IAerodrome[];
  editable: INonAviationBillingEngine;
  dateObject: Date;
  startOfCurrentMonth: any;
  isBillingPeriodValid: boolean;
  externalChargeCategories: Array<IExternalChargeCategory>;
}
