// interfaces
import { IReportGeneration } from '../report-generation.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = 'reports/generic';

export class ReportGenerationService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IReportGeneration = {
    report: null,
    used_defined: null,
    used_undefined: null,
    todate: null,
    overdue_interval: null,
    invoices: null,
    credit_type: null,
    summary_interval: null,
    filter_type: null,
    account_ids: null,
    account_new_page: null,
    group_by_aerodrome: null,
    group_by_account: true,
    group_by_owner: null,
    credit_notes: null,
    debit_notes: null,
    status: null,
    include_null_accounts: null,
    fiscal_year: null,
    report_month: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IReportGeneration {
    return angular.copy(this._mod);
  }

  public getMissingInfoErrors(): Array<IStaticType> {
    return [
      {id: 1, name: 'Unresolved aircraft type', value: 'UNKNOWN_AIRCRAFT_TYPE'},
      {id: 2, name: 'No Associated Account', value: 'NO_ASSOCIATED_ACCOUNT'},
      {id: 3, name: 'Zero length billable flight track', value: 'ZERO_LENGTH_BILLABLE_TRACK'},
      {id: 4, name: 'No international passenger count', value: 'MISSING_PASSENGER_INTERNATIONAL_DATA'},
      {id: 5, name: 'No domestic passenger count', value: 'MISSING_PASSENGER_DOMESTIC_DATA'},
      {id: 6, name: 'No parking time', value: 'MISSING_PARKING_TIME'},
      {id: 7, name: 'No flight plan', value: 'FLIGHT_PLAN_MISSING'},
      {id: 8, name: 'No radar track', value: 'RADAR_SUMMARY_CROSSCHECK'},
      {id: 9, name: 'No ATC log', value: 'ATC_LOG_MISSING'},
      {id: 10, name: 'No Tower log', value: 'TOWER_LOG_MISSING'},
      {id: 11, name: 'No passenger service charge return', value: 'MISSING_PASSENGER_SERVICE_CHARGE_RETURN'},
      {id: 12, name: 'Small aircraft withough valid CoA', value: 'EXPIRED_OR_MISSING_COA'}
    ];
  }
}
