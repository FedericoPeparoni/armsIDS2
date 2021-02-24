/**
 * If the system is set to enter MTOW or Cargo values in kg,
 * they will need to be converted to tons before sending to 
 * the back-end
 * 
 * This directive is placed on create / update buttons
 * and is given the property name for the value to convert
 * 
 * After conversion, it proceeds with the crud function
 * 
 * example use:
 * convert-to-tons property="nameOfProperty"
 * conversion-complete="create(convertedEditable || editable)"
 */

// services
import { LocalStorageService } from '../services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';

/** @ngInject */
export function convertToTons(): angular.IDirective {
  return {
    restrict: 'A',
    link: linkFunc,
    priority: 2
  };
}

function linkFunc(scope: ng.IScope, elem: ng.IAugmentedJQuery, attrs: any): void {
  const KG = 907.185;
  const mtowUnitOfMeasure = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.MTOW_UNIT_OF_MEASURE}`);
  const cargoDisplayUnits = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.CARGO_DISPLAY_UNITS}`);

  const mtowInKg = attrs.property; // property name for MTOW
  const cargoInKg = attrs.properties; // property name for cargo

  const conversionComplete = attrs.conversionComplete; // function to run after conversion

  function runClickEvent(func: string): void {
    scope.$eval(func);
  }

  elem.bind('click', () => {
    if (mtowUnitOfMeasure === 'kg') {
      scope.convertedEditable = angular.copy(scope.editable);
      scope.convertedEditable[mtowInKg] = scope.editable[mtowInKg] / KG;
    }

    if (cargoDisplayUnits === 'kg' && cargoInKg) {
      scope.convertedEditable = angular.copy(scope.editable);
      const array = cargoInKg.split(',');
      array.forEach((element: string) => {
        scope.convertedEditable[element] = scope.editable[element] ? scope.editable[element] / KG : null;
      });
    }

    if (conversionComplete) {
      runClickEvent(conversionComplete); // continue with create
    }
  });
}
