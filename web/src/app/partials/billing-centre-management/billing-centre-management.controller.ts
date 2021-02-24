// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { BillingCentreManagementService } from './service/billing-centre-management.service';

// interfaces
import { IBillingCentreScope, IBillingCentreSpring, IBillingCentre } from './billing-centre-management.interface';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class BillingCentreManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IBillingCentreScope, private billingCentreManagementService: BillingCentreManagementService,
              private systemConfigurationService: SystemConfigurationService) {
    super($scope, billingCentreManagementService);
    super.setup();

    billingCentreManagementService.listAll().then((data: IBillingCentreSpring) => $scope.hqIsSet = this.isHeadquarterSet(data.content)); // finds out if there is currently an hq
    $scope.checkHq = (editable: IBillingCentre) => billingCentreManagementService.listAll().then((data: IBillingCentreSpring) => {
      $scope.hqIsSet = this.isHeadquarterSet(data.content);
      this.checkHq(editable);
    });

    $scope.edit = (editable: IBillingCentre) => {
      super.edit(editable); $scope.original = angular.copy(editable);
      $scope.checkHq($scope.original);
    };

    // set scope value for require external system identifier
    $scope.requireExternalSystemId = this.requireExternalSystemId();
    $scope.iataInvoiceSeparatedSeqNumber = this.iataInvoiceSeparatedSeqNumber();
    $scope.receiptSeqNumberByPayMechanism = this.receiptSeqNumberByPayMechanism();
    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();
  }

  private isBillingCentre(centre: any): centre is IBillingCentre {
    return centre.id !== undefined; // this typeguard is used for validation below
  }

  private isHeadquarterSet(billingCentres: Array<IBillingCentre>): boolean | IBillingCentre {
    for (let centre of billingCentres) {
      if (centre.hq) {
        return centre;
      }
    }
    return false;
  }

  private checkHq(centre: IBillingCentre): void { // checks hq upon every change in headquarters
    this.$scope.updateWarning = null;
    this.$scope.createWarning = null;
    this.$scope.deleteWarning = null;

    if ((this.$scope.hqIsSet && centre.hq && this.$scope.original && this.isBillingCentre(this.$scope.hqIsSet) && this.$scope.original.id !== this.$scope.hqIsSet.id) ||
      (!this.$scope.original && centre.id === null && centre.hq) ||
      (this.$scope.original && !this.$scope.hqIsSet && centre.hq)) { // call on updating a centre to the hq when previous hq already exists
      this.$scope.updateWarning = 'The billing centre headquarters designation is being changed to this centre.';
    }

    if (!this.$scope.hqIsSet && !centre.hq) { // call on creating a centre without an hq when no other hq exists
      this.$scope.createWarning = 'A billing centre headquarter is not defined.';
    }

    if (this.$scope.hqIsSet && this.$scope.original && this.isBillingCentre(this.$scope.hqIsSet) && this.$scope.original.id === this.$scope.hqIsSet.id) { // call on deleting the billing centre hq
      this.$scope.deleteWarning = 'Are you sure you want to delete the headquarters billing centre?';
    }

    if (this.$scope.hqIsSet && this.$scope.original && this.isBillingCentre(this.$scope.hqIsSet) && this.$scope.original.id === this.$scope.hqIsSet.id && !centre.hq) { // call on changing hq from true to false
      this.$scope.updateWarning = 'Are you sure you want change this centre so that it is no longer headquarters?';
    }
  }

  /**
   * Get required external system id flag from system configuration.
   *
   * @returns boolean true if required
   */
  private requireExternalSystemId(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_BILLING_CENTRE_EXTERNAL_SYSTEM_ID);
  }

  /**
   * Get IATA Invoice sequence number flag from system configuration.
   *
   * @returns boolean true if required
   */
  private iataInvoiceSeparatedSeqNumber(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.USE_ADDITIONAL_INVOICES_NUMBER);
  }

  /**
   * Get flag from system configuration to use or not receipts sequence number by payment mechanism .
   *
   * @returns boolean true if required
   */
  private receiptSeqNumberByPayMechanism(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.USE_RECEIPT_NUMBER_BY_PAYMENT_MECHANISM);
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      searchFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
