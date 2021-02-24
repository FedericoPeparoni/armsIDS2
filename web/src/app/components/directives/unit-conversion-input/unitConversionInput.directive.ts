/**
 * Unit conversion input
 *
 * Currently only used for MTOW, CARGO and USER_CROSSING_DISTANCE
 *
 * ex:
 * <unit-conversion-input id="upper-limit" name="upperLimit" ng-model="editable.upper_limit" measurement-type="weight" required />
 */

 // services
import { SystemConfigurationService } from '../../../partials/system-configuration/service/system-configuration.service';

// constants
import { SysConfigConstants } from '../../../partials/system-configuration/system-configuration.constants';

// mtow settings
const MTOW = {
  min: 0,
  max_kg: 907186,
  max_tons: 1000,
  pattern_kg: /^\d*?$/,
  pattern_tons: /^(?!0\d|$)\d*(\.\d{1,})?$/,
  step_kg: 1,
  step_tons: 0.01
};

// cargo settings
const CARGO = {
  min: 0,
  max_kg: 999000,
  max_tons: 1101.21,
  pattern_kg: /^\d*?$/,
  pattern_tons: /^(?!0\d|$)\d*(\.\d{1,})?$/,
  step_kg: 1,
  step_tons: 0.01
};

/** @ngInject */
export function unitConversionInput(): angular.IDirective {
  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/unit-conversion-input/unitConversionInput.template.html',
    controller: UnitConversionInputController,
    controllerAs: 'UnitConversionInputController',
    replace: true,
    require: 'ngModel',
    scope: {
      id: '@',
      name: '@',
      ngModel: '=',
      isRequired: '@',
      measurementType: '@'
    }
  };
}

/** @ngInject */
class UnitConversionInputController {

  constructor ($scope: ng.IScope, systemConfigurationService: SystemConfigurationService) {
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);
    $scope.cargoDisplayUnits = systemConfigurationService.getValueByName(<any>SysConfigConstants.CARGO_DISPLAY_UNITS);

    $scope.isWeight = $scope.measurementType === 'weight';
    $scope.isCargo = $scope.measurementType === 'cargo';

    if ($scope.isWeight) {
      $scope.min = MTOW.min;
      $scope.max = $scope.mtowUnitOfMeasure === 'kg' ? MTOW.max_kg : MTOW.max_tons;
      $scope.step = $scope.mtowUnitOfMeasure === 'kg' ? MTOW.step_kg : MTOW.step_tons;
      $scope.pattern = $scope.mtowUnitOfMeasure === 'kg' ? MTOW.pattern_kg : MTOW.pattern_tons;
    }

    if ($scope.isCargo) {
      $scope.min = CARGO.min;
      $scope.max = $scope.cargoDisplayUnits === 'kg' ? CARGO.max_kg : CARGO.max_tons;
      $scope.step = $scope.cargoDisplayUnits === 'kg' ? CARGO.step_kg : CARGO.step_tons;
      $scope.pattern = $scope.cargoDisplayUnits === 'kg' ? CARGO.pattern_kg : CARGO.pattern_tons;
    }
  }

}
