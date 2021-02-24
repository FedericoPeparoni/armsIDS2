import { HelpController } from './help.controller';

describe('controller HelpController', () => {

  let helpController: HelpController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    helpController = $controller('HelpController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(helpController).not.toEqual(null);
  }));

});
