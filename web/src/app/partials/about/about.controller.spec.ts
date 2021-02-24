import { AboutController } from './about.controller';

describe('controller about', () => {

  let aboutController: AboutController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aboutController = $controller('AboutController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aboutController).not.toEqual(null);
  }));

});

