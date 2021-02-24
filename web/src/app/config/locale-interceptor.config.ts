// services
import { LocalStorageService } from '../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { ConfigService } from '../angular-ids-project/src/components/services/config/config.service';

// constants
import { SysConfigConstants } from '../partials/system-configuration/system-configuration.constants';


/** @ngInject */
export function LocaleInterceptor($httpProvider: ng.IHttpProvider): void {
  $httpProvider.interceptors.push(LocaleInHeader);
}

function LocaleInHeader(): any {
  return {
    request: function (config: ng.IRequestConfig): ng.IRequestConfig {
      const configService = new ConfigService();
      const apiHost = configService.get('API_HOST');
      const localStorageLocale = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.LANGUAGE_SELECTION}`);

      // interceptor should only be applied on requests to ABMS API
      // fall back to browser if locale is not set in local storage
      if (!config.url.startsWith(apiHost) || !localStorageLocale) {
        return config;
      }

      // set accept-language code to use on the backend
      config.headers['Accept-Language'] = localStorageLocale.code;

      return config;
    }
  };
}
