// interface
import { IAMHSConnection } from '../amhs-connection.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'amhs-configurations';

export class AMHSConnectionService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAMHSConnection = {
    id: null,
    active: null,
    descr: null,
    protocol: null,
    rtse_checkpoint_size: null,
    rtse_window_size: null,
    max_conn: null,
    ping_enabled: null,
    ping_delay: null,
    network_device: null,
    local_bind_authenticated: null,
    local_passwd: null,
    local_hostname: null,
    local_ipaddr: null,
    local_port: null,
    local_tsap_addr: null,
    local_tsap_addr_is_hex: null,
    remote_hostname: null,
    remote_ipaddr: null,
    remote_port: null,
    remote_passwd: null,
    remote_bind_authenticated: null,
    remote_class_extended: null,
    remote_content_corr: null,
    remote_dl_exp_prohibit: null,
    remote_rcpt_reass_prohibit: null,
    remote_idle_time: null,
    remote_internal_trace: null,
    remote_latest_delivery_time: null,
    remote_tsap_addr: null,
    remote_tsap_addr_is_hex: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public startAgent(): restangular.IPromise<any> {
    return this.restangular.all(`${endpoint}/agent/start`).customPOST();
  }

  public stopAgent(): restangular.IPromise<any> {
    return this.restangular.all(`${endpoint}/agent/stop`).customPOST();
  }

  public restartAgent(): restangular.IPromise<any> {
    return this.restangular.all(`${endpoint}/agent/restart`).customPOST();
  }
  public agentStatus(): restangular.IPromise<any> {
    return this.restangular.one(`${endpoint}/agent/status`).get();
  }
}
