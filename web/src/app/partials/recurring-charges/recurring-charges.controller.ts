// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { RecurringChargesService } from './service/recurring-charges.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interface
import { IRecurringCharge, IRecurringChargeScope } from './recurring-charges.interface';
import { CatalogueServiceChargeService } from '../catalogue-service-charge/service/catalogue-service-charge.service';
import { ICatalogueServiceChargeType, ICatalogueServiceChargeTypeSpring } from '../catalogue-service-charge/catalogue-service-charge.interface';

export class RecurringChargesController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IRecurringChargeScope, private recurringChargesService: RecurringChargesService,
    private catalogueServiceChargeService: CatalogueServiceChargeService, private customDate: CustomDate) {
    super($scope, recurringChargesService);

    super.setup({ filter: 'all' });

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.refresh = () => this.refreshOverride();
    $scope.create = (recurringCharge: IRecurringCharge, startDate: string, endDate: string) => this.createOverride(recurringCharge, startDate, endDate);
    $scope.update = (recurringCharge: IRecurringCharge, startDate: string, endDate: string) => this.updateOverride(recurringCharge, startDate, endDate);
    this.getFilterParameters();

    catalogueServiceChargeService.listAll().then((results: ICatalogueServiceChargeTypeSpring) => $scope.catalogueServiceChargeListFiltered = this.setUpFilteredServiceCharges(results.content));
  }

  protected reset(): void {
    if (typeof this.$scope.control !== 'undefined') {
      this.$scope.control.reset();
    }

    super.reset();
  }

  protected edit(recurringCharge: IRecurringCharge): void {

    const d = new Date(recurringCharge.start_date);
    this.$scope.control.setUTCStartDate(d);

    let e = new Date(recurringCharge.end_date);
    this.$scope.control.setUTCEndDate(e);

    super.edit(recurringCharge);

  }

  protected createOverride(recurringCharge: IRecurringCharge, startDate: string, endDate: string): ng.IPromise<IRecurringCharge> {
    recurringCharge.start_date = startDate;
    recurringCharge.end_date = endDate;
    return super.create(recurringCharge);
  }

  protected updateOverride(recurringCharge: IRecurringCharge, startDate: string, endDate: string): ng.IPromise<IRecurringCharge> {
    recurringCharge.start_date = startDate;
    recurringCharge.end_date = endDate;
    return super.update(recurringCharge, recurringCharge.id);
  }

  private setUpFilteredServiceCharges(serviceCharges: ICatalogueServiceChargeType[]): ICatalogueServiceChargeType[] {
    const allowable = ['non-avi', 'utility', 'lease', 'concession'];
    let allowableValues: ICatalogueServiceChargeType[] = [];

    for (let i in serviceCharges) {
      if (allowable.indexOf(serviceCharges[i].invoice_category) !== -1) {
        allowableValues.push(serviceCharges[i]);
      }
    }

    return allowableValues;
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      textFilter: this.$scope.textFilter,
      accountFilter: this.$scope.accountFilter,
      statusFilter: this.$scope.statusFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

}








