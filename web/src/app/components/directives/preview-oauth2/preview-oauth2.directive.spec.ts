import { PreviewOAuth2Controller } from './preview-oauth2.directive';

describe('directive preview-oauth2', () => {

  let element: angular.IAugmentedJQuery;
  let previewOAuth2Controller: PreviewOAuth2Controller;
  let httpBackend, restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
    inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService, $compile: angular.ICompileService, $rootScope: angular.IRootScopeService) => {

      $httpBackend.when('GET', (url: string): any => {
        if ('http://localhost:8080/api/system-configurations/noauth') {
          return true;
        };
      }).respond('ok');

      httpBackend = $httpBackend;
      restangular = Restangular;
      element = angular.element(`<preview-oauth2 url="/" error="error"> </preview-oauth2>`);

      $compile(element)($rootScope.$new());
      $rootScope.$digest();
      previewOAuth2Controller = (<any>element.isolateScope()).PreviewOAuth2Controller;
    });
  });

  it('should be compiled', () => {
    expect(element.html()).not.toEqual(null);
  });
});
