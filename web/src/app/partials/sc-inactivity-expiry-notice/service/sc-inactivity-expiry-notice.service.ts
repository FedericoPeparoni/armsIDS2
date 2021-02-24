// interface
import { ISCInactivityExpiryNotices } from '../sc-inactivity-expiry-notice.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'self-care-portal-inactivity-expiry-notices';

export class ScInactivityExpiryNoticeService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ISCInactivityExpiryNotices = {
    id: null,
    account: null,
    notice_type: null,
    date_time: null,
    message_text: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
