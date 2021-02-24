// services
import { OAuthService } from '../../../components/services/oauth/oauth.service';
import { SystemConfigurationService } from '../../../partials/system-configuration/service/system-configuration.service';
import { UserEventLogService } from '../../../partials/user-event-log/service/user-event-log.service';

// classes
import { Event } from '../../../partials/user-event-log/user-event-log.class';

export class ScLoginService {

  protected restangular: restangular.IService;

  /** @ngInject */
  constructor(private oAuthService: OAuthService, private systemConfigurationService: SystemConfigurationService,
    private userEventLogService: UserEventLogService) {
  }

  public login(username: string, password: string): ng.IPromise<any> {
    let valid = false;

    // makes a call to system configuration as well when logging in to get system variables
    return this.oAuthService.authenticate(username, password, true).then(() => {
      this.systemConfigurationService.listForClientStorage();
      window.localStorage.setItem('loginFromBilling', JSON.stringify(false));

      // allows logging of successfull sign in event
      valid = true;
    }).finally(() => {
      if (valid) {
        const event = new Event('login', '', 'login');
        this.userEventLogService.create(event);
      }
    });
  }
}
