// note this is not an angular service, it's just extended by the services that use enumerators
export abstract class EnumService {

  /**
   * Takes an Enum Object and returns a new object with every other value
   * @param enumObject
   *
   * It skips every other because of the way typescript builds enum objects
   *
   * @returns {Array}
   */
  list(enumObject: any): Array<string> {
    var list: Array<string> = [];
    Object.keys(enumObject).filter((value: string, index: number, array: Array<any>) => {
      if (index % 2 === 0) {
        list[value] = array[index + 1];
      }
    });
    return list;
  }

}
