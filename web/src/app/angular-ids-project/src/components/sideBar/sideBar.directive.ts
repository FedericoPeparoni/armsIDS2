// interface
import { ISideBarLinkScope, ISideBarScope } from './sideBar.interface';

// services
import { LocalStorageService } from '../services/localStorage/localStorage.service';
import { OAuthService } from '../../../../components/services/oauth/oauth.service';

// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';

/**
 * Side bar (parent)
 */

/** @ngInject */
export function sideBar(): angular.IDirective {

  return {
    restrict: 'E',
    template: '<div ng-class="{\'show\': toggle }"><ul ng-transclude> </ul></div>',
    controller: SideBarController,
    controllerAs: 'SideBar',
    bindToController: true,
    replace: true,
    transclude: true,
    scope: {}
  };

}

/** @ngInject */
export class SideBarController {

  public scope: ISideBarScope;

  public constructor(private $scope: ISideBarScope) {

    $scope.$on('toggleSideBar', (e: any, open: boolean) => {
      $scope.toggle = typeof open === 'boolean' ? open : !$scope.toggle; // if open is sent
    });

    /**
     * On State change, the sidebar will broadcast what the state is changing to and try and highlight the correct link
     * @param linkToHighlight
     */
    $scope.$on('$stateChangeStart', (event: any, toState: angular.ui.IState) => {
      $scope.highlight(toState);
    });

    $scope.highlight = (stateToHighlight: angular.ui.IState) => {
      $scope.$broadcast('sideBarLink.highlight', stateToHighlight);
    };

  }

}

/**
 * Side bar category
 */
/** @ngInject */
export function sideBarCategory(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/sideBar/sideBarCategory.html',
    controller: SideBarCategory,
    controllerAs: 'SideBarCategory',
    bindToController: true,
    replace: true,
    transclude: true,
    scope: {
      icon: '=',
      text: '=',
      links: '=',
      categoryOpen: '='
    }
  };

}

/**
 * Side bar category
 */
/** @ngInject */
export class SideBarCategory {
  active: boolean;
  icon: string;
  text: string;
  links: Array<any>;

  constructor(private $scope: angular.IScope, private $state: angular.ui.IStateService, private $translate: angular.translate.ITranslateService) {

    // translate link category for search
    if (LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.LANGUAGE_ENABLED}`) === SysConfigConstants.SYSTEM_CONFIG_TRUE) {
      $scope.SideBarCategory.links.map((link: SideBarLink) => link.filterableText = $translate.instant(link.category));
    }

    // this is used for initial launch of the app, will highlight on load
    this.highlight($state.current, this.links);

    // listen for state changes and highlight if active
    $scope.$on('sideBarLink.highlight', (e: any, state: angular.ui.IState) => {
      this.highlight(state, this.links);
    });
  }

  /**
   * Set active value based on provided state.
   *
   * @param state angular ui router state
   * @param links category links
   */
  private highlight(state: angular.ui.IState, links: Array<any>): void {

    // loop through each link and set active true if one matches the state provided
    this.active = links.some((link: any): boolean => {
      return highlight(state, link);
    });
  }
}


/**
 * Side bar link (child)
 */
/** @ngInject */
export function sideBarLink(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/sideBar/sideBarLink.html',
    controller: SideBarLink,
    controllerAs: 'SideBarLink',
    bindToController: true,
    replace: true,
    transclude: true,
    scope: {
      icon: '=',
      uiSref: '=',
      url: '=',
      category: '@'
    }
  };
}

/** @ngInject */
export class SideBarLink {
  active: boolean;
  icon: string;
  uiSref: string;
  url: string;
  category: string;
  filterableText: string;

  constructor(private $scope: ISideBarLinkScope, private $state: angular.ui.IStateService, private oAuthService: OAuthService) {

    // this is used for initial launch of the app, will highlight on load
    this.highlight($state.current);

    // listen for state changes and highlight if active
    this.$scope.$on('sideBarLink.highlight', (e: any, state: angular.ui.IState) => {
      this.highlight(state);
    });
  }

  /**
   * Change state using `uiSref` or `url` values if set.
   */
  public goTo(): void {
    if (this.url === 'logout') {
      this.oAuthService.logout(true);
    } else if (this.uiSref) {
      this.$state.go(this.uiSref);
    } else if (this.url) {
      this.$state.go('main.' + this.url);
    }
  }

  /**
   * Set active value based on provided state.
   *
   * @param state angular ui router state
   */
  private highlight(state: angular.ui.IState): void {
    this.active = highlight(state, { uiSref: this.uiSref, url: this.url });
  }
}

/**
 * Return true if provided state matched the provided link.
 *
 * @param state angular ui router currrent state
 * @param link link object containing at least uiSref and url properties
 */
export function highlight(state: angular.ui.IState, link: { uiSref: string, url: string }): boolean {
  if (link.uiSref) {
    // standard angular-ui-router approach
    return state.name === link.uiSref;
  } else if (link.url) {
    // custom to match sideBar.ts list of main items -> `$state.go('main.' + itemUrl)`
    return state.name === 'main.' + link.url;
  } else {
    return false;
  }
}
