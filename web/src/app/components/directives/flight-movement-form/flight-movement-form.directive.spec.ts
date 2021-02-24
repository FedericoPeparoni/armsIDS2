describe('directive flight-movement-form', () => {
  let element: any;
  let scope;
  let template;
  let httpBackend;
  let restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($httpBackend: angular.IHttpBackendService, $compile: angular.ICompileService, Restangular: restangular.IService, $rootScope: angular.IRootScopeService) => {
    $httpBackend.when('GET', (url: string): any => {
      if ('http://localhost:8080/api/system-configurations/noauth') {
        return true;
      };
    }).respond('ok');

    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    restangular = Restangular;

    element = angular.element(`<flight-movement-form flight-movement="editable" manual="manual"><!-- Flight Movement Component --></flight-movement-form>`);

    scope.manual = [];
    scope.editable = {};

    httpBackend
      .expect('GET', `${restangular.configuration.baseUrl}/accounts/allMinimal?active=true`)
      .respond(200, {});

    template = $compile(element)(scope);
    scope.$apply();

    httpBackend.flush();
  }));

  it('should be defined', inject(() => {
    expect(template[0]).toBeDefined();
  }));

});
