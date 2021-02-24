/**
 * Date Converter
 *
 * This is used to convert a date string into one of the following
 *
 * https://docs.angularjs.org/api/ng/filter/date
 *
 * Note: there is the optional parameter `additionalDateFilter` that can append the systems format.
 *       it is ignored if you use an Angular date filter that is a "word" to keep things from breaking
 *       ex. you can't do `longDate hh:mm:ss`, it does not work
 *
 */

// services
import { LocalStorageService } from '../../services/localStorage/localStorage.service';

/** @ngInject */
export function dateConverter($filter: ng.IFilterService): Function  {

  // the reason we do the regex inside is to save memory if we don't need to test against it
  const ignoreWords = [
    'medium',
    'short',
    'fullDate',
    'longDate',
    'mediumDate',
    'shortDate',
    'mediumTime',
    'shortTime'
  ];

  return function (oldValue: string, additionalDateFilter?: string): string {

    // do not convert placeholders
    if (oldValue === '...') {
      return oldValue;
    }

    if (oldValue !== null) {

      let format: string = LocalStorageService.get('SystemConfiguration:Date format')
        ? LocalStorageService.get('SystemConfiguration:Date format')
        : 'yyyy-MMM-dd';

      // we join the words together and see if our format contains it
      // append the additional optional parameter to our filter
      if (additionalDateFilter && ignoreWords.indexOf(format) === -1) {
        format += ` ${additionalDateFilter}`;
      }

      // always display times in UTC, GMT +0000
      return $filter('date')(new Date(oldValue), format, 'UTC');
    }
  };
}
