// interfaces
import { IConvertItemTypeProperties } from '../convert-item-type.interface';

export enum ConvertItemType {
  ARGUMENTS = <any>'[object Arguments]',
  ARRAY = <any>'[object Array]',
  BOOLEAN = <any>'[object Boolean]',
  DATE = <any>'[object Date]',
  ERROR = <any>'[object Error]',
  FUNCTION = <any>'[object Function]',
  JSON = <any>'[object JSON]',
  MATH = <any>'[object Math]',
  NULL = <any>'[object Null]',
  NUMBER = <any>'[object Number]',
  OBJECT = <any>'[object Object]',
  REGEXP = <any>'[object RegExp]',
  STRING = <any>'[object String]',
  UNDEFINED = <any>'[object Undefined]'
}

export class ConvertItemTypeService {

  /**
   * Accepts an item and converts it to a specific type based
   * on properties supplied.
   * 
   * Currently only DATE, NUMBER, and STRING convert item types
   * are implemented.
   * 
   * @param item item to convert
   * @param properties convert item properties
   */
  public itemTo(item: any, properties: IConvertItemTypeProperties): any {

    // get item type value
    let type: ConvertItemType = Object.prototype.toString.call(item);

    // validate that item exists and 
    // source type equals item type
    if (!item || properties.source !== type) {
      return item;
    }

    // send to appropriate handler based on target type
    if (properties.target === ConvertItemType.DATE) {
      return this.itemToDate(item, type, properties.format, properties.utc);
    } else if (properties.source === ConvertItemType.NUMBER) {
      return this.itemToNumber(item, type, properties.format, properties.utc);
    } else if (properties.target === ConvertItemType.STRING) {
      return this.itemToString(item, type, properties.format, properties.utc);
    }

    // if unhandled, return supplied value
    return item;
  }

  /**
   * Accepts an item and converts it to a Date based on an
   * optional format.
   * 
   * @param item item to convert
   * @param type item type
   * @param format optional item format (default: null)
   * @param utc optional utc mode if applicable (default: localtime)
   */
  private itemToDate(item: any, type: ConvertItemType, format?: any, utc?: boolean): Date {
    if (type === ConvertItemType.STRING && format && utc) {
      return moment.utc(item, format).toDate();
    } else if (type === ConvertItemType.STRING && format) {
      return moment(item, format).toDate();
    } else if ((type === ConvertItemType.STRING || type === ConvertItemType.NUMBER) && utc) {
      return moment.utc(item).toDate();
    }
    return moment(item).toDate();
  }

  /**
   * Accepts an item and converts it to a Number.
   * 
   * @param item item to convert
   * @param type item type
   * @param format optional item format (default: null)
   * @param utc optional utc mode if applicable (default: localtime)
   */
  private itemToNumber(item: any, type: ConvertItemType, format?: any, utc?: boolean): Number {
    if (type === ConvertItemType.DATE && utc) {
      return moment(item).utc().unix();
    } else if (type === ConvertItemType.DATE) {
      return moment(item).unix();
    } else if (type === ConvertItemType.STRING && format) {
      return parseInt(item, format);
    }
    return parseInt(item, 0);
  }

  /**
   * Accepts an item and converts it to a string.
   * 
   * @param item item to convert
   * @param type item type
   * @param format optional string format (default: null)
   * @param utc optional utc mode if applicable (default: localtime)
   */
  private itemToString(item: any, type: ConvertItemType, format?: any, utc?: boolean): string {
    if (type === ConvertItemType.DATE && format && utc) {
      return moment(item).utc().format(format);
    } else if (type === ConvertItemType.DATE && format) {
      return moment(item).format(format);
    } else if (type === ConvertItemType.DATE && utc) {
      return moment(item).utc().toISOString();
    } else if (type === ConvertItemType.DATE) {
      return moment(item).toISOString();
    } else if (type === ConvertItemType.NUMBER && format) {
      return item.toString(format);
    }
    return item.toString();
  }
}
