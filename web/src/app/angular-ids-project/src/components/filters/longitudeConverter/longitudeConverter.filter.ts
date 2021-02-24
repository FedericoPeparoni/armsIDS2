/**
 * Longitude Converter
 *
 * This filter is used to display a longitude in Degrees-Minutes-Seconds
 *
 */

// services
import { LocalStorageService } from '../../services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../../partials/system-configuration/system-configuration.constants';

// classes
import { Coordinates } from '../../convertCoordinates/convertCoordinates.class';

/** @ngInject */
export function longitudeConverter($filter: ng.IFilterService): Function {

  return (longitude: string): string => {

    const coordinates = new Coordinates();

    if (longitude !== null) {
      const format: string = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.COORDINATE_FORMAT}`);

      // if system set to dms, return
      // dms with 0 decimals points
      if (format === <any>SysConfigConstants.DEGREES_MINUTES_SECONDS) {
        const longitudeNumber = parseFloat(longitude);
        return coordinates.toLon(longitudeNumber, 'dms', 0);
      } else {
        // else return decimal degrees
        // with 5 decimal points
        const lon = $filter('number')(longitude, 5);
        if (lon !== null) {
          return lon.replace(/,/g, '');
        }
      }
    }
  };
}
