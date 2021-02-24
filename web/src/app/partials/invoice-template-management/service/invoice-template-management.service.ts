// interface
import { IInvoiceTemplate } from '../invoice-template-management.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'invoice-templates';

export class InvoiceTemplateManagementService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IInvoiceTemplate = {
    id: null,
    invoice_template_name: null,
    invoice_category: null,
    template_document: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public resetTemplate(invoiceCategory: string): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/${invoiceCategory}/reset`).get();
  }
}
