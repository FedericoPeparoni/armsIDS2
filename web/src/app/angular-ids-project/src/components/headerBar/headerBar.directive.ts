// interface
import { IHeaderBarScope } from './headerBar.interface';

// services
import { OAuthService } from '../../../../components/services/oauth/oauth.service';
import { LocalStorageService } from '../services/localStorage/localStorage.service';
import { LocaleSwitcherService } from '../../helpers/services/localeSwitcher.service';

/** @ngInject */
export function headerBar(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/headerBar/headerBar.html',
    controller: HeaderBarController,
    controllerAs: 'HeaderBar',
    bindToController: true,
    replace: true,
    transclude: true
  };

}

/** @ngInject */
export class HeaderBarController {
  public constructor(private $rootScope: ng.IRootScopeService, private $scope: IHeaderBarScope, private localeSwitcherService: LocaleSwitcherService,
    private oAuthService: OAuthService
  ) {
    $scope.toggleSideBar = () => $scope.$broadcast('toggleSideBar');
    $scope.changeMode = (mode: string) => this.changeMode(mode);
    $scope.$on('userChanged', (e: any, user: any) => $scope.user = user);
    $scope.onButtonGroupClick = ($event: any) => this.onButtonGroupClick($event);
    $scope.logout = () => this.oAuthService.logout(true);
    $scope.changeLanguage = (lang: any) => this.localeSwitcherService.changeLanguage(lang);

    $scope.mode = LocalStorageService.get('contentMode') ? LocalStorageService.get('contentMode') : 'content-mode';

    $scope.language = $rootScope.language;

    $rootScope.$watch('language', () => {
      $scope.language = $rootScope.language;
    });
  }

  private changeMode(mode: string): void {
    this.$scope.mode = mode;

    this.$rootScope.$broadcast('contentModeChange', mode);
  }

  private onButtonGroupClick($event: any): void {
    $event.stopPropagation();

    let clickedElement = $event.target || $event.srcElement;
    let alreadyActive = clickedElement.parentElement.querySelector('.active');

    if (alreadyActive) {
      alreadyActive.classList.remove('active');
    }

    clickedElement.classList.add('active');
  }

}
