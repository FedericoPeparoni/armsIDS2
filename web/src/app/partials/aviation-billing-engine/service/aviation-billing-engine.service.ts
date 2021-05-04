// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { DownloadService } from '../../../angular-ids-project/src/helpers/services/download.service';

// interface
import { IAviationBillingEngine } from '../aviation-billing-engine.interface';
import { IJobStatus } from '../../aviation-billing-engine/aviation-billing-engine.interface';

export let endpoint: string = 'flm-job';

export class AviationBillingEngineService extends CRUDService {
  protected restangular: restangular.IService;

  private _mod: IAviationBillingEngine = {
    month: null,
    account_id_list: null,
    year: null,
    flights: null,
    incompleteFlights: null,
    iata_status: 'iata',
    sort: null,
    userBillingCenterOnly: null,
    billing_interval: null,
    end_date: null,
    start_date: null,
    start_date_open: null,
    end_date_open: null,
    processStartDate: null,
    processEndDate: null,
    flightCategory: null,
    preview: null,
    endDateInclusive: null,
    account_type: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private $sce: ng.ISCEService, private downloadService: DownloadService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  // reconciliation methods by month and year
  public executeRecalulation({ processStartDate, processEndDate, account_id_list, flightCategory }: IAviationBillingEngine): ng.IPromise<any> {
    let url: string = '';

    if (flightCategory) {
      url = `${endpoint}/reconciliation/${processStartDate}/${processEndDate}/?flightMovementCategoryId=${flightCategory}`;
    } else {
      url = `${endpoint}/reconciliation/${processStartDate}/${processEndDate}/`;
    };

    // execute a recalculation job
    return this.restangular.all(url).customPOST(account_id_list).then((data: any) => {
      return this.restangular.stripRestangular(data);
    });
  }

  // get status of bulk recalculation
  public getStatusForRecalulations(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/reconciliation`).get().then((data: IJobStatus) => {
      return this.restangular.stripRestangular(data);
    });
  }

  // cancels a recalculation job
  public cancelRecalculation(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/reconciliation`).remove();
  }

  // start a generate invoice or preview invoice report polling request
  public executeGeneration(editable: any): ng.IPromise<any> {
    let url: string = `async-reports/aviation-invoice?billingInterval=${editable.billing_interval}&endDateInclusive=${editable.endDateInclusive}&` +
      `format=pdf&sort=${editable.sort}&startDate=${editable.processStartDate}&` +
      `userBillingCenterOnly=${editable.userBillingCenterOnly}&preview=${editable.preview}&iataInvoice=${editable.iataInvoice}`;

    // if flight movement category provided, add to params
    if (editable.flightCategory) {
      url += `&flightCategory=${editable.flightCategory}`;
    }

    return this.Restangular.one(url).customPOST(editable.account_id_list).then((data: IJobStatus) => {
      return this.restangular.stripRestangular(data);
    });
  }

  // get status of bulk reconciliation
  public getStatusForGeneration(): ng.IPromise<any> {
    return this.restangular.one(`async-reports/aviation-invoice`).get().then((data: IJobStatus) => {
      return this.restangular.stripRestangular(data);
    });
  }

  // cancel a generate invoice or preview invoice report polling request
  public cancelGeneration(): ng.IPromise<any> {
    return this.restangular.one(`async-reports/aviation-invoice`).remove();
  }

  // download and show a pdf preview of the aviation billing invoice
  public downloadInvoice(preview: number, iataInvoice: boolean = false): any {
    const req = this.restangular.withConfig((RestangularConfigurer: restangular.IProvider) => RestangularConfigurer.setFullResponse(true));
    const url = `async-reports/aviation-invoice/download?format=pdf&preview=${preview === 1}&iata=${iataInvoice === true}`;

    return req.one(url).withHttpConfig({ responseType: 'arraybuffer' }).get().then((resp: any) => {

      let result: any = null;

      // if not preview, download result and return null to clear preview
      // else display result as preview
      if (resp.status === 200) {
        if (preview === 0) {
          this.downloadService.getDownload(resp);
        } else {
          result = this.downloadService.getPreview(resp);
        }
      }

      return result;
    });
  }

  // validates flights by period
  // this endpoint is only used for aviation billing engine, not fm
  // back end expects true/false for iata value
  public validate(editable: IAviationBillingEngine, startDate: string, endDateInclusive: string): ng.IPromise<any> {
    const params: Object = {
      endDateInclusive: endDateInclusive,
      startDate: startDate,
      userBillingCenterOnly: editable.userBillingCenterOnly,
      iata: editable.iata_status === 'iata',
      flightCategory: editable.flightCategory
    };

    return this.restangular.one('flightmovements/validations/byParams').customPOST(editable.account_id_list || [], '', params);
  }

  // required because of typescript:
  // the left-hand side of an arithmetic operation must be of type 'any', 'number' or an enum type.
  // does not allow for a vanilla (http://vanilla-js.com/) expression in the form: return new Date() * 1
  public returnEpochTime(): number {
    const date: any = new Date();

    return date * 1;
  }

  /**
   * Below are the recalculation and
   * reconciliation methods a list of
   * maccount ids. They are used
   * in flight movement management
   */

  // reconciliation methods by flight movement ids
  // execute a reconciliation job
  public executeSpecificReconciliation(ids: string): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/reconciliation/item/${ids}`).customPOST();
  }

  // get status of bulk reconciliation
  public getStatusForSpecificReconciliation(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/reconciliation/item`).get();
  }

  // cancels a reconciliation job
  public cancelSpecificReconciliation(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/reconciliation/item`).remove();
  }

  // recalculation methods by flight movement ids
  // execute a recalculation job
  public executeSpecificRecalculation(ids: string): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/recalculation/item/${ids}`).customPOST();
  }

  // get status of bulk recalculation
  public getStatusForSpecificRecalculate(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/recalculation/item`).get();
  }

  // cancels a recalculation job
  public cancelSpecificRecalculation(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/recalculation/item`).remove();
  }



}
