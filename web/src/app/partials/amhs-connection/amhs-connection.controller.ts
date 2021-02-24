// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// services
import { AMHSConnectionService } from './service/amhs-connection.service';
import { IAMHSConnection, IAmhsAgentStatus } from './amhs-connection.interface';

export class AMHSConnectionController extends CRUDFormControllerUserService {
  /* @ngInject */
  constructor(protected $scope: ng.IScope, private amhsConnectionService: AMHSConnectionService, private $interval: ng.IIntervalService) {
    super($scope, amhsConnectionService);
    super.setup();

    // overrides
    $scope.reset = () => this.resetOverride();
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.create = (editable: IAMHSConnection) => this.createOverride(editable);
    $scope.update = (editable: IAMHSConnection, id: number) => this.updateOverride(editable, id);
    $scope.delete = (id: number) => this.deleteOverride(id);
    $scope.startAgent = () => this.startAgent();
    $scope.stopAgent = () => this.stopAgent();
    $scope.restartAgent = () => this.restartAgent();
    $scope.agentOperationInProgress = false;

    // finish setup
    this.getFilterParameters();
    this.setDefaults();

    // refresh agent status every 30 seconds
    $scope.agentStatusPromise = $interval (() => this.agentStatus(), 30000);
    $scope.$on ('$destroy', () => $interval.cancel ($scope.agentStatusPromise));
    this.agentStatus();
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
    this.agentStatus();
  }

  private resetOverride(): void {
    super.reset();
    this.setDefaults();
  }

  private setDefaults(): void {
      // set defaults for the form
      this.$scope.editable.active = true;
      this.$scope.editable.descr = '';
      this.$scope.editable.protocol = 'P3';
      this.$scope.editable.rtse_checkpoint_size = 4;
      this.$scope.editable.rtse_window_size = 3;
      this.$scope.editable.max_conn = 3;
      this.$scope.editable.ping_enabled = true;
      this.$scope.editable.ping_delay = null;
      this.$scope.editable.network_device = null;
      this.$scope.editable.local_bind_authenticated = false;
      this.$scope.editable.local_passwd = 'default';
      this.$scope.editable.local_hostname = '';
      this.$scope.editable.local_ipaddr = '';
      this.$scope.editable.local_port = 103;
      this.$scope.editable.local_tsap_addr = '';
      this.$scope.editable.local_tsap_addr_is_hex = false;
      this.$scope.editable.remote_hostname = '';
      this.$scope.editable.remote_ipaddr = '';
      this.$scope.editable.remote_port = 103;
      this.$scope.editable.remote_passwd = 'default';
      this.$scope.editable.remote_bind_authenticated = false;
      this.$scope.editable.remote_class_extended = true;
      this.$scope.editable.remote_content_corr = true;
      this.$scope.editable.remote_dl_exp_prohibit = true;
      this.$scope.editable.remote_rcpt_reass_prohibit = true;
      this.$scope.editable.remote_idle_time = 120;
      this.$scope.editable.remote_internal_trace = true;
      this.$scope.editable.remote_latest_delivery_time = true;
      this.$scope.editable.remote_tsap_addr = '';
      this.$scope.editable.remote_tsap_addr_is_hex = false;
      this.$scope.agent_status = {};
      this.$scope.agent_status.installed = false;
      this.$scope.agent_status.started = false;

      // validation defaults
      this.$scope.valid = true;
      this.$scope.pattern = '';

      this.agentStatus();
  }

  private async createOverride(editable: IAMHSConnection): Promise<any> {
    await super.create(editable);
    this.setDefaults();
  }

  private async updateOverride(editable: IAMHSConnection, id: number): Promise<any> {
    await super.update(editable, id);
    this.setDefaults();
  }

  private async deleteOverride(id: number): Promise<any> {
    await super.delete(id);
    this.setDefaults();
  }

  private startAgent(): Promise<any> {
    this.$scope.agentOperationInProgress = true;
    return this.amhsConnectionService.startAgent()
      .then((response: IAmhsAgentStatus) => this.$scope.agent_status = response)
      .catch((response: IRestangularResponse) => this.displayErrorResponse(response))
      .finally(() => this.$scope.agentOperationInProgress = false);
  }
  private stopAgent(): Promise<any> {
    this.$scope.agentOperationInProgress = true;
    return this.amhsConnectionService.stopAgent()
      .then((response: IAmhsAgentStatus) => this.$scope.agent_status = response)
      .catch((response: IRestangularResponse) => this.displayErrorResponse(response))
      .finally(() => this.$scope.agentOperationInProgress = false);
  }
  private restartAgent(): Promise<any> {
    this.$scope.agentOperationInProgress = true;
    return this.amhsConnectionService.restartAgent()
      .then((response: IAmhsAgentStatus) => this.$scope.agent_status = response)
      .catch((response: IRestangularResponse) => this.displayErrorResponse(response))
      .finally(() => this.$scope.agentOperationInProgress = false);
  }
  private agentStatus(): Promise<any> {
    if (!this.$scope.agentOperationInProgress) {
      return this.amhsConnectionService.agentStatus()
        .then((response: IAmhsAgentStatus) => this.$scope.agent_status = response);
    }
    return Promise.resolve({});
  }

  /**
   * Display error message of restangular response.
   *
   * @param response restangular response with error
   */
  private displayErrorResponse(response: IRestangularResponse): void {
    this.$scope.error = {
      error: response
    };
  }

}
