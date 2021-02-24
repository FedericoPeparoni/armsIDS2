/**
 * Directive to be placed on text inputs
 * Converts all input to uppercase
 */

interface IUppercaseInputElement extends HTMLElement {
  selectionStart: number;
  selectionEnd: number;
  setSelectionRange: (start: number, end: number) => void;
}

/** @ngInject */
export function uppercaseInput(): angular.IDirective {

  const inputToUppercase = (val: string, modelCtrl: ng.INgModelController, elem: ng.IRootElementService) => {
    if (!val || !val.length) { return; }

    const targetElement = <IUppercaseInputElement>elem[0];

    // store position to prevent line jump
    const start = targetElement.selectionStart;
    const end = targetElement.selectionEnd;

    // set value and rerender
    modelCtrl.$setViewValue(val.toUpperCase());
    modelCtrl.$render();

    // restore position
    targetElement.setSelectionRange(start, end);
  };

  return {
    require: 'ngModel',
    restrict: 'A',
    link: (scope: ng.IScope, elem: ng.IRootElementService, attrs: ng.IAttributes, modelCtrl: ng.INgModelController): any => {
      scope.$watch(() => modelCtrl.$modelValue, (val: any) => inputToUppercase(val, modelCtrl, elem));
    }
  };
}
