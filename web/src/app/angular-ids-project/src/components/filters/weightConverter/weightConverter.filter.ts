/**
 * Weight Converter
 *
 * This filter is used to display a weight in kilograms (kg)
 * Conversion is currently done with the US ton: 1 ton = 907.185 kg
 *
 */

// services
import { LocalStorageService } from '../../services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../../partials/system-configuration/system-configuration.constants';

/** @ngInject */
export function weightConverter($filter: ng.IFilterService): Function {

  return function(val: number): number {

    if (val !== null && !isNaN(val)) {
      const format: string = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.MTOW_UNIT_OF_MEASURE}`);

      if (format === 'kg') {
        return $filter('number')(val * 907.185, 0);  // us ton
      } else {
        return $filter('number')(val, 2);
      }
    }
  };
}
