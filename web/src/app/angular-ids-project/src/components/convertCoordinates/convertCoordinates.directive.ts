/**
 * This directive is placed on create / update buttons
 * and is given latitude and longitude property names,
 * the values of which will be converted from 
 * Degrees/Minutes/Seconds to Decimal Degrees
 * 
 * After conversion, it proceeds with the crud function
 * 
 * @example
 * convert-to-tons latitude="prop" longitude="prop"
 * conversion-complete="create(convertedEditable || editable)"
 * 
 * For Lat/Lon properties that are nested, dot 
 * notation must be used (including array indexes)
 * @example
 * convert-to-tons latitude="nested.property.1"
 * longitude="nested.property.2"
 */

// classes
import { Coordinates } from './convertCoordinates.class';

// lodash
import { set } from 'lodash';

/** @ngInject */
export function convertCoordinates(): angular.IDirective {
  return {
    restrict: 'A',
    link: linkFunc,
    priority: 2
  };
}

function linkFunc(scope: ng.IScope, elem: ng.IAugmentedJQuery, attrs: any): void {
  const lon = attrs.longitude;
  const lat = attrs.latitude;
  const conversionComplete = attrs.conversionComplete; // function to run after conversion
  const coordinates = new Coordinates();

  function runClickEvent(func: string): void {
    scope.$eval(func);
  }

  elem.bind('click', () => {
    scope.convertedEditable = angular.copy(scope.editable);

    // if lat and long are nested 
    // properties with dot notation
    if (lat.includes('.') || lon.includes('.')) {

      // finds values of nested lat/lon properties
      // on the convertedEditable object
      const lonValue = lon.split('.').reduce((o: string, i: string) => o[i], scope.convertedEditable);
      const latValue = lat.split('.').reduce((o: string, i: string) => o[i], scope.convertedEditable);

      // use lodash to assign the converted value
      // to the nested property on the
      // convertedEditable object
      set(scope.convertedEditable, lon, coordinates.parseDMS(lonValue));
      set(scope.convertedEditable, lat, coordinates.parseDMS(latValue));
    } else {
      scope.convertedEditable[lat] = coordinates.parseDMS(scope.convertedEditable[lat]);
      scope.convertedEditable[lon] = coordinates.parseDMS(scope.convertedEditable[lon]);
    }

    if (conversionComplete) {
      runClickEvent(conversionComplete); // continue with create
    }
  });
}
