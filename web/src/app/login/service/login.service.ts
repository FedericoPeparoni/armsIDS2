// services
import { OAuthService } from '../../components/services/oauth/oauth.service';
import { SystemConfigurationService } from '../../partials/system-configuration/service/system-configuration.service';
import { UserEventLogService } from '../../partials/user-event-log/service/user-event-log.service';

// classes
import { Event } from '../../partials/user-event-log/user-event-log.class';

export class LoginService {

  protected restangular: restangular.IService;

  /** @ngInject */
  constructor(private oAuthService: OAuthService, private systemConfigurationService: SystemConfigurationService, private userEventLogService: UserEventLogService,
    private $location: ng.ILocationService, private $rootScope: ng.IRootScopeService) { }

  public login(username: string, password: string): ng.IPromise<any> {
    let valid = false;

    // makes a call to system configuration as well when logging in to get system variables
    return this.oAuthService.authenticate(username, password, false).then(() => {
      this.systemConfigurationService.listForClientStorage().then(() => {
        this.$rootScope.$broadcast('system_configurations', 'complete');
      });

      window.localStorage.setItem('loginFromBilling', JSON.stringify(true));

      // allows logging of successfull sign in event
      valid = true;
    }).finally(() => {
      if (valid) {
        const event = new Event('login', '', 'login');
        this.userEventLogService.create(event);
      }
    });
  }

  public redirectIfLoggedIn(): void {
    const oauth = this.oAuthService.oauth;
    if (oauth && Object.keys(oauth)) {
      this.$location.path('/welcome-page');
    }
  }
}
