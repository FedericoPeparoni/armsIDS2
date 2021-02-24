/**
 * Directive to be placed on text inputs
 * Check validation for Aerodrome  names
 */

/** @ngInject */
export function aerodromeIdentifiers(): angular.IDirective {

  return {
    require: 'ngModel',
    link: (scope: ng.IScope, elem: ng.IRootElementService, attrs: ng.IAttributes, modelCtrl: ng.INgModelController): any => {

      modelCtrl.$parsers.push((inputValue: any) => {

        let transformedInput: any;
        let maxlength = 4;

        if (inputValue) {
          transformedInput = inputValue[0].replace(/[^a-zA-Z]/g, '').toUpperCase();
        }

        for (let i = 1, len = inputValue.length; i < len; i++) {
          transformedInput += inputValue[i].replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
          if (transformedInput.length > maxlength) {
              transformedInput = inputValue.substring(0, maxlength);
          }
        }

        if (transformedInput !== inputValue) {
          modelCtrl.$setViewValue(transformedInput);
          modelCtrl.$render();
        }

        return transformedInput;
      });
    }
  };
}
