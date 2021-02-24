import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IInvoiceTemplate {
  id?: number;
  invoice_template_name: string;
	invoice_category: string;
	template_document: string;
}

export interface IInvoiceTemplateManagementScope {
  categories: Array<IStaticType>;
  getInvoiceCategoryName: Function;
  pattern?: string;
  resetTemplate: Function;
  refreshOverride: Function;
  getSortQueryString: Function;
  textFilter: string;
  pagination: ISpringPageableParams;
  filterParameters: object;
}
