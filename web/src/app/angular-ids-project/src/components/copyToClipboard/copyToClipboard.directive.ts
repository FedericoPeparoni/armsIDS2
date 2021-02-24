// interfaces
import { ICopyToClipboardScope } from './copyToClipboard.interface';

// services
import { HelperService } from '../services/helpers/helpers.service';

/** @ngInject */
export function copyToClipboard(): angular.IDirective {
  return {
    restrict: 'A',
    link: linkFunc,
    priority: 2,
    scope: {
      copyToClipboard: '='
    }
  };
}

function linkFunc(scope: ICopyToClipboardScope, element: angular.IAugmentedJQuery, attrs: any): void {

  element.bind('click', () => {
    HelperService.copyToClipboard(document.getElementById(scope.copyToClipboard));
  });
}
