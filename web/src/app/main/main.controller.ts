import { LocalStorageService } from '../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { sideBar } from '../angular-ids-project/src/components/sideBar/sideBar';

// interfaces
import { IPermissionsMap } from '../partials/permissions/permissions.interface';
import { IUser } from '../partials/users/users.interface';

// services
import { NO_PERMISSIONS_REQUIRED, SELF_CARE_PORTAL_PERMISSIONS, SELF_CARE_PORTAL_PERMISSIONS_NOT_AUTHORIZED } from '../partials/permissions/service/permissions.service';

interface IMainScope extends ng.IScope {
  page: string;
  permissions: Array<string>;
  contentMode: string;
  sideBar: Array<Object>;
  sidebarProps: Array<ISidebarProps>;
  keyUp: Function;
  clear: Function;
  hasPermission: (permission: string) => boolean;
  hasPermissionInArray: (listOfPermissions: Array<string>) => boolean;
}

interface ISidebarProps extends ng.IScope {
  open: boolean;
}

import { UsersService } from '../partials/users/service/users.service';
import { PermissionsService } from '../partials/permissions/service/permissions.service';
import { OAuthService } from '../components/services/oauth/oauth.service';

export class MainController {

  private permissions: Array<string>;
  private userAuthorized: boolean;
  private loginFromBilling: boolean;

  /* @ngInject */
  constructor(private $scope: IMainScope, private $rootScope: ng.IRootScopeService, private $state: angular.ui.IStateService,
    private usersService: UsersService, private permissionsService: PermissionsService, private $location: ng.ILocationService,
    private $window: ng.IWindowService, private oAuthService: OAuthService
  ) {
    this.$scope.page = this.$state.current.name;

    this.permissionsService.generatePermissionsMap();

    // log out a self care user from billing and vice-versa
    // and redirect if accessing pages on incorrect url
    oAuthService.validate();

    this.usersService.current.then((user: IUser) => {
      this.userAuthorized = user !== undefined;
      if (this.userAuthorized) {
        this.loginFromBilling = JSON.parse(this.$window.localStorage.getItem('loginFromBilling'));
        if (user.force_password_change) {
          this.checkForcePasswordChange();
          this.$scope.forcePasswordChange = true;
          this.$state.go('main.password-change');
        }
        this.$scope.$broadcast('userChanged', user);
        this.permissions = user ? user.permissions : [];
      }
      this.$scope.hasPermission = (permission: string) => this.hasPermission(permission);
      this.$scope.hasPermissionInArray = (listOfPermissions: Array<string>) => this.hasPermissionInArray(listOfPermissions);
    });

    // if content mode has been changed by the user, we set the content mode to what it was set on load
    let contentMode = LocalStorageService.get('contentMode');
    if (contentMode !== null) {
      this.$scope.contentMode = contentMode;
    }

    this.$rootScope.$on('contentModeChange', (event: angular.IAngularEvent, modeToChangeTo: string) => {
      this.$scope.contentMode = modeToChangeTo;
      LocalStorageService.set('contentMode', modeToChangeTo);
    });

    // on page change, we append the state name as a class
    this.$scope.$on('$stateChangeStart', (event: angular.IAngularEvent, toState: angular.ui.IState) => {
      this.$scope.page = toState.name;

      // check if user is forced to change his password and always
      // redirect him to password-change page
      if (this.userAuthorized) {
        if (this.$location.path() !== '/main.password-change' && this.$scope.forcePasswordChange) {
          this.$location.path('/main.password-change');
        } else if (!toState.url || toState.url !== '/login') {
          this.checkPermissions();
        }
      }

      // log out a self care user from billing and vice-versa
      // and redirect if accessing pages on incorrect url
      this.oAuthService.validate(toState, event);

      this.$scope.$broadcast('toggleSideBar', false);
    });

    // redirect to appropriate authorized landing page if url not defined
    if (this.$state.current.url === '') {
      this.oAuthService.redirect();
    }

    this.$scope.sideBar = this.buildSidebarLabels(sideBar);
    this.$scope.sidebarProps = Array.apply(null, new Array($scope.sideBar.length)).map(function(): Object { return { 'open': false }; });

    this.$scope.clear = () => this.setSidebarProps(this.$scope.sidebarProps, false);

    this.$scope.keyUp = (s: string) => {
      if (s.length > 0) {
        this.setSidebarProps(this.$scope.sidebarProps, true);
      } else {
        this.setSidebarProps(this.$scope.sidebarProps, false);
      }
    };
  }

  /**
   * Returns whether the current user has this permission.
   */
  private hasPermission(permission: string): boolean {
    return this.permissions && this.permissions.indexOf(permission) > -1;
  }

  /**
   * Returns whether the current user has one of the permissions in an array of permissions.
   */
  private hasPermissionInArray(listOfPermissions: Array<string>): boolean {
    if (listOfPermissions && listOfPermissions.length > 0) {
      for (let permission of listOfPermissions) {
        if (this.userAuthorized) {
          if (this.loginFromBilling) {
            if (this.hasPermission(permission) || permission === NO_PERMISSIONS_REQUIRED) {
              return true;
            }
          } else {
            if (this.hasPermission(permission) && (permission === 'self_care_admin' || permission === 'self_care_access') ||
              permission === SELF_CARE_PORTAL_PERMISSIONS) {
              return true;
            }
          }
        } else {
          if (permission === SELF_CARE_PORTAL_PERMISSIONS || permission === SELF_CARE_PORTAL_PERMISSIONS_NOT_AUTHORIZED) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private setSidebarProps(array: Array<ISidebarProps>, boolean: boolean): void {
    for (let i in array) {
      if (array.hasOwnProperty(i)) {
        array[i].open = boolean;
      }
    }
  }

  private checkPermissions(): void {
    let permissionsMap: IPermissionsMap = this.permissionsService.getPermissionsMap;
    let attemptedUrl = this.$scope.page.split('.')[1];
    let permitted;
    const isAuthRoute = ['/login'].indexOf(this.$location.path()) >= 0;

    // if attempted url is found in the permissions map
    // check to see if user has permissions
    if (permissionsMap[attemptedUrl]) {
      for (let neededPermission of permissionsMap[attemptedUrl]) {
        if (this.isPermitted(neededPermission)) {
          permitted = true;
          break;
        }
      }
    }

    if (!permitted && !isAuthRoute) {
      this.oAuthService.logout();
    }
  }

  private isPermitted(permission: string): boolean {
    return (this.permissions && this.permissions.indexOf(permission) !== -1)
      || permission === NO_PERMISSIONS_REQUIRED
      || permission === SELF_CARE_PORTAL_PERMISSIONS;
  }

  private checkForcePasswordChange(): void {
    if (!JSON.parse(window.localStorage.getItem('forcePasswordChange'))) {
      window.localStorage.setItem('forcePasswordChange', JSON.stringify(true));
    }
  }

  private buildSidebarLabels(sidebar: any): any {
    sidebar.map((category: any) => category.links.map((link: any) => link.filterableText = link.category));

    return sidebar;
  };

}
