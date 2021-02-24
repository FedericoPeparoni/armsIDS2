// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

// services
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';

export class HelpController {

  /* @ngInject */
  constructor(private $scope: ng.IScope, private $location: ng.ILocationService) {
    const lang: any = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.LANGUAGE_SELECTION}`);
    $scope.url = `${$location.absUrl().split('#')[0]}assets/guide.${lang && lang.code ? lang.code : 'en'}.pdf#page=1`;
  }
}
