// interface
import { IConvertItemTypeProperties } from '../convert-item-type.interface';

// service
import { ConvertItemTypeService, ConvertItemType } from './convert-item-type.service';

describe('service ConvertItemTypeService', () => {

  beforeEach(angular.mock.module('armsWeb'));

  it('should be registered', inject((convertItemTypeService: ConvertItemTypeService) => {
    expect(convertItemTypeService).not.toEqual(null);
  }));

  describe('itemTo', () => {

    it('should convert ConvertItemType.DATE to ConvertItemType.STRING', inject((convertItemTypeService: ConvertItemTypeService) => {

        let properties: IConvertItemTypeProperties = {
          source: ConvertItemType.DATE,
          target: ConvertItemType.STRING,
          format: 'YYMMDD'
        };

        let result: string = convertItemTypeService.itemTo(new Date(2001, 1, 24), properties);

        expect(result).toEqual('010224');
    }));

    it('should convert ConvertItemType.STRING to ConvertItemType.DATE', inject((convertItemTypeService: ConvertItemTypeService) => {

        let properties: IConvertItemTypeProperties = {
          source: ConvertItemType.STRING,
          target: ConvertItemType.DATE,
          format: 'YYMMDD'
        };

        let result: Date = convertItemTypeService.itemTo('010224', properties);

        expect(result).toEqual(new Date(2001, 1, 24));
    }));

    it('should format ConvertItemType.DATE values in UTC when ConvertItemTypeProperties.utc TRUE', inject((convertItemTypeService: ConvertItemTypeService) => {

      let properties: IConvertItemTypeProperties = {
        source: ConvertItemType.DATE,
        target: ConvertItemType.STRING,
        format: 'YYMMDD',
        utc: true
      };

      let result: string = convertItemTypeService.itemTo(new Date(Date.UTC(2001, 1, 24)), properties);

      expect(result).toEqual('010224');
    }));

    it('should set ConvertItemType.DATE values in UTC when ConvertItemTypeProperties.utc TRUE', inject((convertItemTypeService: ConvertItemTypeService) => {

      let properties: IConvertItemTypeProperties = {
        source: ConvertItemType.STRING,
        target: ConvertItemType.DATE,
        format: 'YYMMDD',
        utc: true
      };

      let result: Date = convertItemTypeService.itemTo('010224', properties);

      expect(result).toEqual(new Date(Date.UTC(2001, 1, 24)));

    }));
  });
});
