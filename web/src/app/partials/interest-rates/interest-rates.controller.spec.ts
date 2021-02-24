import { InterestRatesController } from './interest-rates.controller';

describe('controller InterestRatesController', () => {

  let interestRatesController: InterestRatesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    interestRatesController = $controller('InterestRatesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(interestRatesController).not.toEqual(null);
  }));

});
