// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

const AATIS_ENDPOINT = 'aatis_invoice_permits';
const EAIP_ENDPOINT = 'eaip_requisitions';

interface IInvoicePermit {
  permitNumber: string;
}

interface IRequisition {
  reqNumber: string;
  reqCurrency: string;
}

export class ExternalDatabaseInputService extends CRUDService {

  protected restangular: restangular.IService;


  private _mod: any = {
    permitNumber: null,
    reqNumber: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, null);
    this.model = angular.copy(this._mod);
  }

  // eaip

  public getRequisitionFromEAIP(reqNumber: string): ng.IPromise<[IRequisition]> {
    return this.restangular.one(EAIP_ENDPOINT).get({ 'reqNumber': reqNumber });
  }

  // aatis

  public getInvoicePermitFromAATIS(permitNumber: string, currencyCode: string): ng.IPromise<[IInvoicePermit]> {
    return this.restangular.one(AATIS_ENDPOINT).get({ 'permitNumber': permitNumber, 'currencyCode' : currencyCode});
  }

}

