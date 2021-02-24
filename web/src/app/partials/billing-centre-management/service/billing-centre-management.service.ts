// interface
import { IBillingCentre, IBillingCentreMinimal } from '../billing-centre-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'billing-centers';

export class BillingCentreManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IBillingCentre = {
    aerodromes: [],
    hq: null,
    id: null,
    invoice_sequence_number: null,
    name: null,
    prefix_invoice_number: null,
    prefix_receipt_number: null,
    receipt_sequence_number: null,
    users: [],
    external_accounting_system_identifier: null,
    iata_invoice_sequence_number: null,
    receipt_cheque_sequence_number: null,
    receipt_wire_sequence_number: null

  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Return list of billing centres IDs and names ONLY
   */
  public findAllMinimalReturn(): ng.IPromise<Array<IBillingCentreMinimal>> {
    return this.restangular.one(`${endpoint}/allMinimal`).get();
  }

  public getBillingCentreByAerodromes(depAd: string, destAd: string): ng.IPromise<IBillingCentre> {
    return this.restangular.one(`${endpoint}/${depAd}/${destAd}`).get();
  }

}
