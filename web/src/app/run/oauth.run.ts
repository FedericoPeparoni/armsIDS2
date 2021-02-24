// services
import { ConfigService } from '../angular-ids-project/src/components/services/config/config.service';
import { LocalStorageService } from '../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { OAuthService } from '../components/services/oauth/oauth.service';
import { UserEventLogService } from '../partials/user-event-log/service/user-event-log.service';

/** @ngInject */
export function OAuthRun($location: ng.ILocationService, Restangular: restangular.IService, configService: ConfigService, oAuthService: OAuthService,
  userEventLogService: UserEventLogService, $timeout: ng.ITimeoutService, $window: ng.IWindowService): void {
  // todo the oauth stuff below should be cleaned up and done better
  // https://github.com/witoldsz/angular-http-auth
  // currently it checks the oauth token timestamp when it was initialized and determines if should refresh
  // issue is that multiple calls can be made at once which some may be authorized and unauthorized (forcing a logout)

  const baseURL: string = configService.get('API_HOST') || '';
  const oauth = LocalStorageService.get('oauth');

  Restangular.setBaseUrl(`${baseURL}/api`);
  Restangular.setDefaultHeaders({ 'Content-Type': 'application/json' });
  Restangular.setRequestInterceptor((data: any, operation: string, identifier: string) => {
    $timeout(() => { if (operation === 'remove') { userEventLogService.createEvent(data, operation, identifier); } });

    return data;
  });
  Restangular.addResponseInterceptor((data: any, operation: string, identifier: string, what: string) => {
    $timeout(() => { if (operation !== 'remove') { userEventLogService.createEvent(data, operation, identifier); } });
    return data;
  });

  if (oauth !== null) {
    oAuthService.oauth = oauth; // user has oauth token

    if (oAuthService.shouldRefreshToken()) { // token has expired, we refresh it
      oAuthService.refresh(oauth.refresh_token); // error has occurred when attempting to refresh token, we force a logout
    }
  }

  if ($location.path().startsWith('/sc-') || $window.location.pathname.endsWith('/selfcare/')) {
    if ($location.path() === '/sc-user-registration-activate') {
      $location.path('sc-user-registration-activate');
    }

    Restangular.setErrorInterceptor((response: restangular.IResponse) => {
      console.error('Set Error Interceptor: ', response);
      if (response.status === 401 || response.status === 403) { // fail request
        if (JSON.parse(window.localStorage.getItem('forcePasswordChange'))) {
          $location.path('/main.password-change');
        } else if (($location.path().startsWith('/sc-') || $window.location.pathname.endsWith('/selfcare/'))
                && ($location.path() === '/sc-user-registration-activate')) {
            $location.path('sc-user-registration-activate');
        } else if (oAuthService.oauth) {
          oAuthService.logout();
        }
      }
    });
  }
}
