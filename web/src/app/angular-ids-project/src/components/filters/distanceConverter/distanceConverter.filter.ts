/**
 * Distance converter
 *
 * This filter is used to display distance in nautical miles.
 * Conversion: km to nm 1km =  0.539957nm
 *
 */

// services
import { LocalStorageService } from '../../services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../../partials/system-configuration/system-configuration.constants';

/** @ngInject */
export function distanceConverter(): Function {

  return function(val: number): number {

    if (val !== null && !isNaN(val)) {
      const format: string = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.DISTANCE_UNIT_OF_MEASURE}`);

      if (format === 'nm') {
        return val * 0.539957; // nautical mile
      }

      return val;
    }
  };
}
