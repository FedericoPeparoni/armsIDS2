// interface
import { IRevenueData, IRevenueResponse, IRevenueDataTemplate } from '../revenue-data.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'dbqueries/stats/revenue-statistics';
export let endpointRevenue: string = 'revenue-statistics';

export class RevenueDataService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRevenueData = {
    start_date: null,
    end_date: null,
    analysis_type: null,
    billing_centres: null,
    accounts: null,
    aerodromes: null,
    payment_mode: null,
    charge_class: null,
    charge_category: null,
    charge_type: null,
    temporal_group: null,
    group_by: null,
    sort: null,
    fiscal_year: false
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IRevenueData {
    return angular.copy(this._mod);
  }

  public query(obj: Object): ng.IPromise<IRevenueResponse[]> {
    return this.restangular.all(`${endpoint}`).customPOST(obj);
  }

  public createTemplate(obj: Object): ng.IPromise<IRevenueDataTemplate> {
    return this.restangular.all(`${endpointRevenue}`).customPOST(obj);
  }

  public deleteTemplate(id: number): ng.IPromise<void> {
    return this.restangular.one(`${endpointRevenue}/${id}`).remove();
  }

  public updateTemplate(obj: Object, id: number): ng.IPromise<IRevenueDataTemplate> {
    return this.restangular.one(`${endpointRevenue}/${id}`).customPUT(obj);
  }

  // returns all template names
  public getAllNames(): ng.IPromise<Array<string>> {
    return this.restangular.one(`${endpointRevenue}/getNames/`).get();
  }

  // returns template by name
  public getTemplateByName(name: string): ng.IPromise<IRevenueDataTemplate> {
    return this.restangular.one(`${endpointRevenue}/template-name/${name}`).get();
  }

  displayValues(): Array<IStaticType> {
    return [
      {
        name: 'Revenue (USD)',
        value: 'usd_sum'
      },
      {
        name: 'Revenue (ANSP)',
        value: 'ansp_sum'
      },
      {
        name: 'Occurrences of Item',
        value: 'count'
      }
    ];
  }

  /**
   * Used for `Group By` as well as fields to ignore
   */
  public groupByValues(): Array<IStaticType> {
    return [
      {
        id: 1,
        name: 'Analysis Type',
        value: 'analysis_type'
      },
      {
        id: 2,
        name: 'Billing Centre',
        value: 'billing_centre'
      },
      {
        id: 3,
        name: 'Account',
        value: 'account_name'
      },
      {
        id: 4,
        name: 'Aerodrome',
        value: 'aerodrome_name'
      },
      {
        id: 5,
        name: 'Payment Mode',
        value: 'payment_mode'
      },
      {
        id: 6,
        name: 'Charge Class',
        value: 'charge_class'
      },
      {
        id: 7,
        name: 'Charge Category',
        value: 'category'
      },
      {
        id: 8,
        name: 'Charge Type',
        value: 'type'
      }
    ];
  }
}

