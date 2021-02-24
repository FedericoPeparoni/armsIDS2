// services
import { FlightTypeService } from './flight-types.service';

describe('service FlightTypeService', () => {

  beforeEach(angular.mock.module('armsWeb'));

  it('should be registered', inject((flightTypeService: FlightTypeService) => {
    expect(flightTypeService).not.toEqual(null);
  }));

  describe('list method', () => {

    it('should be registered', inject((flightTypeService: FlightTypeService) => {
      expect(flightTypeService.list).not.toEqual(null);
    }));

    it('should contain 8 items', inject((flightTypeService: FlightTypeService) => {
      expect(Object.keys(flightTypeService.list()).length).toEqual(8);
    }));

  });

});
