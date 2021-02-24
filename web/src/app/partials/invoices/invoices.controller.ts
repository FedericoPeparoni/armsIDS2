// constants
import { MapFlyToEntityType } from '../map/map.constants';
import { EditableType } from './invoices.constants';

// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IInvoicesScope, IInvoice } from './invoices.interface';
import { IFlightMovement, IFlightMovementSpring } from '../flight-movement-management/flight-movement-management.interface';
import { TransactionsService } from '../transactions/service/transactions.service';
import { ITransaction } from '../transactions/transactions.interface';
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// services
import { InvoicesService } from './service/invoices.service';
import { InvoiceStateTypeService } from '../invoice-state-type/service/invoice-state-type.service';
import { FlightMovementManagementService } from '../flight-movement-management/service/flight-movement-management.service';
import { FlightType } from '../flight-types/service/flight-types.service';
import { SystemConfigurationService } from '../../partials/system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { UsersService } from '../users/service/users.service';
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';
import { IUser, IUserLight } from '../users/users.interface';

export class InvoicesController extends CRUDFileUploadController {

  private exportSupportType: Object = {};

  /* @ngInject */
  constructor(protected $scope: IInvoicesScope, private invoicesService: InvoicesService, private invoiceStateTypeService: InvoiceStateTypeService,
    private transactionsService: TransactionsService, private flightMovementManagementService: FlightMovementManagementService,
    protected $uibModal: ng.ui.bootstrap.IModalService, private $rootScope: ng.IRootScopeService, private $state: angular.ui.IStateService,
    private systemConfigurationService: SystemConfigurationService, private customDate: CustomDate, private usersService: UsersService) {
    super($scope, invoicesService, $uibModal);
    super.setup({ refresh: false });

    $scope.inverseExchange = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE);
    $scope.manualApproval = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_INVOICE_MANUAL_APPROVAL);
    $scope.manualPublishing = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_INVOICE_MANUAL_PUBLISHING);
    $scope.showProforma = LocalStorageService.getBooleanFromValueByName(`SystemConfiguration:${SysConfigConstants.PROFORMA_INVOICE_SUPPORT}`);

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    this.usersService.getListLightInternalUsers().then((users: Array<IUserLight>) => {
      $scope.usersList = users;
      this.usersService.current.then((user: IUser) => {
        $scope.userFilter = $scope.usersList.find((item: any) => item.name === user.name).id;
        super.list({}, `createdByUserId=${user.id}`);
        this.getFilterParameters();
      });
    });

    invoiceStateTypeService.listAll().then((listInvoiceTypes: Array<string>) => $scope.statesList = listInvoiceTypes); // retrieve list of states
    this.flightMovementManagementService = flightMovementManagementService;
    this.transactionsService = transactionsService;
    $scope.getFlightType = (flightType: string) => FlightType[flightType];
    $scope.updateState = (id: number, invoiceStatus: string) => this.updateState(id, invoiceStatus);
    $scope.showFlightMovementOnMap = (flightMovement: IFlightMovement) => this.showFlightMovementOnMap(flightMovement);

    $scope.getFlightMovementsByInvoiceId = (invoiceId: number, page?: number, size?: number) => this.getFlightMovementsByInvoiceId(invoiceId, page, size)
      .then((listFlightMovements: IFlightMovementSpring) => {
        $scope.listFlightMovements = listFlightMovements;
        $scope.listFlightMovements.number++; // fake the number one higher because it will be decreased in crud.service.ts
        if (!page) {
          this.getTransactionPaymentsByInvoiceId(invoiceId)
            .then((listTransactionPayments: Array<ITransaction>) => $scope.listTransactionPayments = listTransactionPayments);

          this.getTransactionByInvoiceId(invoiceId)
            .then((listTransactions: Array<ITransaction>) => this.$scope.listTransactions = listTransactions);
        }
      });

      $scope.openAccount = (id: string, permission: boolean)  => {
        if (permission) {
          this.$state.go('main.accounts', { accountId: id });
        }
      };

    $scope.exportSupport = false;
    $scope.exportInProcess = false;
    $scope.selectedItems = {};

    $scope.exportAllInvoices = () => this.exportAllInvoices();
    $scope.exportSelectedInvoices = (selected: any) => this.exportSelectedInvoices(selected);
    $scope.isExportSupport = (invoice: IInvoice) => this.isExportSupport(invoice);
    $scope.isSelectedItems = (selected: any) => this.isSelectedItems(selected);
    $scope.setExportSupportType = (type: string) => this.setExportSupportType(type);
    $scope.refreshList = () => this.refreshList();

    // default to FLIGHT_MOVEMENT type due to flight movement table directive selection logic
    $scope.edit = (data: Object, type: EditableType = EditableType.FLIGHT_MOVEMENT) => this.editOverride(data, type);

    this.invoicesService.exportSupport()
      .then((response: boolean) => $scope.exportSupport = response);

    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
    $scope.payInvoice = (invoice: IInvoice) => this.payInvoice(invoice);
    $scope.enableVoid = (invoice: IInvoice) => this.enableVoid(invoice);
    $scope.voidInvoice = (invoice: IInvoice) => this.voidInvoice(invoice);
  }

  /**
   * Edit override as flight movement form directive expects 'edit' to be specfiically for
   * flight movement selection only.
   * 
   * @param data date for object being editted
   * @param type data object type
   */
  private editOverride(data: Object, type: EditableType = EditableType.INVOICE): void {

    // only call parent edit method if INVOICE data type
    // the rest of the data types are ignored
    if (type === EditableType.INVOICE) {
      this.editInvoice(data as IInvoice);
    }
  }

  /**
   * Edit method for selecting an invoice object.
   * 
   * @param data data for invoice object being editted
   */
  private editInvoice(invoice: IInvoice): void {

    // call parent edit method
    super.edit(invoice);

    // get appropriate line items or flight movements based on type
    invoice.invoice_type !== 'non-aviation'
      ? this.$scope.getFlightMovementsByInvoiceId(invoice.id)
      : this.getLineItemsByInvoiceId(invoice.id);
  }

  private updateState(id: number, invoiceStatus: string): ng.IPromise<any> {
    return this.invoicesService.updateInvoiceStatus(id, invoiceStatus)
      .then(() => this.refreshList().then(() => {
        this.updateEditable();
        if (invoiceStatus === 'void') {
          this.getFlightMovementsByInvoiceId(id);
        }
      }))
      .catch((response: IRestangularResponse) => this.setErrorResponse(response));
  }

  private voidInvoice(invoice: IInvoice): ng.IPromise<any> {
    if (invoice.invoice_state_type === 'PUBLISHED') {
      return this.voidPublishedInvoice(invoice.id);
    }

    return this.updateState(invoice.id, 'void');

  }

  private voidPublishedInvoice(id: number): ng.IPromise<any> {
    return this.invoicesService.voidPublishedInvoice(id)
      .then(() => this.refreshList().then(() => {
        this.updateEditable();
        this.getFlightMovementsByInvoiceId(id);
      }))
      .catch((response: IRestangularResponse) => this.setErrorResponse(response));

  }

  // for non-aviation invoices, returns associated line items, transaction payments and transactions
  private getLineItemsByInvoiceId(invoiceId: number): void {
    this.invoicesService.getLineItemsByInvoiceId(invoiceId)
      .then((lineItems: Array<IInvoiceLineItem>) => this.$scope.lineItems = lineItems);

    this.getTransactionPaymentsByInvoiceId(invoiceId)
      .then((listTransactionPayments: Array<ITransaction>) => this.$scope.listTransactionPayments = listTransactionPayments);

    this.getTransactionByInvoiceId(invoiceId)
      .then((listTransactions: Array<ITransaction>) => this.$scope.listTransactions = listTransactions);
  }

  // for aviation invoices, returns a list of flight movements by related invoice id
  private getFlightMovementsByInvoiceId(invoiceId: number, page?: number, size?: number): ng.IPromise<IFlightMovementSpring> {
    return this.flightMovementManagementService.findAllFlightMovementsByAssociatedInvoiceId(invoiceId, page, size);
  }

  private getTransactionPaymentsByInvoiceId(invoiceId: number): ng.IPromise<ITransaction[]> {
    return this.transactionsService.getTransactionPaymentsByInvoiceId(invoiceId);
  }

  private getTransactionByInvoiceId(invoiceId: number): ng.IPromise<ITransaction[]> {
    return this.transactionsService.getTransactionByInvoiceId(invoiceId);
  }

  private showFlightMovementOnMap(flightMovement: IFlightMovement): void { // broadcasts a single airspace to be shown on map
    this.$rootScope.$broadcast('map.flyToEntity', MapFlyToEntityType.SINGLE_FLIGHT_MOVEMENT, [flightMovement]);
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
      statusFilter: this.$scope.statusFilter,
      exportedFilter: this.$scope.exportedFilter === true ? false : null, // since "non-exported" only set false when checked
      search: this.$scope.invoiceTextFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      startDate: startDate,
      endDate: endDate,
      account: this.$scope.accountFilter,
      createdByUserId: this.$scope.userFilter,
      flightIdOrRegistration: this.$scope.flightIdOrRegistration,
      billingCentre: this.$scope.billingCentreFilter
    };
  }

  /**
   * Refresh data list in scope.
   */
  private refreshList(): ng.IPromise<any> {
    this.$scope.listFlightMovements = null;
    this.$scope.listTransactionPayments = null;
    this.$scope.lineItems = null;

    this.getFilterParameters();

    return this.$scope.refresh(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  /**
   * Update the editable item from list if editable item still exists
   * in the list.
   *
   * If editable item no longer exists, the editable item is reset.
   */
  private updateEditable(): void {

    // flag to indicate if editable item has been refreshed
    let refreshed: boolean = false;

    // if editable item, loop through refreshed list for match and update editable item
    let editable: IInvoice = this.$scope.editable;
    if (editable !== null && editable.id !== null) {
      let items: Array<IInvoice> = this.$scope.list;
      for (let i = 0; i < items.length; i++) {
        if (items[i].id === editable.id) {
          this.$scope.editable = items[i];
          refreshed = true;
          break;
        }
      }
    }

    // if editable item is NOT updated, clear it out as it is no longer visible in the list
    if (!refreshed) {
      this.$scope.editable = angular.copy(this.service.model);
    }
  }

  /**
   * Determine if any invoice items are selected.
   *
   * @param selectedItems invoice id items selected
   */
  private isSelectedItems(selectedItems: any): boolean {
    return Object.keys(selectedItems)
      .filter((key: string) => selectedItems[key])
      .length > 0;
  }

  /**
   * Export all non-exported invoices.
   */
  private exportAllInvoices(): void {
    this.$scope.exportInProcess = true;
    this.invoicesService.exportAllInvoices()
      .then((response: any) => this.handleExportSuccess(response))
      .catch((response: IRestangularResponse) => this.handleExportError(response))
      .finally(() => this.handleExportFinished(this.$scope.selectedItems));
  }

  /**
   * Export a list of invoice ids.
   *
   * @param item invoice ids to export
   */
  private exportSelectedInvoices(selectedItems: any): void {
    this.$scope.exportInProcess = true;

    const ids: Array<number> = Object.keys(selectedItems)
      .filter((key: string) => selectedItems[key])
      .map(Number);

    this.invoicesService.exportSelectedInvoices(ids)
      .then((response: any) => this.handleExportSuccess(response))
      .catch((response: IRestangularResponse) => this.handleExportError(response))
      .finally(() => this.handleExportFinished(selectedItems));
  }

  /**
   * Clear any error messages away and display success message.
   */
  private handleExportSuccess(response: any): void {
    this.$scope.exportSuccess = true;
    this.$scope.error = null;
  }

  /**
   * Clear success message and display error message without subtitle.
   *
   * @param response error response
   */
  private handleExportError(response: IRestangularResponse): void {
    this.$scope.exportSuccess = false;
    response.data.hide_subtitle = true;
    this.setErrorResponse(response);
  }

  /**
   * Reset view and makr export in process as false.
   *
   * @param selectedItems items to uncheck
   */
  private handleExportFinished(selectedItems: any): void {

    // deselect ALL items
    if (selectedItems) {
      Object.keys(selectedItems).forEach((key: string) => this.$scope.selectedItems[key] = false);
    }

    // reset view list
    this.$scope.exportInProcess = false;
    this.refreshList();
  }

  /**
   * Validate if invoice is available for exproting.
   *
   * @param invoice invioce item to validate
   */
  private isExportSupport(invoice: IInvoice): boolean {
    const support: boolean = !invoice.exported
      && (invoice.invoice_state_type === 'PUBLISHED' || invoice.invoice_state_type === 'PAID')
      && this.exportSupportType[invoice.invoice_type];

    // make sure item is not already selected if support is false
    if (!support && this.$scope.selectedItems[invoice.id]) {
      this.$scope.selectedItems[invoice.id] = false;
    }

    return support;
  }

  /**
   * Set export support for invoice type and cache for resuse. Should
   * reset on page change/reload.
   *
   * @param type invoice type support to set
   */
  private setExportSupportType(type: string): void {
    if (this.exportSupportType[type] !== false && this.exportSupportType[type] !== true) {
      this.exportSupportType[type] = false;
      this.invoicesService.exportSupportType(type)
        .then((result: boolean) => this.exportSupportType[type] = result);
    }
  }

  private payInvoice(invoice: IInvoice): void {
    const transaction = {
      account: invoice.account,
      description: `Payment of ${invoice.invoice_number}`,
      payment_currency: invoice.invoice_currency,
      currency: invoice.invoice_currency,
      payment_amount: invoice.amount_owing,
      amount: invoice.amount_owing,
      billing_ledger_ids: [invoice.id]
    };

    this.$state.go('main.transactions', { transaction: transaction });
  }

  private enableVoid(invoice: IInvoice): boolean {
    return (invoice.point_of_sale && invoice.invoice_state_type === 'PUBLISHED'
      && invoice.invoice_amount === invoice.amount_owing) ||
      (!invoice.point_of_sale && invoice.invoice_state_type === 'NEW' || invoice.invoice_state_type === 'APPROVED');
  }
}
