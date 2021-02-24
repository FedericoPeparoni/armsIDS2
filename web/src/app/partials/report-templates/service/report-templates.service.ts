// interface
import { IReportTemplate } from '../report-templates.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'report-templates';

export class ReportTemplatesService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IReportTemplate = {
    id: null,
    report_name: null,
    sql_query: null,
    parameters: null,
    template_document: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
