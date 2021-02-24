describe('directive DateRange', () => {
  let element: angular.IAugmentedJQuery;
  let scope;
  let template;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($httpBackend: angular.IHttpBackendService, $compile: angular.ICompileService, $rootScope: angular.IRootScopeService) => {

    $httpBackend.when('GET', (url: string): any => {
      if ('http://localhost:8080/api/system-configurations/noauth') {
        return true;
      };
    }).respond('ok');

    scope = $rootScope.$new();

    element = angular.element(`<form name="form"><date-range is-required="required" start-end-adjust="adjust" control="control"></date-range></form>`);

    scope.required = false;
    scope.adjust = false;

    template = $compile(element)(scope);
    scope.$apply();

  }));

  it('should be compiled', () => {
    const el = angular.element(template[0]).find('input');
    expect(el).toBeDefined();
  });

  it('should have two input tags', () => {
    const el = angular.element(template[0]).find('input');
    expect(el.length).toBe(2);
  });

  describe('method setUTCStartDate', () => {

    it('should set correct UTC date from outside', () => {
      const date = new Date('2013-02-05T15:12:23.000Z');
      scope.control.setUTCStartDate(date);
      scope.$digest();
      expect(scope.control.getUTCStartDate().toISOString()).toBe('2013-02-05T00:00:00.000Z');
    });

    it('should set correct UTC date from outside, and when `adjusted` should go to the first of the month', () => {
      const date = new Date('2013-02-05T15:12:23.000Z');
      scope.adjust = true;
      scope.$digest();
      scope.control.setUTCStartDate(date);
      scope.$digest();
      expect(scope.control.getUTCStartDate().toISOString()).toBe('2013-02-01T00:00:00.000Z');
    });


    it('should be null if setting it as a bad date', () => {
      scope.control.setUTCStartDate('FOO');
      scope.$digest();
      expect(scope.control.getUTCStartDate()).toBeNull();
    });

    it('should set the minDate for the end date', () => {
      expect(scope.control.end.minDate).toBeNull();
      scope.control.setUTCStartDate(new Date());
      expect(scope.control.end.minDate).not.toBeNull();
    });

  });

  describe('method setUTCEndDate', () => {

    it('should set correct UTC date from outside', () => {
      const date = new Date('2013-02-05T15:12:23.000Z');
      scope.control.setUTCEndDate(date);
      scope.$digest();

      expect(scope.control.getUTCEndDate().toISOString()).toBe('2013-02-05T23:59:59.000Z');
    });

    it('should set correct UTC date from outside, and when `adjusted` should go to the end of the month', () => {
      const date = new Date('2013-02-05T15:12:23.000Z');
      scope.adjust = true;
      scope.$digest();
      scope.control.setUTCEndDate(date);
      scope.$digest();
      expect(scope.control.getUTCEndDate().toISOString()).toBe('2013-02-28T23:59:59.000Z');
    });

    it('should be null if setting it as a bad date', () => {
      scope.control.setUTCEndDate('FOO');
      scope.$digest();
      expect(scope.control.getUTCEndDate()).toBeNull();
    });

    it('should set the minDate for the end date', () => {
      expect(scope.control.start.maxDate).toBeNull();
      scope.control.setUTCEndDate(new Date());
      expect(scope.control.start.maxDate).not.toBeNull();
    });

  });

    // date validation
  describe('date validation', () => {

    it('should by default be `valid`', () => {
      expect(scope.control.dateRangeForm.end.$valid).toBe(true);
    });

    it('should be set from outside to invalid when startDate > endDate', () => {
      scope.control.setUTCStartDate(new Date('2017-04-04T15:12:23.000Z'));
      scope.control.setUTCEndDate(new Date('2017-04-03T15:12:23.000Z'));
      scope.$digest();
      expect(scope.control.dateRangeForm.end.$valid).toBe(false);
    });

    it('should be set from outside to valid when startDate <= endDate', () => {
      scope.control.setUTCStartDate(new Date('2017-04-05T15:12:23.000Z'));
      scope.control.setUTCEndDate(new Date('2017-04-05T15:12:23.000Z'));
      scope.$digest();
      expect(scope.control.dateRangeForm.end.$valid).toBe(true);
    });

    it('should become invalid when date changes manually and startDate > endDate', () => {
      angular.element(element.find('input')[0]).val('2017-05-06').triggerHandler('input');
      angular.element(element.find('input')[1]).val('2017-04-04').triggerHandler('input');
      scope.$digest();

      expect(scope.control.dateRangeForm.end.$valid).toBe(false);
    });

    it('should become valid when date changes manually and startDate <= endDate', () => {
      angular.element(element.find('input')[0]).val('2017-05-06').triggerHandler('input');
      angular.element(element.find('input')[1]).val('2017-05-06').triggerHandler('input');
      scope.$digest();
      expect(scope.control.dateRangeForm.end.$valid).toBe(true);

    });

  });

  // manual changes to the directive
  describe('start date', () => {

    it('should be UTC date when setting it manually', () => {
      const date = '2017-05-06';
      angular.element(element.find('input')[0]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCStartDate().toISOString()).toBe('2017-05-06T00:00:00.000Z');
    });

    it('when setting as an invalid date manually, should become undefined', () => {
      let date = '2017-05-06';
      angular.element(element.find('input')[0]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCStartDate().toISOString()).toBe('2017-05-06T00:00:00.000Z');
      date = 'FOO';
      angular.element(element.find('input')[0]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCStartDate()).toBeUndefined();
    });

    it('should be undefined if setting it as a bad date', () => {
      angular.element(element.find('input')[0]).val('FOO').triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCStartDate()).toBeUndefined();
    });

    it('should set the minDate for the end date', () => {
      expect(scope.control.end.minDate).toBeNull();
      angular.element(element.find('input')[0]).val('2013-05-12').triggerHandler('input');
      scope.$digest();
      expect(scope.control.end.minDate).not.toBeNull();
    });

  });

  describe('end date', () => {

    it('should be UTC date when setting it manually, end of the day, last hour', () => {
      const date = '2017-05-06';
      angular.element(element.find('input')[1]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCEndDate().toISOString()).toBe('2017-05-06T23:59:59.000Z');
    });


    it('when setting as an invalid date manually, should become undefined', () => {
      let date = '2017-05-06';
      scope.adjust = true;
      scope.$digest();
      angular.element(element.find('input')[1]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCEndDate().toISOString()).toBe('2017-05-31T23:59:59.000Z');
      date = 'FOO';
      angular.element(element.find('input')[1]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCEndDate()).toBeUndefined();
    });

    it('should set the minDate for the end date', () => {
      expect(scope.control.start.maxDate).toBeNull();
      angular.element(element.find('input')[1]).val('2013-05-12').triggerHandler('input');
      scope.$digest();
      expect(scope.control.start.maxDate).not.toBeNull();
    });

  });

  describe('adjust parameter', () => {

    beforeEach(() => {
      scope.adjust = true;
      scope.$digest();
    });

    it('settings the start date should go to the first of the month', () => {
      const date = '2017-05-12';
      angular.element(element.find('input')[0]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCStartDate().toISOString()).toBe('2017-05-01T00:00:00.000Z');
    });

    it('setting the end date should go to the last of the month end of the day, last hour', () => {
      const date = '2017-05-06';
      angular.element(element.find('input')[1]).val(date).triggerHandler('input');
      scope.$digest();
      expect(scope.control.getUTCEndDate().toISOString()).toBe('2017-05-31T23:59:59.000Z');
    });

  });

  describe('required parameter', () => {

    beforeEach(() => {
      scope.required = true;
      scope.$digest();
    });

    it('is required by default on the element', () => {
      let required = element.find('input')[0].getAttribute('required');
      expect(required).toBe('required');
      required = element.find('input')[1].getAttribute('required');
      expect(required).toBe('required');

      scope.required = false; // make not required
      scope.$digest();

      required = element.find('input')[0].getAttribute('required');
      expect(required).toBeNull();
      required = element.find('input')[1].getAttribute('required');
      expect(required).toBeNull();
    });

  });

});
