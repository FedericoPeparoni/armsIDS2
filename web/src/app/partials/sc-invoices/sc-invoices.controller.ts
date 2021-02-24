// constants
import { MapFlyToEntityType } from '../map/map.constants';

// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IInvoicesScope, IInvoice } from '../invoices/invoices.interface';
import { IFlightMovement, IFlightMovementSpring } from '../flight-movement-management/flight-movement-management.interface';
import { TransactionsService } from '../transactions/service/transactions.service';
import { ITransaction } from '../transactions/transactions.interface';
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { IUser } from '../users/users.interface';

// services
import { ScInvoicesService } from './service/sc-invoices.service';
import { InvoicesService } from '../invoices/service/invoices.service';
import { FlightMovementManagementService } from '../flight-movement-management/service/flight-movement-management.service';
import { SystemConfigurationService } from '../../partials/system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { UsersService } from '../users/service/users.service';

// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';

export class ScInvoicesController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IInvoicesScope, private scInvoicesService: ScInvoicesService, private invoicesService: InvoicesService,
    private transactionsService: TransactionsService, private flightMovementManagementService: FlightMovementManagementService,
    protected $uibModal: ng.ui.bootstrap.IModalService, private $rootScope: ng.IRootScopeService, private customDate: CustomDate,
    private systemConfigurationService: SystemConfigurationService, private usersService: UsersService) {

    super($scope, scInvoicesService, $uibModal);
    super.setup();

    $scope.inverseExchange = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE);
    $scope.customDate = this.customDate.returnDateFormatStr(false);

    this.usersService.getListLightWebUsers().then((users: Array<IUser>) => {
      $scope.usersList = users;
      this.getFilterParameters();
    });

    $scope.showFlightMovementOnMap = (flightMovement: IFlightMovement) => this.showFlightMovementOnMap(flightMovement);
    $scope.refresh = () => this.refreshOverride();
    $scope.edit = (invoice: IInvoice) => this.editOverride(invoice);

    $scope.getFlightMovementsByInvoiceId = (invoiceId: number, page?: number, size?: number) => this.getFlightMovementsByInvoiceId(invoiceId, page, size)
      .then((listFlightMovements: IFlightMovementSpring) => {
        $scope.listFlightMovements = listFlightMovements;
        $scope.listFlightMovements.number++; // fake the number one higher because it will be decreased in crud.service.ts
        if (!page) {
          this.getTransactionPaymentsByInvoiceId(invoiceId)
            .then((listTransactionPayments: Array<ITransaction>) => $scope.listTransactionPayments = listTransactionPayments);
        }
      }
    );
  }

  // for non-aviation invoices, returns associated line items and transactions
  private getLineItemsByInvoiceId(invoiceId: number): void {
    this.invoicesService.getLineItemsByInvoiceId(invoiceId)
      .then((lineItems: Array<IInvoiceLineItem>) => this.$scope.lineItems = lineItems);
    this.getTransactionPaymentsByInvoiceId(invoiceId)
      .then((listTransactionPayments: Array<ITransaction>) => this.$scope.listTransactionPayments = listTransactionPayments);
  }

  // for aviation invoices, returns a list of flight movements by related invoice id
  private getFlightMovementsByInvoiceId(invoiceId: number, page?: number, size?: number): ng.IPromise<IFlightMovementSpring> {
    return this.flightMovementManagementService.findAllFlightMovementsByAssociatedInvoiceId(invoiceId, page, size);
  }

  private getTransactionPaymentsByInvoiceId(invoiceId: number): ng.IPromise<ITransaction[]> {
    return this.transactionsService.getTransactionPaymentsByInvoiceId(invoiceId);
  }

  private showFlightMovementOnMap(flightMovement: IFlightMovement): void { // broadcasts a single airspace to be shown on map
    this.$rootScope.$broadcast('map.flyToEntity', MapFlyToEntityType.SINGLE_FLIGHT_MOVEMENT, [flightMovement]);
  }

  private editOverride(invoice: IInvoice): void {
    // call parent edit method
    super.edit(invoice);

    // get appropriate line items or flight movements based on type
    invoice.invoice_type !== 'non-aviation'
      ? this.$scope.getFlightMovementsByInvoiceId(invoice.id)
      : this.getLineItemsByInvoiceId(invoice.id);
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
      status: this.$scope.filter,
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      startDate: startDate,
      endDate: endDate,
      account: this.$scope.accountFilter,
      createdByUserId: this.$scope.userFilter,
      flightIdOrRegistration: this.$scope.flightIdOrRegistration,
      billingCentre: this.$scope.billingCentreFilter
    };
  }

  private refreshOverride(): ng.IPromise<any> {
    this.$scope.listFlightMovements = null;
    this.$scope.listTransactionPayments = null;
    this.$scope.lineItems = null;

    this.getFilterParameters();

    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
