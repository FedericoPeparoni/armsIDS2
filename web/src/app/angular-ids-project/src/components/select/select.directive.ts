// interfaces
import { DISPLAY } from '../input/input.constants';

/**
 * Select Clientside Error Hanlding
 *
 * NOTE: This is not an isolate scope!
 */
export function select(): angular.IDirective {
  return {
    restrict: 'E',
    link: linkFunc,
    require: ['?ngModel', '?^^form']
  };
}

function linkFunc(scope: angular.IScope, elem: angular.IAugmentedJQuery, attr: angular.IAttributes, ctrls: Array<any>): void {

  const ctrl: angular.INgModelController = ctrls[0];
  const formCtrl: angular.IFormController = ctrls[1];

  if (ctrl === null) { // if input does not have `ng-model` attribute, we do not continue
    return;
  }

  // if the model changes, we check the validity, this may occur outside the directive
  scope.$watch(attr.ngModel, (val: string) => {

    // set api-response validation to true to remove the error highlighting sent back from the server
    ctrl.$setValidity(DISPLAY.API_RESPONSE_CLASS, true);

    // if existing api response duplicate is invalid, set to true and reset all form field duplicate validation errors as well if exist
    if (ctrl.$error[DISPLAY.API_RESPONSE_EXISTS_CLASS]) {

      // set response duplicate error to valid and do the same for all remaining form duplicate errors if they exist
      ctrl.$setValidity(DISPLAY.API_RESPONSE_EXISTS_CLASS, true);
      if (formCtrl && formCtrl.$error && formCtrl.$error[DISPLAY.API_RESPONSE_EXISTS_CLASS]) {

        // loop in reverse as $setValidity ends up removing item from array index
        const errors: Array<angular.INgModelController> = formCtrl.$error[DISPLAY.API_RESPONSE_EXISTS_CLASS];
        for (let i = errors.length - 1; i >= 0; i--) {
          errors[i].$setValidity(DISPLAY.API_RESPONSE_EXISTS_CLASS, true);
        }
      }
    }
  });
}
