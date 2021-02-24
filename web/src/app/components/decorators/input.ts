import { IExtendableError, IError, IFieldError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

import { DISPLAY, MESSAGES } from '../../angular-ids-project/src/components/input/input.constants';

/**
 * InputDecorator is used to extend functions found on the input directive.
 * Functions are extended to give more control and functionality for displaying errors
 */

// apply class to corresponding label
const setLabelError = (element: HTMLElement) => {
  const labels = (element as any).labels || [];

  for (let label of labels) {
    const classList = label.classList;

    // check if label has already been updated
    if (!classList.contains(DISPLAY.LABEL_CLASS)) {
      classList.add(DISPLAY.LABEL_CLASS);
    }
  }
};

// remove all corresponding labels
const clearLabelError = (element: HTMLElement) => {
  const labels = (element as any).labels || [];

  for (let label of labels) {
    label.classList.remove(DISPLAY.LABEL_CLASS);
  }
};

// add error icon to input with error
const setErrorIcon = (element: HTMLElement) => {
  const nextSibling = element.nextElementSibling;

  // check if icon has already been created
  if (nextSibling && nextSibling.className.includes(DISPLAY.INPUT_CLASS)) {
    return;
  }

  // create icon, set class, and append to input
  const error = document.createElement('span');
  error.setAttribute(
    'class',
    DISPLAY.INPUT_CLASS + ' ' + DISPLAY.ICON_CLASS
  );

  element.parentNode.insertBefore(error, nextSibling);
};

// remove error icon from input
const clearErrorIcon = (element: HTMLElement) => {
  const nextSibling = element.nextElementSibling;

  if (nextSibling && nextSibling.className.includes(DISPLAY.INPUT_CLASS)) {
    nextSibling.remove();
  }
};

// display form errors in red box used for API errors
const setFormErrors = ($scope: any, ctrlTarget: any, element: HTMLElement) => {
  // find appropriate scope with form value
  const errorTarget = $scope.form
    // form on current scope
    ? $scope
    // form on parent scope
    : $scope.$parent;

  // return if no target scope with a form was found
  if (!errorTarget) {
    return;
  }

  // get error message for field, combine all that apply
  let message = null;
  for (const key of Object.keys(ctrlTarget.$error)) {

    // skip messages that are not found
    if (!MESSAGES[key]) {
      continue;
    }

    // add message onto existing message with space and periods
    message = message === null ? '' : message + ' ';
    message += MESSAGES[key] + '.';
  }

  // return if no message to set
  if (message === null) {
    return;
  }

  // get all labels and name for the element
  const labels = (element as any).labels || [];
  const name = ctrlTarget.$name;

  // get field text for readability and translation
  const field = labels && labels[0] && labels[0].textContent
    ? labels[0].textContent.replace('*', '').trim()
    : name;

  // make sure necessary error objects exist
  errorTarget.error = errorTarget.error || <IExtendableError>{};
  errorTarget.error.error = errorTarget.error.error || {};
  errorTarget.error.error.data = errorTarget.error.error.data || <IError>{};
  errorTarget.error.error.data.form_errors = errorTarget.error.error.data.form_errors || [];

  // prevent duplicate errors, only update message and field if exists
  const formErrors = errorTarget.error.error.data.form_errors;
  const fieldError: IFieldError = formErrors.find((error: IFieldError) => error.name === name);
  if (fieldError) {
    fieldError.field = field;
    fieldError.message = message;
  } else {
    formErrors.push(<IFieldError>{ field, message, name });
  }
};

const clearFormErrors = ($scope: any, ctrlTarget: any) => {
  const errorTarget = $scope.form
    // form on current scope
    ? $scope
    // form on parent scope
    : $scope.$parent;

  // ensure the an error exists
  if (!(errorTarget
    && errorTarget.error
    && errorTarget.error.error
    && errorTarget.error.error.data
    && errorTarget.error.error.data.form_errors)
  ) {
    return;
  }

  const formErrors = errorTarget.error.error.data.form_errors;

  // return form errors sans valid input
  errorTarget.error.error.data.form_errors = formErrors.filter((error: IFieldError) => error.name !== ctrlTarget.$name);
};

const clearAllErrors = ($scope: any, elementTarget: HTMLElement) => {
  $scope.error = null;
  $scope.$parent.error = null;

  clearLabelError(elementTarget);
  clearErrorIcon(elementTarget);
};

const handleErrorDisplay = ($scope: ng.IScope, ctrlTarget: any, elementTarget: HTMLElement) => {
  const { $invalid, $valid, $dirty, $touched, $$parentForm } = ctrlTarget;

  // required fields are not valid -- check for touched
  const invalidAndDirtyAndTouched = $invalid && $dirty && $touched;

  // date range inputs react to other in pair -- check for dirty and NOT valid
  const dirtyAndDateRange = ($$parentForm.$name === DISPLAY.DATE_RANGE_FORM && ($dirty && !$valid));

  if (invalidAndDirtyAndTouched || dirtyAndDateRange) {
    setLabelError(elementTarget);
    setErrorIcon(elementTarget);

    // don't show box for date range
    if (!dirtyAndDateRange) {
      setFormErrors($scope, ctrlTarget, elementTarget);
    }
  } else {
    clearLabelError(elementTarget);
    clearErrorIcon(elementTarget);
    clearFormErrors($scope, ctrlTarget);
  }
};


/** @ngInject */
export const InputDecorator = ($provide: ng.IScope) => {
  $provide.decorator('inputDirective', ($delegate: ng.IScope) => {
    const directive = $delegate[1];
    const link = directive.link;

    directive.compile = () => {
      return function($scope: ng.IScope, element: ng.IAugmentedJQuery, attrs: any, ctrl: any): void {
        link.apply(this, arguments);

        const ctrlTarget = ctrl;
        const elementTarget = element[0];

        // exit if control is not accessible
        if (!ctrlTarget) {
          return;
        }

        const setValidity = ctrlTarget.$setValidity;
        const setUntouched = ctrlTarget.$setUntouched;
        const setTouched = ctrlTarget.$setTouched;

        ctrlTarget.$setValidity = function (): void {
          setValidity.apply(this, arguments);

          handleErrorDisplay($scope, ctrlTarget, elementTarget);
        };

        ctrlTarget.$setTouched = function (): void {
          setTouched.apply(this, arguments);

          handleErrorDisplay($scope, ctrlTarget, elementTarget);
        };

        ctrlTarget.$setUntouched = function(): void {
          setUntouched.apply(this, arguments);

          clearAllErrors($scope, elementTarget);
        };

        ctrlTarget.$setManualError = function(): void {
          ctrlTarget.$setDirty();
          ctrlTarget.$setTouched();
        };

      };
    };

    return $delegate;
  });

};
