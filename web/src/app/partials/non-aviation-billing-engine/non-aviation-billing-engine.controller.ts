// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { INonAviationBillingEngineScope, INonAviationBillingEngine } from './non-aviation-billing-engine.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IExternalChargeCategory } from '../external-charge-category/external-charge-category.interface';
import { IAccount } from '../accounts/accounts.interface';

// services
import { ExternalChargeCategoryService } from '../external-charge-category/service/external-charge-category.service';
import { NonAviationBillingEngineService } from './service/non-aviation-billing-engine.service';
import { ReportsService } from '../reports/service/reports.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { AccountsService } from '../accounts/service/accounts.service';

export class NonAviationBillingEngineController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(public $scope: INonAviationBillingEngineScope, private nonAviationBillingEngineService: NonAviationBillingEngineService,
    private reportsService: ReportsService, private customDate: CustomDate, private externalChargeCategoryService: ExternalChargeCategoryService,
    private systemConfigurationService: SystemConfigurationService, private accountsService: AccountsService) {
    super($scope, nonAviationBillingEngineService);
    super.setup({ refresh: false });
    $scope.getLineItems = (nonAviation: INonAviationBillingEngine) => this.getLineItems(nonAviation);
    $scope.startOfCurrentMonth = moment().startOf('month');
    this.setDefaultDate();

    this.$scope.customDate = this.customDate.returnDateFormatStr(true);

    $scope.$watchGroup(['editable.month', 'editable.year'], () => {
      $scope.isBillingPeriodValid = this.validateBillingPeriod();
    });

    // watch for account change and get invoice currency for selected account
    // this is required as we only pull the minified account list for account dropdown selection
    $scope.$watch('editable.accountId', (accountId: number) => this.updateAccountCurrencyCode(accountId));

    $scope.generateNonAviationInvoice = (format: string, preview: number, accountId: Array<number>, year: object, month: object,
      lineItems: Array<IInvoiceLineItem>) => this.generateNonAviationInvoice(format, preview, accountId, year, month, lineItems);

    externalChargeCategoryService.getNonAviationCategories()
      .then((response: Array<IExternalChargeCategory>) => this.setExternalChargeCategories(response))
      .catch((error: IRestangularResponse) => this.setErrorResponse(error));
  };

  /**
   * Generate a Non-Aviation invoice with the status "new"
   */
  private generateNonAviationInvoice(format: string, preview: number, accountId: Array<number>, year: object, month: object,
    lineItems: Array<IInvoiceLineItem>): void {

    this.reportsService.generateNonAviationInvoice(format, preview, accountId, year, month, lineItems).then(() => {
      this.generateSuccess();
    })
      .catch((error: IRestangularResponse) => this.setErrorResponse(error));
  }

  /**
   * Validate billing period
   */

  private validateBillingPeriod(): boolean {
    return this.$scope.startOfCurrentMonth.isAfter(this.$scope.dateObject);
  }

  /**
   * Populates line items table once month, year, and
   * account have been selected
   *
   * @param  {INonAviationBillingEngine} nonAviation
   * @returns void
   */
  private getLineItems(nonAviation: INonAviationBillingEngine): void {
    this.$scope.editable.month = this.$scope.dateObject.getMonth() + 1;
    this.$scope.editable.year = this.$scope.dateObject.getFullYear();

    if (!(this.$scope.dateObject.getMonth() + 1) || !this.$scope.dateObject.getFullYear() || !nonAviation.accountId
      || (!nonAviation.externalChargeCategory && this.$scope.externalChargeCategories.length > 0)) {
      return;
    }
    this.reportsService.getLineItemsByMonth(this.$scope.dateObject.getMonth() + 1, this.$scope.dateObject.getFullYear(),
      nonAviation.accountId, nonAviation.externalChargeCategory ? nonAviation.externalChargeCategory.id : null)
      .then((data: Array<IInvoiceLineItem>) => {
        for (let i = 0; i < data.length; i++) {
          if (data[i].service_charge_catalogue.charge_basis === 'discount') {
            data.push(data.splice(i, 1)[0]); // set discount to be the last of array
          }
        }

        this.$scope.lineItems = data;
      })
      .catch((error: IRestangularResponse) => this.setErrorResponse(error));
  }

  private setDefaultDate(): void {
    let today = new Date();
    let month = today.getMonth();
    this.$scope.dateObject = new Date(today.getFullYear(), month - 1);
  }

  // callback for successful invoice generation
  private generateSuccess(): void {
    this.$scope.invoiceCreated = true;
    this.$scope.lineItems = null;
    this.$scope.editable.accountId = null;
    this.$scope.editable.currencyCode = null;
  }

  private setExternalChargeCategories(items: Array<IExternalChargeCategory>): void {
    if (!items || items.length === 0) {
      this.$scope.externalChargeCategories = [];
      this.$scope.editable.externalChargeCategory = null;
    } else if (items.length === 1) {
      this.$scope.externalChargeCategories = items;
      this.$scope.editable.externalChargeCategory = items[0];
    } else {
      this.$scope.externalChargeCategories = items;
      this.$scope.editable.externalChargeCategory = null;
    }
  }

  private updateAccountCurrencyCode(accountId: number): void {
    if (accountId) {
      this.accountsService.get(accountId)
        .then((account: IAccount) => this.$scope.editable.currencyCode = account.invoice_currency.currency_code);
    } else {
      this.$scope.editable.currencyCode = null;
    }
  }

}
