import { IDateObject } from '../../../helpers/interfaces/dateObject.interface';

export class HelperService {

  /** @ngInject */

  /**
   * Takes the datepicker date/time (which would be the users local time) and converts and returns the date object in GMT
   * It throws errors without converting the getTime() to string?
   */
  static convertLocalTimeToUTC(dateObj: IDateObject): IDateObject {
    let d = parseInt((dateObj.getTime() / 1000) + '', 10) - (dateObj.getTimezoneOffset() * 60); // returns date object
    return new Date(d * 1000);
  }

  /**
   * Random number, both min/max included
   *
   * @param min
   * @param max
   * @returns {number}
   */
  static getRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1) + min);
  }

  /**
   * Takes an date object, gets the ISO string from it but strips off the milliseconds
   * @param dateObj
   * @returns {*}
   */
  static dateToISOString(dateObj: IDateObject): string {
    let str = dateObj.toISOString();
    return str.replace(/(.+)\.\d{3}\Z$/, '$1Z');
  }

  /**
   * Inserting date objects returns it into a date ISO style string (without milliseconds) inside an object
   *
   * @param date JS Date Object
   * @returns string
   */
  static getStartEndStrings(date: IDateObject): string {
    date = this.convertLocalTimeToUTC(date);
    return this.dateToISOString(date);
  }

  /**
   * Generates random characters/string
   * @param num
   * @returns {string}
   */
  static getRandomChar(num: number = 1): string {
    let text = '';
    let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (let i = 0; i < num; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
  }

  /**
   * Returns a random hex color
   * @returns {string}
   */
  static generateRandomHexColor(): string {
    return ('000000' + Math.random().toString(16).slice(2, 8).toUpperCase()).slice(-6); // http://www.paulirish.com/2009/random-hex-color-code-snippets/
  }

  /**
   * Returns result of banker's rounding. Even rounds up and odd rounds down.
   * @param num number to round
   * @param decimals decimals places to round (default 0)
   * @returns {number}
   */
  static evenRound(num: number, decimals: number = 0): number {
    if (num !== null && !isNaN(num)) {
      var m = Math.pow(10, decimals);
      var n = +(decimals ? num * m : num).toFixed(8); // avoid rounding errors
      var i = Math.floor(n), f = n - i;
      var e = 1e-8; // allow for rounding errors in f
      var r = (f > 0.5 - e && f < 0.5 + e) ?
                  ((i % 2 === 0) ? i : i + 1) : Math.round(n);
      return decimals ? r / m : r;
    }
  }

  /**
   * Returns object property in comma seperated string form from array of objects.
   * @param array array of objects to loop through
   * @param property property in object to comma seperate
   * @param max max comma seperated values before eclipsed
   * @returns {string}
   */
  static commaSeperate(array: Array<any>, property: string, max?: number): string {

    let length: number = array.length;
    max = max || length;

    let result: string = '';
    for (let i: number = 0; i < length; i++) {
      result += array[i][property];
      if (max < length && i >= max - 1) {
        result += ', ...';
        break;
      } else if (i !== length - 1) {
        result += ', ';
      }
    }

    return result;
  }

  /**
   * Copies the contents of an element to the user's clipboard.
   * @param element element to copy
   */
  static copyToClipboard(element: HTMLElement | null): void {
    if (element !== null) {

      let hide: boolean = false;
      if (element.classList.contains('ng-hide')) {
        element.classList.remove('ng-hide');
        hide = true;
      }

      let range: Range = document.createRange();
      range.selectNodeContents(element);
      let selection: Selection = window.getSelection();
      selection.removeAllRanges();
      selection.addRange(range);
      document.execCommand('Copy');

      if (hide) {
        element.classList.add('ng-hide');
      }
    }
  }

  /**
   * Check if valid JSON before setting value
   * Useful for handling case where translation is not available
   * Returns the Object corresponding to the given JSON text
   * @param value a JSON string
   * @returns {object}
   */
  static valueToJSON(value: string): object {
    try {
      return JSON.parse(value);
    } catch (e) {
      return null;
    }
  }

  /**
   * Get a url param value by url param name
   * @param paramName string param
   * @param defaultvalue any
   * @returns {any}
   */
  static getUrlParamValueByName(paramName: string, defaultvalue: any = false): any {
    let urlparameter = defaultvalue;

    const getUrlVars = (): object => {
        let vars = {};
        window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, (m: string, key: string, value: string): string => {
            return vars[key] = value;
        });
        return vars;
    };

    if (window.location.href.indexOf(paramName) > - 1) {
        urlparameter = getUrlVars()[paramName];
    }

    return urlparameter;
  }
}
