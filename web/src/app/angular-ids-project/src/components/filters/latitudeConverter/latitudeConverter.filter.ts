/**
 * Latitude Converter
 *
 * This filter is used to display a latitude in Degrees-Minutes-Seconds
 *
 */

// services
import { LocalStorageService } from '../../services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../../partials/system-configuration/system-configuration.constants';

// classes
import { Coordinates } from '../../convertCoordinates/convertCoordinates.class';

/** @ngInject */
export function latitudeConverter($filter: ng.IFilterService): Function {

  return (latitude: string): string => {

    const coordinates = new Coordinates();

    if (latitude !== null) {
      const format: string = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.COORDINATE_FORMAT}`);

      // if system set to dms, return
      // dms with 0 decimals points
      if (format === <any>SysConfigConstants.DEGREES_MINUTES_SECONDS) {
        const latitudeNumber = parseFloat(latitude);
        return coordinates.toLat(latitudeNumber, 'dms', 0);
      } else {
        // else return decimal degrees
        // with 5 decimal points
        const lat = $filter('number')(latitude, 5);
        if (lat !== null) {
          return lat.replace(/,/g, '');
        }
      }
    }
  };
}
