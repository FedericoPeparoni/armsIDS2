describe('directive DatePicker', () => {
  let element: any; // is HTMLInputElement | angular.IAugmentedJQuery;
  let scope;
  let controller;
  let template;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($httpBackend: angular.IHttpBackendService, $compile: angular.ICompileService, $rootScope: angular.IRootScopeService) => {

    $httpBackend.when('GET', (url: string): any => {
      if ('http://localhost:8080/api/system-configurations/noauth') {
        return true;
      };
    }).respond('ok');

    scope = $rootScope.$new();

    element = angular.element(`<form name="form"><date-picker id="date-of-flight"
                                            show-button-bar="false"
                                            name="date"
                                            ng-model="model"></date-picker></form>`);
    template = $compile(element)(scope);
    controller = element.controller('datePicker');
    scope.$apply();

  }));

  describe('should compile', () => {

    it('should be defined', () => {
      const el = angular.element(template[0]).find('input')[0];
      expect(el).toBeDefined();
    });

    it('should be an `input` tag', () => {
      const el = angular.element(template[0]).find('input')[0];
      expect(el.nodeName).toBe('INPUT');
    });

  });

  describe('updating the value', () => {

    it('via the model should update the value', () => {
      const el = <any>angular.element(template[0]).find('input')[0];
      scope.model = new Date('2017-12-25');
      scope.$digest();
      expect(scope.model.toISOString()).toBe('2017-12-25T00:00:00.000Z');
      let currentViewValue = el.value;
      scope.model = new Date('2017-01-01');
      expect(scope.model.toISOString()).toBe('2017-01-01T00:00:00.000Z');
      scope.$digest();

      expect(currentViewValue).not.toEqual(el.value); // ensure $viewValue updates after $apply
    });

    it('via manually, should update the model', () => {
      const date = '2017-01-01T00:00:00.000Z';
      expect(scope.form.date.$viewValue).toBeUndefined();
      angular.element(element.find('input')[0]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.form.date.$viewValue).toBeDefined();
      expect(scope.model.toISOString()).toBe(date);
    });

  });

});
