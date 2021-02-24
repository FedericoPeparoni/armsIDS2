// constants
import { DISPLAY, MESSAGES } from './input.constants';

// interfaces
import { IInput, IInputAttrs, IInputCtrl, IInputElement } from './input.interface';

/**
 * /**
 * Input Clientside Error Tooltip
 *
 * When an input field is considered invalid, a tooltip will appear on hover
 *
 * NOTE: This is not an isolate scope!
 *
 * @returns {{restrict: string, controller: InputController, controllerAs: string, link: (function(IInput, ng.IAugmentedJQuery, ng.IAttributes, ng.INgModelController): void), require: string}}
 */

export function input(): angular.IDirective {
  return {
    restrict: 'E',
    controller: InputController,
    controllerAs: 'InputController',
    link: linkFunc,
    require: ['?ngModel', '?^^form']
  };
}

function linkFunc(scope: IInput, elem: IInputElement, attr: IInputAttrs, ctrls: Array<any>): void {

  const ctrl: IInputCtrl = ctrls[0];
  const formCtrl: ng.IFormController = ctrls[1];

  if (ctrl === null) { // if input does not have `ng-model` attribute, we do not continue
    return;
  }

  let el;
  let errorHasBeenCreated: boolean = false; // this prevents multiple tooltips from being created
  let id: string = String(new Date().getTime()) + String(Math.floor(Math.random() * (100 - 0 + 1))); // because the scope is not isolate, we just generate an "id" from milleseconds but also a random number.  This could be avoided with an isolate scope

  if (typeof scope.showTooltip === 'undefined') {
    scope.showTooltip = {};
  }

  scope.showTooltip[id] = false;

  scope.errorMessages = MESSAGES;

  scope.minLength = attr.minLength;
  scope.$watch(attr.minLength, (val: string) => scope.minLength = val);

  function setInputStateToDirty(): void {
    scope.errorTooltip = ctrl.$error;
    ctrl.$setDirty(); // sets the input as dirty, for scss reasons
    ctrl.$setTouched(); // sets the input as touched, for scss reasons
  }

  function tooltip(): void {
    if (attr && attr.inputFormat && ctrl.$error.pattern) {
      ctrl.$error.patternFormat = attr.inputFormat;
    } else if (ctrl.$error.patternFormat) {
      delete ctrl.$error.patternFormat;
      ctrl.$validate();
    }

    if (ctrl.$valid && typeof el !== 'undefined' && errorHasBeenCreated) {
      scope.showTooltip[id] = false; // force hide the tooltip
      errorHasBeenCreated = false; // reset value
    } else if (ctrl.$invalid && ctrl.$touched && !errorHasBeenCreated) { // is not valid and an error has not occurred yet
      setInputStateToDirty();
      el = scope.InputController.$compile(`<span class="error error-tooltip" uib-tooltip-template="'app/angular-ids-project/src/components/input/input.tooltip.html'" trigger="''" tooltip-is-open="showTooltip[${id}]"><!-- Empty --></span>`)(scope);
      elem.parent().append(el);
      errorHasBeenCreated = true;
    } else if (ctrl.$invalid && ctrl.$touched) { // is invalid, error bubble has already been created
      setInputStateToDirty();
    }
  }

  scope.$watch(attr.ngModel, (val: string) => { // if the model changes, we check the validity, this may occur outside the directive

    // set api-response validation to true to remove the error highlighting sent back from the server
    ctrl.$setValidity(DISPLAY.API_RESPONSE_CLASS, true);

    // if existing api response duplicate is invalid, set to true and reset all form field duplicate validation errors as well if exist
    if (ctrl.$error[DISPLAY.API_RESPONSE_EXISTS_CLASS]) {

      // set response duplicate error to valid and do the same for all remaining form duplicate errors if they exist
      ctrl.$setValidity(DISPLAY.API_RESPONSE_EXISTS_CLASS, true);
      if (formCtrl && formCtrl.$error && formCtrl.$error[DISPLAY.API_RESPONSE_EXISTS_CLASS]) {

        // loop in reverse as $setValidity ends up removing item from array index
        const errors: Array<IInputCtrl> = formCtrl.$error[DISPLAY.API_RESPONSE_EXISTS_CLASS];
        for (let i = errors.length - 1; i >= 0; i--) {
          errors[i].$setValidity(DISPLAY.API_RESPONSE_EXISTS_CLASS, true);
        }
      }
    }

    if (val !== null) { // on a clean "pristine" input, we do not want to show the error
      tooltip();
    }
  });

  elem.bind('mouseenter', (): void => { // displays tooltip if it needs to
    if (errorHasBeenCreated && !angular.equals(ctrl.$error, {}) && !ctrl.$pristine) { // checks to see if object is empty, if so display nothing.  Also will only display if the element has been altered
      scope.errorTooltip = ctrl.$error;
      scope.showTooltip[id] = true;
    }
  });
  elem.bind('mouseleave', (): boolean => scope.showTooltip[id] = false); // hides tooltip
  elem.bind('keyup', tooltip); // on keyup it checks validity and updates tooltip
  elem.bind('blur', tooltip); // on blur it checks validity and updates tooltip
}

/** @ngInject */
export class InputController {

  constructor(private $compile: ng.ICompileService, private $sce: ng.ISCEService) {
    this.$compile = $compile;
    this.$sce = $sce;
  }

}
