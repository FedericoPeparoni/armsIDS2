// classes
import { Event } from '../../../partials/user-event-log/user-event-log.class';

// constants
import { SysConfigConstants } from '../../../partials/system-configuration/system-configuration.constants';

// interfaces
import { Ioauth, IoauthError } from './oauth.interface';

// services
import { ConfigService } from '../../../angular-ids-project/src/components/services/config/config.service';
import { LocalStorageService } from '../../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { UserEventLogService } from '../../../partials/user-event-log/service/user-event-log.service';

// todo this could use refactoring to simplify how it works
export class OAuthService {

  private _oauth: Ioauth;
  private _secondsPassUntilRefresh: number = 604800; // expires if time passed is 1 week
  private makingRefreshRequest: boolean = false;

  // list of config items to keep on logout (i.e. stuff needed by login UI or preconfiguration items)
  private list: Array<SysConfigConstants> = [SysConfigConstants.LANGUAGE_SELECTION, SysConfigConstants.LANGUAGE_ENABLED, SysConfigConstants.LANGUAGE_SUPPORTED];

  /** @ngInject */
  constructor(private $location: ng.ILocationService, private $http: ng.IHttpService, private Restangular: restangular.IService,
    private configService: ConfigService, private $window: ng.IWindowService, private $state: angular.ui.IStateService,
    private userEventLogService: UserEventLogService, private $auth: any, private $q: ng.IQService) {}

  public set oauth(oauth: Ioauth) {
    this._oauth = oauth;

    // updated restangular default authorization header state to match oauth value
    // on clear, revet back to 'Content-Type': 'application/json' to match oauth.run
    let defaultHeaders: any;
    if (oauth && oauth.access_token) {
      defaultHeaders = { 'Authorization': `Bearer ${oauth.access_token}` };
    } else {
      defaultHeaders = { 'Content-Type': 'application/json' };
    }
    this.Restangular.setDefaultHeaders(defaultHeaders);
  }

  public get oauth(): Ioauth {
    return this._oauth;
  }

  public get secondsPassUntilRefresh(): number {
    return this._secondsPassUntilRefresh;
  }

  public setExpiryTimestamp(): void {
    LocalStorageService.set('oauth.expiry', (Math.floor(Date.now() / 1000)));
  }

  public getExpiryTimestamp(): number {
    return LocalStorageService.get('oauth.expiry');
  }

  public shouldRefreshToken(): boolean { // returns whether or not a refresh should be made
    return (Math.floor(Date.now() / 1000)) - this.getExpiryTimestamp() > this.secondsPassUntilRefresh;
  }

  public authenticate(username: string, password: string, selfCare: boolean): ng.IHttpPromise<any> {
    let api = this.configService.get('API_HOST') || '';
    let token = this.configService.get('AUTH_TOKEN');

    return this.$http.post(`${api}/oauth/token`, null, {
      params: {
        username: username,
        password: password,
        grant_type: 'password',
        selfCare: selfCare
      },
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': `Basic ${token}`
      },
      withCredentials: true
    }).success((oauth: Ioauth) => {
      this.oauth = oauth;
      LocalStorageService.set('oauth', oauth);
      this.setExpiryTimestamp();
      return oauth;
    }).error((error: IoauthError) => {
      return error;
    });
  }

  /**
   * Logout user session and navigate to appropriate landing page state.
   */
  public logout(logEvent: boolean = false): ng.IPromise<any> {
    // create a 'logout' user event before session is destroyed
    let promise: ng.IPromise<any>;
    if (logEvent && this.oauth) {
      promise = this.userEventLogService.create(new Event('logout', '', 'logout'));
    } else {
      promise = this.$q.reject('No user session found');
    }
    return promise.finally(() => this.destroySession());
  }

  /**
   * Redirect user to appropriate landing page based on route.
   */
  public redirect(isLogout: boolean = false, event: ng.IAngularEvent = null): void {

    // prevent default event from occuring before redirecting
    // required when called from event $stateChargeStart
    if (event) {
      event.preventDefault();
    }

    // determine if selfcare based on location
    const isBillingPath: boolean = this.$window.location.pathname.endsWith('/web/');
    const isSelfcarePath: boolean = this.$window.location.pathname.endsWith('/selfcare/');
    const isSelfcarePage: boolean = this.$location.path().startsWith('/sc-') || this.$location.path().includes('password-change');

    // redirect user to self-care home page if selfcare route, else go to billing login page
    if (isSelfcarePath || (!isBillingPath && isSelfcarePage)) {
      this.$state.go('main.sc-home-page', {}, { reload: true });
    } else {
      this.$state.go(isLogout ? 'login' : 'main.welcome-page', {}, { reload: true });
    }
  }

  public refresh(refreshToken: string): ng.IHttpPromise<any> {
    if (this.makingRefreshRequest !== true) {

      let api = this.configService.get('API_HOST') || '';
      let token = this.configService.get('AUTH_TOKEN');
      // prevents multiple refresh requests being made
      // throws a constraint SQL error if too many come in at once

      this.makingRefreshRequest = true;

      return this.$http.post(`${api}/oauth/token`, null, {
        params: {
          grant_type: 'refresh_token',
          refresh_token: refreshToken
        },
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': `Basic ${token}`
        },
        withCredentials: true
      }).success((oauth: Ioauth) => {
        this.oauth = oauth;
        this.makingRefreshRequest = false;
        LocalStorageService.set('oauth', oauth);
        this.setExpiryTimestamp();
        return oauth;
      }).error((error: IoauthError) => {
        this.logout();
        return error;
      });
    }
  }

  /**
   * Ensure billing users are not authenticated in self-care portal and self-care portal users are not authenticated in billing.
   */
  public validate(toState: angular.ui.IState = null, event: ng.IAngularEvent = null): void {

    // default to current location path
    const path: string = toState ? <string> toState.url : this.$location.path();

    // determine if user exists and where they logged in from
    const loginFromBilling: string = this.$window.localStorage.getItem('loginFromBilling');
    const isUserBilling: boolean = !!loginFromBilling && loginFromBilling.toLowerCase() === 'true';
    const isUserSelfcare: boolean = !!loginFromBilling && loginFromBilling.toLowerCase() === 'false';

    // determine if billing or selfcare path name exists
    const isPathBilling: boolean = this.$window.location.pathname.endsWith('/web/');
    const isPathSelfcare: boolean = this.$window.location.pathname.endsWith('/selfcare/');

    // determine if path is for selfcare or shared page
    const isPageSelfcare: boolean = path.startsWith('/sc-');
    const isPageShared: boolean = path.includes('password-change');

    // determine if route is billing or selfcare by path and pages
    const isRouteBilling: boolean = isPathBilling || !isPageSelfcare;
    const isRouteSelfcare: boolean = isPathSelfcare || isPageSelfcare || isPageShared;

    // if is a selfcare route and a billing user or a selfcare user and a billing route, logout to appropriate page
    if ((isUserBilling && !isRouteBilling) || (isUserSelfcare && !isRouteSelfcare)) {
      this.logout();
    }

    // limit '/selfcare/' and '/web/' path to respective pages if present by redirecting to landing page
    if ((isPathSelfcare && !(isPageSelfcare || isPageShared)) || (isPathBilling && isPageSelfcare)) {
      this.redirect(false, event);
    }
  }

  private clear(): void {
    this.oauth = null;
  }

  private destroySession(): void {

    // remove saved items from local storage
    LocalStorageService.destroy(this.savedItems());

    // nullify oauth object
    this.clear();
    this.$auth.logout();

    // redirect user to login page, selfcare
    this.redirect(true);
  }

  private savedItems(): any {
    let output: Array<string> = [];

    for (let i = 0, len = this.list.length; i < len; i++) {
      output.push(`SystemConfiguration:${this.list[i]}`);
    }

    return output;
  }
}
