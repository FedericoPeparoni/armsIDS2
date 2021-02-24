// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ScApprovalRequestService } from './service/sc-approval-request.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interfaces
import { ISCApprovalRequestScope, ISCApprovalRequest } from './sc-approval-request.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class ScApprovalRequestController extends CRUDFormControllerUserService {

  private defaultApprovalResponse: string;
  private defaultRejectionResponse: string;


  /* @ngInject */
  constructor(protected $scope: ISCApprovalRequestScope, private scApprovalRequestService: ScApprovalRequestService,
    private systemConfigurationService: SystemConfigurationService, private customDate: CustomDate,
    private $filter: ng.IFilterService, private $translate: angular.translate.ITranslateService) {
    super($scope, scApprovalRequestService);
    super.setup();

    $scope.customDate = customDate.returnDateFormatStr(false);
    this.defaultApprovalResponse = <string>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.APPROVAL_REQUEST_APPROVAL_RESPONSE);
    this.defaultRejectionResponse = <string>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.APPROVAL_REQUEST_REJECTION_RESPONSE);
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);

    this.getFilterParameters();
    $scope.selectedRequests = [];
    $scope.edit = (item: ISCApprovalRequest) => this.editOverride(item);
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.approve = (item: ISCApprovalRequest, text: string) => this.approve(item, text);
    $scope.reject = (item: ISCApprovalRequest, text: string) => this.reject(item, text);
    $scope.approveSelected = (text: string) => this.approveSelected(text);
    $scope.rejectSelected = (text: string) => this.rejectSelected(text);
    $scope.getSelected = () => $scope.selected = this.getIdsFromCheckboxes();

    $scope.formatNameAndTranslate = (name: string) => this.formatNameAndTranslate(name);
  }

  protected editOverride(item: ISCApprovalRequest): void {
    super.edit(item);
    this.$scope.date = this.$filter('dateConverter')(item.created_at, 'HHmm');
    this.$scope.responseDate = this.$filter('dateConverter')(item.response_date, 'HHmm');
    this.$scope.requestText = JSON.parse(this.$scope.editable.request_text);
    this.$scope.approvalResponse = this.defaultApprovalResponse;
    this.$scope.rejectionResponse = this.defaultRejectionResponse;
    if (item.request_dataset === 'account') {
      this.$scope.requestText.invoice_currency = this.$scope.requestText.invoice_currency.currency_name;
      this.$scope.requestText.account_type = this.$scope.requestText.account_type.name;
    }

    if (item.request_dataset === 'aircraft registration') {
      this.$scope.requestText.account = this.$scope.requestText.account.name;
      this.$scope.requestText.mtow_override = this.$filter('weightConverter')(this.$scope.requestText.mtow_override);
      this.$scope.requestText.aircraft_type = this.$scope.requestText.aircraft_type.aircraft_type;
      this.$scope.requestText.country_of_registration = this.$scope.requestText.country_of_registration.country_name;
      this.$scope.requestText.registration_expiry_date = this.$filter('dateConverter')(this.$scope.requestText.registration_expiry_date);
      this.$scope.requestText.registration_start_date = this.$filter('dateConverter')(this.$scope.requestText.registration_start_date);
    }

    if (item.request_dataset === 'flight schedule') {
      this.$scope.requestText.account = this.$scope.requestText.account.name;
      this.$scope.requestText.start_date = this.$filter('dateConverter')(this.$scope.requestText.start_date);
    }

    if (this.$scope.requestText.account_users) {
      let names: string[] = [];

      for (let i = 0, len = this.$scope.requestText.account_users.length; i < len; i++) {
        names.push(this.$scope.requestText.account_users[i].name);
      };

      this.$scope.requestText.account_users = names.join(',');
    }
  }

  protected resetOverride(): void {
    super.reset();
    this.$scope.requestText = null;
    this.$scope.date = null;
    this.$scope.selectedRequests = [];
    this.$scope.selected = [];
  }

  private approve(item: ISCApprovalRequest, text: string): void {
    item.response_text = text;
    this.$scope.showErrorOnTableForm = false;
    this.$scope.showErrorOnDataForm = true;
    this.scApprovalRequestService.approve(item, item.id).then(() => {
      this.resetOverride();
      this.refreshOverride();
    }).catch((error: IRestangularResponse) => this.$scope.error = { error });
  }

  private reject(item: ISCApprovalRequest, text: string): void {
    item.response_text = text;
    this.scApprovalRequestService.reject(item, item.id).then(() => {
      this.resetOverride();
      this.refreshOverride();
    }).catch((error: IRestangularResponse) => this.$scope.error = { error });

  }

  private approveSelected(text: string): void {
    this.$scope.showErrorOnDataForm = false;
    this.$scope.showErrorOnTableForm = true;
    this.scApprovalRequestService.approveSelected(this.$scope.selected, text).then(() => {
      this.resetOverride();
      this.refreshOverride();
    }).catch((error: IRestangularResponse) => this.$scope.error = { error });
  }

  private rejectSelected(text: string): void {
    this.scApprovalRequestService.rejectSelected(this.$scope.selected, text).then(() => {
      this.resetOverride();
      this.refreshOverride();
    }).catch((error: IRestangularResponse) => this.$scope.error = { error });
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
      search: this.$scope.search,
      account: this.$scope.account,
      objectType: this.$scope.objectType,
      status: this.$scope.status,
      startDate: startDate,
      endDate: endDate,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return this.$scope.refresh(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getIdsFromCheckboxes(): Array<number> {
    let keys: string[] = Object.keys(this.$scope.selectedRequests);
    return keys.filter((key: string) => this.$scope.selectedRequests[key]).map(Number);
  }

  /**
   * Formate and translate dynamic field label.
   */
  private formatNameAndTranslate(name: string): string {

    // return empty string if no name provided, nothing to translate
    if (!name) {
      return '';
    }

    // translate name instantly with underscores as spaces
    let result = this.$translate.instant(name.replace(/_/g, ' '));

    // if untranslated label contained 'mtow' include units
    return result && name.includes('mtow')
      ? `${result} (${this.$scope.mtowUnitOfMeasure})`
      : result;
  }
}
