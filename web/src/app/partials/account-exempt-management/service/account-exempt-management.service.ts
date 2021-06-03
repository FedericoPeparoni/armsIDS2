// interfaces
import { IAccountExemptManagement } from '../account-exempt-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'account-exemptions';


export class AccountExemptManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAccountExemptManagement = {
    id: null,
    account_id: null,
    account_name: null,
    enroute: 0,
    parking: 0,
    approach_fees_exempt: 0,
    aerodrome_fees_exempt: 0,
    late_arrival: 0,
    late_departure: 0,
    international_pax: 0,
    domestic_pax: 0,
    extended_hours: 0,
    unified_tax: 0,
    flight_notes: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
