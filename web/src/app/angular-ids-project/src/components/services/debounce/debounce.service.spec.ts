describe('Service: debounceService', () => {
  beforeEach(angular.mock.module('armsWeb'));

  let $rootScope, $q, $timeout, service, foo, bar;

  beforeEach(() => {
    inject(($httpBackend: angular.IHttpBackendService, $injector: ng.auto.IInjectorService) => {
      $httpBackend.when('GET', (url: string): any => {
        if ('http://localhost:8080/api/system-configurations/noauth') {
          return true;
        };
      }).respond('ok');

      $rootScope = $injector.get('$rootScope');
      $q = $injector.get('$q');
      $timeout = $injector.get('$timeout');
      service = $injector.get('debounceService');
      foo = {
        setBar: (value: any): void => {
          bar = value;
        }
      };
    });
  });

  it('should execute function passed to debounce', (): void => {
    let spy = spyOn(foo, 'setBar');

    service.debounce(spy());

    expect(spy).toHaveBeenCalled();
  });

  it('should execute function passed with time delay', (): void => {
    let spy = spyOn(foo, 'setBar');

    service.debounce(spy('test'), 500, false);

    $timeout.flush(500);

    expect(spy).toHaveBeenCalled();
  });
});
