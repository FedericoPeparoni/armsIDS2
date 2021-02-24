import { UtilitiesSchedulesController } from './utilities-schedules.controller';

describe('controller UtilitiesSchedulesController', () => {

  let utilitiesSchedulesController: UtilitiesSchedulesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    utilitiesSchedulesController = $controller('UtilitiesSchedulesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(utilitiesSchedulesController).not.toEqual(null);
  }));

});
