/**
 * Certain units must be converted before being sent to the API.
 * For example, if the frontend is set to enter distance 
 * values in nautical miles, they must be converted to kilometers.
 * 
 * This directive is placed on create / update buttons
 * and is given the property name for the value to convert
 * 
 * After conversion, it proceeds with the crud function
 * 
 * example use:
 * unit-converter unit-to-convert="nameOfProperty"
 * conversion-complete="create(convertedEditable || editable)"
 */

 // interfaces
import { ISystemConfiguration } from '../../../../partials/system-configuration/system-configuration.interface';

// services
import { LocalStorageService } from '../services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';

/** @ngInject */
export function unitConverter(): angular.IDirective {
  return {
    restrict: 'A',
    link: linkFunc,
    priority: 2
  };
}

function linkFunc(scope: ng.IScope, elem: ng.IAugmentedJQuery, attrs: any): void {
  const TO_KM = 1.852;
  const unitToConvert = attrs.unitToConvert;
  const conversionComplete = attrs.conversionComplete; // function to run after conversion

  function runClickEvent(func: string): void {
    scope.$eval(func);
  }

  elem.bind('click', () => {
    const localStorageValue = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.DISTANCE_UNIT_OF_MEASURE}`);
    const distanceConfig = scope.list.find((item: ISystemConfiguration) =>
      item.item_name === SysConfigConstants.DISTANCE_UNIT_OF_MEASURE.toString()
    );
    const distanceUnitOfMeasure = distanceConfig ? distanceConfig.current_value : '';

    scope.convertedList = angular.copy(scope.list);
    scope.convertedEditable =  angular.copy(scope.editable);

    if ((isChangingToKM() && !isChangingToNM()) || localStorageValue === 'nm') {
      if (unitToConvert === 'distance') {
        const itemToConvertIndexes = scope.list.filter(
          (item: any) => item.item_name.includes('distance') && ['int', 'float'].indexOf(item.data_type.name) > -1)
            .map((item: any) => scope.list.indexOf(item)
        );

        itemToConvertIndexes.forEach((itemIndex: number) => {
          scope.convertedList[itemIndex].current_value = scope.convertedList[itemIndex].current_value * TO_KM;
        });
      } else if (unitToConvert === 'user_crossing_distance') {
        scope.convertedEditable[unitToConvert] = scope.convertedEditable[unitToConvert] * TO_KM;
      }
    }

    // if NM is in local storage
    // and KM selected on interface
    function isChangingToKM(): boolean {
      return ((unitToConvert === 'user_crossing_distance' && localStorageValue === 'nm') ||
              (unitToConvert === 'distance' && localStorageValue === 'nm' && distanceUnitOfMeasure === 'km'));
    }

    // if KM is in local storage
    // and NM selected on interface
    function isChangingToNM(): boolean {
      return unitToConvert === 'distance' && localStorageValue === 'km' && distanceUnitOfMeasure === 'nm';
    }

    if (conversionComplete) {
      runClickEvent(conversionComplete); // continue with create
    }
  });
}
