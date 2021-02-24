/**
 * Directive to be placed on text inputs
 * Allows only numbers to be entered
 */

/** @ngInject */
export function onlyLetters(): angular.IDirective {
  return {
      require: 'ngModel',
      link: (scope: ng.IScope, elem: ng.IRootElementService, attrs: ng.IAttributes, modelCtrl: ng.INgModelController): any => {

          modelCtrl.$parsers.push((inputValue: any) => {

              let transformedInput = inputValue.replace(/[^a-zA-Z]/g, '');

              if (transformedInput !== inputValue) {
                  modelCtrl.$setViewValue(transformedInput);
                  modelCtrl.$render();
              }

              return transformedInput;
          });
      }
  };
}
