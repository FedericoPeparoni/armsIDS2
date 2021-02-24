import { DownloadOAuth2Controller } from './download-oauth2.directive';

describe('directive download-oauth2', () => {

  let element: angular.IAugmentedJQuery;
  let downloadOAuth2Controller: DownloadOAuth2Controller;
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
      element = angular.element(`<download-oauth2 url="/" error="error"> </download-oauth2>`);

      $compile(element)($rootScope.$new());
      $rootScope.$digest();
      downloadOAuth2Controller = (<any>element.isolateScope()).DownloadOAuth2Controller;
    });
  });

  it('should be compiled', () => {
    expect(element.html()).not.toEqual(null);
  });
});



