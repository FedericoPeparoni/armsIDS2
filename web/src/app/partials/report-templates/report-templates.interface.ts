import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IReportTemplate {
  id?: number;
  report_name: string;
  sql_query: string;
  parameters: string;
  template_document: string;
}

export interface IIReportTemplateScope {
  pattern?: string;
  filterParameters: object;
  textFilter: string;
  pagination: ISpringPageableParams;
  getSortQueryString: Function;
  refreshOverride: Function;
}
