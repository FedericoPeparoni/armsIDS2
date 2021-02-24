// enumerations
import { ConvertItemType } from './service/convert-item-type.service';

export interface IConvertItemTypeProperties {
  source: ConvertItemType;
  target: ConvertItemType;
  format?: any;
  utc?: boolean;
}