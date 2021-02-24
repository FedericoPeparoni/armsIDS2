/**
 *
 * MultiselectDecorator is used to override functionality found within the angularjs-dropdown-multiselect package.
 *
 */

interface IMultiselect extends ng.IScope {
  option: Object;
  selectedItem: Object;
  label: String;
}

const ITEMS_TO_DISPLAY = 5;

const createButtonText = ($scope: ng.IScope) => {
  // return placeholder if none selected or options is empty
  if (!$scope.options || !$scope.selectedModel.length) { return $scope.texts.buttonDefaultText; }

  const selectedItems = $scope.options.filter((option: IMultiselect) =>
    // selectedModel contains selected options
    $scope.selectedModel.map((selectedItem: IMultiselect) => selectedItem.id).includes(option.id)
  );

  const selectedItemsText = selectedItems.slice(0, ITEMS_TO_DISPLAY).map((selectedItem: IMultiselect) =>
    $scope.extraSettings ? selectedItem[$scope.extraSettings.displayProp ] : selectedItem.label
  );

  // add ellipsis if more than ITEMS_TO_DISPLAY selected
  const buttonText = $scope.selectedModel.length > ITEMS_TO_DISPLAY
  ? [...selectedItemsText, '...']
  : selectedItemsText;

  return buttonText.join(', ');
};

const updateLabelText = (translate: angular.translate.ITranslateService): object => {
  return {
    buttonDefaultText: translate.instant('Select'),
    checkAll: translate.instant('Check All'),
    uncheckAll: translate.instant('Uncheck All'),
    searchPlaceholder: `${translate.instant('Search')}...`
  };
};

/** @ngInject */
export const MultiselectDecorator = ($provide: ng.IScope) => {
  $provide.decorator('ngDropdownMultiselectDirective', ($delegate: ng.IScope, $translate: angular.translate.ITranslateService, $rootScope: ng.IRootScopeService) => {
    const directive = $delegate[0];
    const link = directive.link;

    directive.compile = () => {
      return function($scope: ng.IScope, element: ng.IAugmentedJQuery, attrs: any): void {

        link.apply(this, arguments);

        ($translate as any).onReady(() => $scope.texts = updateLabelText($translate));
        $rootScope.$on('$translateChangeEnd', () => $scope.texts = updateLabelText($translate));

        // override function provided by package for displaying selected items
        $scope.getButtonText = () => createButtonText($scope);
      };
    };

    return $delegate;
  });
};
