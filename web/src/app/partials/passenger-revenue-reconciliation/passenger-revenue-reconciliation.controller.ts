// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import {
  IPassengerRevenueReconciliationScope,
  IPassengerRevenueReconciliation,
  IPassengerRevenueReconciliationResponse
} from './passenger-revenue-reconciliation.interface';

// services
import { PassengerServiceChargeReturnService } from '../passenger-service-charge-return/service/passenger-service-charge-return.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export class PassengerRevenueReconciliationController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(public $scope: IPassengerRevenueReconciliationScope,
    private passengerServiceChargeReturnService: PassengerServiceChargeReturnService,
    private systemConfigurationService: SystemConfigurationService, private customDate: CustomDate) {

    super($scope, passengerServiceChargeReturnService);

    super.setup({ refresh: false });

    this.$scope.customDate = this.customDate.returnDateFormatStr(true);

    $scope.executePSCR = (start_date: Date, end_date: Date) => this.executePSCR(start_date, end_date);
    this.setCurrencyLabels();
  }

  /**
   * Send date range of flights to check against PSCR
   */
  private executePSCR(start_date: Date, end_date: Date): void {
    this.$scope.reconcilePSCRJob = false;

    let params: IPassengerRevenueReconciliation = {
      start_date: start_date.toISOString().substr(0, 10),
      end_date: end_date.toISOString().substr(0, 10),
      dom_pay: this.$scope.dom_pay,
      itl_pay: this.$scope.itl_pay
    };

    this.passengerServiceChargeReturnService.executePSCR(params)
    .then((resp: IPassengerRevenueReconciliationResponse) => {
      this.$scope.reconcilePSCRJob = true;
      this.$scope.reconcileResponse = resp;
    })
    .catch((error: IRestangularResponse) => this.setErrorResponse(error));
  }

  /**
   * Sets the currency (ANNSP / USD)
   * for Domestic and International
   * Payment labels
   */
  private setCurrencyLabels(): void {
    const ANSP = 'ANSP';
    const USD = 'USD';

    // get ANSP currency / dom & intl currency options
    let anspCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    let domPaxCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DOM_PAX_CURRENCY);
    let intlPaxCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.INTL_PAX_CURRENCY);

    // set dom & intl currency
    this.$scope.domPaxCurrency = domPaxCurrency === ANSP ? anspCurrency : USD;
    this.$scope.intlPaxCurrency = intlPaxCurrency === ANSP ? anspCurrency : USD;
  }
}
