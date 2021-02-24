import { LocalStorageService } from '../services/localStorage/localStorage.service';
import { IPopupScope, IPopupAttributes } from './popup.interface';

/**
 *  Popup!
 *
 *  NOTE: use the popup-confirm event as the `ng-click`.  This will not override the `ng-click`
 *        I tried fixing this with `terminal` ($compile) attribute for directive, that fixes one issue but creates many more
 *
 *  what this does currently is check if localStorage item is set to true, if so, we continue
 *
 *  this will intercept the ng-click with a confirm/cancel dialog, if `confirm` is clicked, it will run the `ng-click` for that element
 *  if `cancel`, will not fire `ng-click`
 *
 *  popup {required} This binds the directive
 *  popup-text {required} {string} The text to display in the popup
 *  popup-local-storage-enable {required} {string} The local storage variable to determine whether to include this
 *  popup-confirm {required} {any} when confirm is clicked, these events fire.  Works like an `ng-click`
 */


/** @ngInject */
export function popup(): angular.IDirective {

  return {
    restrict: 'A',
    bindToController: false,
    controller: PopupController,
    priority: 1, // this will fire before `ng-click`, `ng-click` will still fire!
    link: linkFunc
  };

}

/** @ngInject */
export class PopupController {
  constructor(private $scope: ng.IScope, private $uibModal: any) {
    this.$uibModal = $uibModal;
  }
}

function linkFunc(scope: ng.IScope, elem: ng.IAugmentedJQuery, attrs: IPopupAttributes, ctrl: any): void {

  let popupText = attrs.popupText !== '' ? attrs.popupText : 'Are you sure?'; // if no attribute is supplied, we use the default
  let createWarning, updateWarning, deleteWarning, saveAs;

  attrs.$observe('createWarning', function(val: string): void {
    createWarning = val;
  });

  attrs.$observe('updateWarning', function(val: string): void {
    updateWarning = val;
  });

  attrs.$observe('deleteWarning', function(val: string): void {
    deleteWarning = val;
  });

  attrs.$observe('saveAs', function(val: boolean): void {
    saveAs = val;
  });

  let popUpConfirmAction = attrs.popupConfirm; // this is required at this time

  function runClickEvent(popUpConfirmAction: string): void { // runs events
    scope.$eval(popUpConfirmAction);
  }

  elem.bind('click', () => {

    if (attrs.popupLocalStorageEnable) { // checks localStorage variable if it should actually popup, if not, nothing will be added
      let continueWithDialog = LocalStorageService.get(attrs.popupLocalStorageEnable);
      if (continueWithDialog !== true && continueWithDialog !== 't') { // because booleans are stored as `t` in database
        runClickEvent(popUpConfirmAction); // runs click event, does not continue with modal
        return;
      }
    }

    if (attrs.popupEnabled === 'false') {
        runClickEvent(popUpConfirmAction); // runs click event, does not continue with modal
        return;
    }

    let modal = ctrl.$uibModal.open({
      templateUrl: 'app/angular-ids-project/src/components/popup/popup.template.html',
      controller: ['$scope', ($scope: IPopupScope) => {

        $scope.text = popupText;
        $scope.saveAs = saveAs;
        $scope.createWarning = createWarning;
        $scope.updateWarning = updateWarning;
        $scope.deleteWarning = deleteWarning;

        $scope.setName = (inputText: string) => scope.inputText = inputText;
        $scope.close = () => modal.close(); // bind the `close` scope method
        $scope.confirm = () => {
          runClickEvent(popUpConfirmAction); // runs ng-click action, note: scope of the element, not the dialog
          modal.close();
        };
      }]
    });

  });

}
