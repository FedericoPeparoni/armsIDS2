/* Shows a tooltip on hover saying not implemented */
import { INotImplementedScope } from './notImplemented.interface';

/** @ngInject */
export function notImplemented(): angular.IDirective {

  return {
    controller: NotImplementedController,
    controllerAs: 'NotImplementedController',
    bindToController: true,
    restrict: 'A',
    compile: (): any => {
      return {
        pre: function precompile(scope: INotImplementedScope, element: any): void {
          if (angular.isUndefined(element.attr('uib-tooltip'))) {
            element.attr('uib-tooltip', 'this has not been implemented yet');
            scope.NotImplementedController.$compile(element)(scope);
          }
        }
      };
    }
  };
}

/** @ngInject */
export class NotImplementedController {
  constructor(private $compile: ng.ICompileService) {
    this.$compile = $compile;
  }
}
