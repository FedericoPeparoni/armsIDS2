// interfaces
import { IPassengerServiceChargeReturn } from '../sc-passenger-service-charge-return.interface';
import { IPassengerRevenueReconciliation } from '../../passenger-revenue-reconciliation/passenger-revenue-reconciliation.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'sc-passenger-service-charge-return';

export class ScPassengerServiceChargeReturnService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IPassengerServiceChargeReturn = {
    id: null,
    flight_id: null,
    day_of_flight: null,
    departure_time: null,
    transit_passengers: 0,
    joining_passengers: 0,
    children: 0,
    chargeable_itl_passengers: 0,
    chargeable_domestic_passengers: 0,
    document_filename: null,
    mime_type: null,
    document: null,
    document_filename2: null,
    arriving_pax_domestic_airport: null,
    landing_pax_domestic_airport: null,
    transfer_pax_domestic_airport: null,
    departing_pax_domestic_airport: null,
    arriving_child_domestic_airport: null,
    landing_child_domestic_airport: null,
    transfer_child_domestic_airport: null,
    departing_child_domestic_airport: null,
    exempt_arriving_pax_domestic_airport: null,
    exempt_landing_pax_domestic_airport: null,
    exempt_transfer_pax_domestic_airport: null,
    exempt_departing_pax_domestic_airport: null,
    loaded_goods: null,
    discharged_goods: null,
    loaded_mail: null,
    discharged_mail: null,
    account: null,
    account_id: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Send date range of flights to check against PSCR
   */
  public executePSCR(params: IPassengerRevenueReconciliation): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/reconciliation`).customPOST(null, '', params);
  }

}
