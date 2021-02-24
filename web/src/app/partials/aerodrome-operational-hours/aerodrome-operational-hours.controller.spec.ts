import { AerodromeOperationalHoursController } from './aerodrome-operational-hours.controller';

describe('controller AerodromeOperationalHoursController', () => {

  let aerodromeOperationalHoursController: AerodromeOperationalHoursController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aerodromeOperationalHoursController = $controller('AerodromeOperationalHoursController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aerodromeOperationalHoursController).not.toEqual(null);
  }));

});
