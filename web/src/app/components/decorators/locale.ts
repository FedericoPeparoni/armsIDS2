// services
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { HelperService } from '../../angular-ids-project/src/components/services/helpers/helpers.service';

// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';

const updateCalendar = (translate: angular.translate.ITranslateService, delegate: ng.IScope, valueToJSON: Function): any => {

  // override $locale.DATETIME_FORMATS
  const value = delegate.DATETIME_FORMATS;

  const day = valueToJSON(translate.instant('DP_DAY'));
  const shortDay = valueToJSON(translate.instant('DP_SHORTDAY'));
  const month = valueToJSON(translate.instant('DP_MONTH'));
  const shortMonth = valueToJSON(translate.instant('DP_SHORTMONTH'));

  if (day) { value.DAY = day; };
  if (shortDay) { value.SHORTDAY = shortDay; };
  if (month) { value.MONTH = month; };
  if (shortMonth) { value.SHORTMONTH = shortMonth; };
};

/** @ngInject */
export const LocaleDecorator = ($provide: ng.IScope) => {

  // only decorate if language support is enabled
  if (LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.LANGUAGE_ENABLED}`) === SysConfigConstants.SYSTEM_CONFIG_TRUE) {

    $provide.decorator('$locale', ($delegate: ng.IScope, $translate: angular.translate.ITranslateService, $rootScope: ng.IRootScopeService) => {

      ($translate as any).onReady(() => updateCalendar($translate, $delegate, HelperService.valueToJSON));
      $rootScope.$on('$translateChangeEnd', () => updateCalendar($translate, $delegate, HelperService.valueToJSON));

      return $delegate;
    });
  }
};
