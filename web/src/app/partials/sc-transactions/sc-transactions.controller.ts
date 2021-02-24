// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IInvoice } from '../invoices/invoices.interface';
import { ITransactionsScope } from '../transactions/transactions.interface';

// services
import { ScTransactionsService } from './service/sc-transactions.service';
import { TransactionsService } from '../transactions/service/transactions.service';
import { SysConfigBoolean } from '../../angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class ScTransactionsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ITransactionsScope, private scTransactionsService: ScTransactionsService, private transactionsService: TransactionsService,
    private sysConfigBoolean: SysConfigBoolean, private systemConfigurationService: SystemConfigurationService, private customDate: CustomDate) {
    super($scope, scTransactionsService);
    super.setup();

    $scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.getInvoicesByTransactionId = (id: number) => scTransactionsService.getInvoicesByTransactionId(id).then((data: IInvoice) => $scope.listOfInvoices = data);
    $scope.inverseExchange = sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE));
    this.getFilterParameters();
    $scope.refreshList = () => this.refreshList();
  }

  private getFilterParameters(): void {
    let startDate: string;
    let endDate: string;

    if (this.$scope.control && this.$scope.control.getUTCStartDate()) {
      startDate = this.$scope.control.getUTCStartDate().toISOString().substr(0, 10);
    }

    if (this.$scope.control && this.$scope.control.getUTCEndDate()) {
      endDate = this.$scope.control.getUTCEndDate().toISOString().substr(0, 10);
    }

    this.$scope.filterParameters = {
      searchFilter: this.$scope.textFilter,
      exportedFilter: this.$scope.exportedFilter === true ? false : null, // since "non-exported" only set false when checked
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      startDate: startDate,
      endDate: endDate,
      account: this.$scope.accountFilter
    };
  }

  private refreshList(): ng.IPromise<any> {
    this.getFilterParameters();
    return this.$scope.refresh(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}

