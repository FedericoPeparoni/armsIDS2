/**
 * Override functionality from angular-translate.
 */

 // service, cannot be injected
import { ConfigService } from '../angular-ids-project/src/components/services/config/config.service';

/** @ngInject */
export function TranslateConfig($translateProvider: angular.translate.ITranslateProvider): void {
    let cfg = new ConfigService();
    let host = cfg.get('API_HOST');

    $translateProvider.useSanitizeValueStrategy(null);
    $translateProvider.fallbackLanguage('en');
    $translateProvider.useUrlLoader(`${host}/api/languages/json`);
}
