import { AMHSConnectionController } from './amhs-connection.controller';

describe('controller AMHSConnectionController', () => {

  let amhsConnectionController: AMHSConnectionController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    amhsConnectionController = $controller('AMHSConnectionController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(amhsConnectionController).not.toEqual(null);
  }));

});
