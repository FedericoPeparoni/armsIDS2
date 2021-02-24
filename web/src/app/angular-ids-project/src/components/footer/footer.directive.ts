// interfaces
import { IFooter } from './footer.interface';

// services
import { ServerDatetimeService } from '../server-datetime/server-datetime.service';

// service, cannot be injected
import { ConfigService } from '../services/config/config.service';

/** @ngInject */
export function footer(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/footer/footer.html',
    controller: FooterBarController,
    controllerAs: 'FooterBarController',
    bindToController: true,
    scope: {}
  };

}

/** @ngInject */
export class FooterBarController {
  public constructor(private $scope: IFooter, private $interval: ng.IIntervalService, private serverDatetimeService: ServerDatetimeService) {

    // set initial server time and update every second
    this.setServerTime();
    $interval(() => this.setServerTime(), 1000);

    let cfg = new ConfigService();
    $scope.buildVersion = cfg.get('BUILD_VERSION');
  }

  /**
   * Set server time using server datetime service for current date.
   */
  private setServerTime(): void {
    this.$scope.serverTime = this.serverDatetimeService.getServerDate(new Date());
  }
}
