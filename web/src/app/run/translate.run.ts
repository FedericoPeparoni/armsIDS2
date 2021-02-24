// services
import { SystemConfigurationService } from '../partials/system-configuration/service/system-configuration.service';
import { LocalStorageService } from '../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { UsersService } from '../partials/users/service/users.service';

// constants
import { SysConfigConstants } from '../partials/system-configuration/system-configuration.constants';

// interfaces
import { ISystemConfiguration } from '../partials/system-configuration/system-configuration.interface';
import { ILanguageObject } from '../partials/languages/languages.interface';

const LANGUAGE_ENABLED = `SystemConfiguration:${SysConfigConstants.LANGUAGE_ENABLED}`;
const LANGUAGE_SELECTION = `SystemConfiguration:${SysConfigConstants.LANGUAGE_SELECTION}`;
const LANGUAGE_SUPPORTED = `SystemConfiguration:${SysConfigConstants.LANGUAGE_SUPPORTED}`;
const DEFAULT_LANGUAGE = '{"code": "en", "label": "English"}';
const DEFAULT_LANGUAGE_CODE = 'en';

export class TranslateService {

  /** @ngInject */
  constructor(
    protected $rootScope: ng.IRootScopeService,
    private systemConfigurationService: SystemConfigurationService,
    private usersService: UsersService,
    private $translate: angular.translate.ITranslateService
  ) {
    this.$rootScope = $rootScope;
    this.systemConfigurationService = systemConfigurationService;
    this.usersService = usersService;
    this.$translate = $translate;
  }

  public run(): void {
    this.$rootScope.language = {};

    this.systemConfigurationService.getLanguageConfiguration().then((resp: Array<ISystemConfiguration>) => {
      this.setLanguageConfiguration(resp);
    });
  }

  private setLanguageConfiguration(data: Array<ISystemConfiguration>): void {
    if (data && data.length) {
      LocalStorageService.set(LANGUAGE_ENABLED, data[0].current_value);
      LocalStorageService.set(LANGUAGE_SELECTION, data[1].current_value);
      LocalStorageService.set(LANGUAGE_SUPPORTED, data[2].current_value);
    } else { // handles condition where api is not running or bad data returned from endpoint
      LocalStorageService.set(LANGUAGE_ENABLED, SysConfigConstants.SYSTEM_CONFIG_FALSE);
      LocalStorageService.set(LANGUAGE_SELECTION, DEFAULT_LANGUAGE);
      LocalStorageService.set(LANGUAGE_SUPPORTED, `[${DEFAULT_LANGUAGE}]`);
    };

    this.initializeTranslator();

    this.createLanguageOptions();
  }

  private initializeTranslator(): void {
    const lang: ILanguageObject = LocalStorageService.get(LANGUAGE_SELECTION);
    const langEnabled: SysConfigConstants = LocalStorageService.get(LANGUAGE_ENABLED);

    this.$rootScope.language.enabled = langEnabled === SysConfigConstants.SYSTEM_CONFIG_TRUE;

    if (lang) {
      this.$translate.preferredLanguage(lang.code);
      this.$translate.use(lang.code);
    } else {
      this.$translate.preferredLanguage(DEFAULT_LANGUAGE_CODE);
      this.$translate.use(DEFAULT_LANGUAGE_CODE);
    };
  }

  private createLanguageOptions(): void {
    const langs: Array<ILanguageObject> = LocalStorageService.get(LANGUAGE_SUPPORTED);

    this.$rootScope.language.supported = langs;

    for (let i = 0, len = langs.length; i < len; i++) {
      if (langs[i].code === this.$translate.preferredLanguage()) {
        this.$rootScope.language.selected = langs[i];
      };
    };
  }

}

/** @ngInject */
export function TranslateRun(translateService: TranslateService): void {
  translateService.run();
}
