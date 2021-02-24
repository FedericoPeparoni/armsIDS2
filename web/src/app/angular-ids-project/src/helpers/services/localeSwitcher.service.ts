// interfaces
import { ILanguageObject } from '../../../../partials/languages/languages.interface';

// services
import { LocalStorageService } from '../../components/services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';
import { SystemConfigurationService } from '../../../../partials/system-configuration/service/system-configuration.service';
import { UsersService } from '../../../../partials/users/service/users.service';

export abstract class LocaleSwitcherService {

  /** @ngInject */
  constructor(private systemConfigurationService: SystemConfigurationService, private $rootScope: ng.IRootScopeService, private $translate: angular.translate.ITranslateService, private usersService: UsersService, private $window: ng.IWindowService) {
    this.systemConfigurationService = systemConfigurationService;
  }

  /**
   * Update Angular Translate, Local Storage, and user (if authenticated) upon locale change
   */

  public changeLanguage(lang: ILanguageObject): void {
    this.setFrontendLanguage(lang);

    this.updateBackendLanguage(lang);
  }

  public setFrontendLanguage(lang: ILanguageObject): void {
    this.$translate.preferredLanguage(lang.code);
    this.$translate.use(lang.code);

    this.$rootScope.language.selected = lang;

    LocalStorageService.set(`SystemConfiguration:${SysConfigConstants.LANGUAGE_SELECTION}`, lang);
  }

  public updateBackendLanguage(lang: ILanguageObject): void {
    this.usersService.setCurrentUserLanguage(lang.code);
  }

}
