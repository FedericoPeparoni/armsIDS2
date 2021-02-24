import { MtowController } from './mtow.controller';

describe('controller MtowController', () => {

  let mtowController: MtowController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    mtowController = $controller('MtowController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(mtowController).not.toEqual(null);
  }));

});
