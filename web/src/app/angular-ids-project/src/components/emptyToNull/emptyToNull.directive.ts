/**
 * Directive to be placed on text inputs
 * Changes model values from empty strings to null
 */

/** @ngInject */
export function emptyToNull(): angular.IDirective {
    return {
        require: 'ngModel',
        link: (scope: ng.IScope, elem: ng.IRootElementService, attrs: ng.IAugmentedJQuery, modelCtrl: ng.INgModelController): void => {
            modelCtrl.$parsers.push(function(viewValue: string): string {
                if (viewValue === '') {
                    return null;
                }
                return viewValue;
            });
        }
    };
}
