import {SystemConfigurationController} from './system-configuration.controller';

describe('controller SystemConfigurationController', () => {

  let systemConfigurationController: SystemConfigurationController;
  let scope;

  let data = [
    {
      id: 46,
      item_name: 'Banking information',
      item_class: {
        id: 7,
        name: 'ansp'
      },
      data_type: {
        id: 2,
        name: 'string'
      },
      units: null,
      range: null,
      default_value: null,
      current_value: 'dfsdfs',
      created_by: 'system',
      created_at: '2016-12-17T12:09:07Z',
      updated_by: 'admin',
      updated_at: '2016-12-24T00:55:51Z',
      client_storage_forbidden: false,
      system_validation_type: null,
      display_units: 'km'
    },
    {
      id: 42,
      item_name: 'Calculate circular flight distance based on speed and duration',
      item_class: {
        id: 11,
        name: 'crossing'
      },
      data_type: {
        id: 3,
        name: 'boolean'
      },
      units: null,
      range: 't/f',
      default_value: 'f',
      current_value: null,
      created_by: 'system',
      created_at: '2016-12-17T12:09:07Z',
      updated_by: 'admin',
      updated_at: '2016-12-24T00:55:47Z',
      client_storage_forbidden: false,
      system_validation_type: null,
      display_units: 'km'
    }
  ];

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    systemConfigurationController = $controller('SystemConfigurationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(systemConfigurationController).not.toEqual(null);
  }));

  // shouldBeFilteredCategory(textFilter: string, list: Array<ISystemConfiguration>, id: number, firstInCategory: boolean) {
  describe('shouldBeFilteredCategory method', () => {

    it('should be registered and a static method', inject(() => {
      expect(SystemConfigurationController.shouldBeFilteredCategory).not.toEqual(null);
    }));

    it('should return true when text filter matches item name, and item class id matches id, and it is the first in category', () => {

      let textFilter = 'info';
      let id = 7;
      let firstInCategory = true;

      expect(SystemConfigurationController.shouldBeFilteredCategory(textFilter, data, id, firstInCategory)).toBeTruthy();

    });

    it('should return false when text filter matches item name, and item class id matches id, and it is not the first in category', () => {

      let textFilter = 'info';
      let id = 7;
      let firstInCategory = false;

      expect(SystemConfigurationController.shouldBeFilteredCategory(textFilter, data, id, firstInCategory)).not.toBeTruthy();

    });

    it('should return false when text filter matches item name, and item class id does not match id, and it is the first in category', () => {

      let textFilter = 'info';
      let id = 2;
      let firstInCategory = true;

      expect(SystemConfigurationController.shouldBeFilteredCategory(textFilter, data, id, firstInCategory)).not.toBeTruthy();

    });

    it('should return false when text filter does not match item name, and item class id matches id, and it is the first in category', () => {

      let textFilter = 'fail';
      let id = 7;
      let firstInCategory = true;

      expect(SystemConfigurationController.shouldBeFilteredCategory(textFilter, data, id, firstInCategory)).not.toBeTruthy();

    });

  });

  // shouldBeFilteredItem(textFilter: string, itemName: string) {
  describe('shouldBeFilteredItem method', () => {

    it('should be registered and a static method', inject(() => {
      expect(SystemConfigurationController.shouldBeFilteredItem).not.toEqual(null);
    }));

    it('should return false when text filter does not contain an item name match', inject(() => {
      let textFilter = 'fail';
      expect(SystemConfigurationController.shouldBeFilteredItem(textFilter, data[0].item_name)).not.toBeTruthy();
    }));

    it('should return true when text filter contains an item name match', inject(() => {
      let textFilter = 'info';
      expect(SystemConfigurationController.shouldBeFilteredItem(textFilter, data[0].item_name)).toBeTruthy();
    }));

  });

});
