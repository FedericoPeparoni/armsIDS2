
const updateLabelText = (scope: ng.IScope, translate: angular.translate.ITranslateService): void => {
    scope.closeText = translate.instant('Close');
    scope.currentText = translate.instant('Today');
    scope.clearText = translate.instant('Clear');
};

/** @ngInject */
export const DatePickerDecorator = ($provide: ng.IScope) => {

  $provide.decorator('uibDatepickerPopupDirective', ($delegate: ng.IScope, $translate: angular.translate.ITranslateService, $rootScope: ng.IRootScopeService) => {
    const directive = $delegate[0];
    const link = directive.link;

    directive.compile = () => {
      return function($scope: ng.IScope, element: ng.IAugmentedJQuery, attrs: any): void {
        link.apply(this, arguments);

        ($translate as any).onReady(() => updateLabelText($scope, $translate));
        $rootScope.$on('$translateChangeEnd', () => updateLabelText($scope, $translate));
      };
    };

    return $delegate;
  });
};
