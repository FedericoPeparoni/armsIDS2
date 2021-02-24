// enums
import { CrudFileHandlerPropertyType } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler-type';

// interfaces
import { ITransactionPending, ITransactionPendingScope } from './transactions-pending.interface';
import { ITransactionWorkflow } from '../transactions-workflow/transactions-workflow.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ICurrencySpring } from '../currency-management/currency-management.interface';

// services
import { TransactionsPendingService } from './service/transactions-pending.service';
import { TransactionsWorkflowService } from '../transactions-workflow/service/transactions-workflow.service';
import { UsersService } from '../users/service/users.service';
import { SystemConfigurationService } from '../../partials/system-configuration/service/system-configuration.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { TransactionsService } from '../transactions/service/transactions.service';

// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';
import { OrganizationService } from '../organization/service/organization.service';

export class TransactionsPendingController extends CRUDFileUploadController {

  /** @ngInject */
  constructor(
    protected $scope: ITransactionPendingScope, private transactionsPendingService: TransactionsPendingService,
    private transactionsWorkflowService: TransactionsWorkflowService, private usersService: UsersService,
    private systemConfigurationService: SystemConfigurationService, private currencyManagementService: CurrencyManagementService,
    private transactionsService: TransactionsService, private organizationService: OrganizationService
  ) {
    super($scope, transactionsPendingService, null, 'Supporting Document', CrudFileHandlerPropertyType.Part);
    super.setup();

    $scope.pattern = '.jpg, .gif, .png, .pdf';

    // get required system configs
    $scope.inverseExchange = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE);
    $scope.activeOrg = this.organizationService.active();
    $scope.roundingAv = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.AVIATION_ROUNDING);
    $scope.roundingNonAv = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.NONAVIATION_ROUNDING);

    // methods exposed to the template
    $scope.refresh = () => this.refreshOverride();
    $scope.selectedItem = (item: ITransactionPending) => this.selectedItem(item);
    $scope.approve = (notes: string) => this.approve(notes);
    $scope.reject = (notes: string) => this.reject(notes);
    $scope.showWarning = () => this.$scope.error = transactionsPendingService.showWarning();

    // get transaction work flow settings and functions
    this.getTransactionSettings();
    $scope.parseApprovalDocument = (file: File) => this.parseApprovalDocument(file);

    // set initial values
    $scope.can_approve = false;
    $scope.can_reject = false;

    // set pending transaction form properties and functions
    currencyManagementService.listAll().then((data: ICurrencySpring) => $scope.currencyList = data.content);
    transactionsService.getPaymentMechanismList().then((data: Array<string>) => $scope.paymentMechanisms = data);
    $scope.fileUploadDisabled = true;
    $scope.fileUploadRequired = false;
    $scope.absoluteValue = (value: number): number => value ? Math.abs(value) : null;
    $scope.resetForm = () => this.resetForm();

    this.getFilterParameters();
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      text_search: this.$scope.textFilter,
      approval_name: this.$scope.filter,
      by_current_role: false,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * Refresh override to include custom filters and sort query.
   */
  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  /**
   * Handles application of the transaction workflow approval steps to pending transactions
   * @param {ITransactionPending} item - The data associated with the selected row
   */
  private getTransactionSettings(): void {
    this.transactionsWorkflowService.getAllWorkflowLevels().then((resp: any) => {
      this.$scope.levels = resp.content;

      this.$scope.levels.forEach((item: ITransactionWorkflow) => {
        if (item.approval_document_required) {
          this.$scope.approval_document_level = item.level;
        }
      });
    });
  }

  /**
   * Code to run when an item is selected in the data grid
   * @param {ITransactionPending} item - The data associated with the selected row
   */
  private selectedItem(item: ITransactionPending): void {
    this.$scope.selected = true;

    this.transactionsPendingService.getOne(item.id).then((resp: ITransactionPending) => {

      // update settings with data from response
      this.$scope.invoices = resp.detailed_invoices;
      this.$scope.charges = resp.pending_charge_adjustments;
      this.$scope.can_reject = resp.can_reject;
      this.$scope.decimalPlaces = resp.payment_currency.decimal_places;
      this.$scope.can_approve = resp.can_approve;
      this.$scope.approvals = resp.pending_transaction_approvals;

      // check to see if approval document is needed
      if (resp.current_approval_level.level === this.$scope.approval_document_level) {
        this.$scope.approval_document_required = true;
      } else {
        this.$scope.approval_document_required = false;
      }
    });

    this.$scope.invoice_to_approve_id = this.$scope.editable.id;
    this.$scope.editable.document_filename = item.supporting_document_name;

    this.$scope.fileUploadDisabled = false;
    this.resetApprovalDocument();
  }

  /**
   * Approve pending transaction, backend determines next steps
   */
  private approve(notes: string): void {
    let fd: FormData = new FormData();

    if (this.$scope.approval_document_required) {
      fd.append('file', this.$scope.approval_document, this.$scope.approval_document_filename);
    }

    this.transactionsPendingService.approve(this.$scope.invoice_to_approve_id, fd, notes)
      .then((resp: any) => {
        this.refreshOverride();
        this.resetForm();
      }).catch((error: IRestangularResponse) => this.handleResponseError(error));
  }

  /**
   * Reject pending transaction, backend determines next steps
   */
  private reject(notes: string): void {
    this.transactionsPendingService.reject(this.$scope.editable.id, notes)
      .then((resp: any) => {
        this.refreshOverride();
        this.resetForm();
      }).catch((error: IRestangularResponse) => this.handleResponseError(error));
  }

  /**
   * Rejsets the form afetr approve/reject
   */
  private resetForm(): void {
    this.reset();

    this.$scope.selected = false;
    this.$scope.can_approve = false;
    this.$scope.can_reject = false;

    this.$scope.fileUploadDisabled = true;
    this.$scope.approval_document_required = false;
    this.$scope.notes = null;
    this.resetApprovalDocument();
  }

  /**
   * Clears out any previous approval document file selection properties.
   */
  private resetApprovalDocument(): void {
    this.$scope.approval_document_filename = null;
    this.$scope.approval_document_mime_type = null;
    this.$scope.approval_document = null;
    this.$scope.approvalDocumentAcceptedFileType = null;
  }

  /**
   * Handle response error within scope.
   *
   * @param error response error to hanlde
   */
  private handleResponseError(error: IRestangularResponse): void {
    this.$scope.error = {
      error: error
    };
  }

  /**
   * Sets the required file information to the scope to be used when the user
   * uploads the file and any related fields of data for approval document.
   */
  private parseApprovalDocument(file: File): void {
    let r = new FileReader();

    if (file) {
      r.onload = (readerEvt: any) => {
        let binaryString = readerEvt.target.result;
        let type = (file.name.substr(file.name.lastIndexOf('.') + 1) === 'csv' ? 'text/plain' : file.type);
        let array = new Uint8Array(binaryString.length);

        for (let i = 0, len = binaryString.length; i < len; i++) {
          array[i] = binaryString.charCodeAt(i);
        }

        this.$scope.approval_document_filename = file.name;
        this.$scope.approval_document_mime_type = type;
        this.$scope.approval_document = new Blob([array], { type: type });

        // grab file extension and compare with pattern of acceptable types
        // compare lower case version of file ext to list of valid extensions
        const fileNameSplit = this.$scope.approval_document_filename.split('.');
        this.$scope.approvalDocumentAcceptedFileType = this.$scope.pattern.includes(fileNameSplit[fileNameSplit.length - 1].toLowerCase());
      };

      r.readAsBinaryString(file);
    }
  }
}
