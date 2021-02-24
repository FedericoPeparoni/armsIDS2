/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/* This class contains methods used to convert lat/long coordinates between decimal degrees and   */
/* degrees-minutes-seconds formats. (DDD.dddd or DDD MM SS). Most of the funtionality was found   */
/* from the MIT Licenced link below.                                                              */
/*                                                                                                */
/* www.movable-type.co.uk/scripts/latlong.html                                                    */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

// interfaces
import { ICoordinates } from './convertCoordinates.interface';

export class Coordinates implements ICoordinates {

  /**
   * Separator character to be used to separate degrees, minutes, seconds
   */
  private separator: string = '';

  /**
   * Parses string representing degrees/minutes/seconds into numeric degrees.
   *
   * This is very flexible on formats, allowing signed decimal degrees, or deg-min-sec optionally
   * suffixed by compass direction (NSEW). A variety of separators are accepted (eg 3° 37′ 09″W).
   * Seconds and minutes may be omitted.
   *
   * @param   {string|number} dmsStr - Degrees or deg/min/sec in variety of formats.
   * @returns {number} Degrees as decimal number.
   *
   * @example
   *     let lat = Dms.parseDMS('51° 28′ 40.12″ N');
   *     let lon = Dms.parseDMS('000° 00′ 05.31″ W');
   *     let p1 = new LatLon(lat, lon); // 51.4778°N, 000.0015°W
   */
  public parseDMS(dmsStr: string): number {
    // check for signed decimal degrees without NSEW, if so return it directly
    if (typeof dmsStr === 'number' && isFinite(dmsStr)) { return Number(dmsStr); };

    // find the number of spaces in the string
    const numberOfSpaces = (dmsStr.match(/ /g) || []).length;

    // if only one space, the user has entered
    // an invalid format and we remove it for the
    // the subsequent algorithms
    if (numberOfSpaces === 1) {
      dmsStr = dmsStr.replace(' ', '');
    }

    // if DMS without spaces, add them to the correct
    // indexes to make DDD MM SS. Currently, only 
    // longitute is allowed without spaces.
    if (/^([0-9]{7})/.test(dmsStr)) {
      // split all chars
      let splitDMS = dmsStr.split('');
      // insert spaces at 3 and 6
      splitDMS.splice(3, 0, ' ');
      splitDMS.splice(6, 0, ' ');
      // join chars back to string
      dmsStr = splitDMS.join('');
    }

    // strip off any sign or compass dir'n & split out separate d/m/s
    let dms: any = String(dmsStr).trim().replace(/^-/, '').replace(/[NSEW]$/i, '').split(/[^0-9.,]+/);
    if (dms[dms.length - 1] === '') { dms.splice(dms.length - 1); };  // from trailing symbol

    if (dms === '') { return NaN; };

    // and convert to decimal degrees
    let deg;
    switch (dms.length) {
      case 3:  // interpret 3-part result as d/m/s
        deg = dms[0] / 1 + dms[1] / 60 + dms[2] / 3600;
        break;
      case 2:  // interpret 2-part result as d/m
        deg = dms[0] / 1 + dms[1] / 60;
        break;
      case 1:  // just d (possibly decimal) or non-separated dddmmss
        deg = dms[0];
        break;
      default:
        return NaN;
    }
    if (/^-|[WS]$/i.test(dmsStr.trim())) { deg = -deg; }; // take '-', west and south as -ve

    deg = Number(deg);
    return deg.toString();
  }

  /**
   * Converts numeric degrees to deg/min/sec latitude (2-digit degrees, suffixed with N/S).
   *
   * @param   {number} deg - Degrees to be formatted as specified.
   * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
   * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
   * @returns {string} Degrees formatted as deg/min/secs according to specified format.
   */
  public toLat(deg: number, format: string, dp: number): string {
    this.separator = ' '; // we separate DD MM SS with a space
    let lat = this.toDMS(deg, format, dp);
    this.separator = ''; // revert separator
    return lat === null ? '–' : lat.slice(1) + this.separator + (deg < 0 ? 'S' : 'N');  // knock off initial '0' for lat!
  }

  /**
   * Convert numeric degrees to deg/min/sec longitude (3-digit degrees, suffixed with E/W)
   *
   * @param   {number} deg - Degrees to be formatted as specified.
   * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
   * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
   * @returns {string} Degrees formatted as deg/min/secs according to specified format.
   */
  public toLon(deg: number, format: string, dp: number): string {
    this.separator = ' '; // we separate DD MM SS with a space
    let lon = this.toDMS(deg, format, dp);
    this.separator = ''; // revert separator
    return lon === null ? '–' : lon + this.separator + (deg < 0 ? 'W' : 'E');
  }

  /**
   * Converts decimal degrees to deg/min/sec format
   *
   * @private
   * @param   {number} deg - Degrees to be formatted as specified.
   * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
   * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
   * @returns {string} Degrees formatted as deg/min/secs according to specified format.
   */
  private toDMS(deg: number, format: string, dp: number): string {
    if (isNaN(deg)) { return null; };  // give up here if we can't make a number from deg

    // default values
    if (format === undefined) { format = 'dms'; };
    if (dp === undefined) {
      switch (format) {
        case 'd': case 'deg': dp = 4; break;
        case 'dm': case 'deg+min': dp = 2; break;
        case 'dms': case 'deg+min+sec': dp = 0; break;
        default: format = 'dms'; dp = 0;  // be forgiving on invalid format
      }
    }

    deg = Math.abs(deg);  // (unsigned result ready for appending compass dir'n)

    let dms, d, m, s;
    switch (format) {
      default: // invalid format spec!
      case 'd': case 'deg':
        d = deg.toFixed(dp);                         // round/right-pad degrees
        if (d < 100) { d = '0' + d; };               // left-pad with leading zeros (note may include decimals)
        if (d < 10) { d = '0' + d; };
        dms = d + '°';
        break;
      case 'dm': case 'deg+min':
        d = Math.floor(deg);                         // get component deg
        m = ((deg * 60) % 60).toFixed(dp);           // get component min & round/right-pad
        if (m === 60) { m = 0; d++; }                // check for rounding up
        d = ('000' + d).slice(-3);                   // left-pad with leading zeros
        if (m < 10) { m = '0' + m; };                // left-pad with leading zeros (note may include decimals)
        dms = d + '°' + this.separator + m + '′';
        break;
      case 'dms': case 'deg+min+sec':
        d = Math.floor(deg);                         // get component deg
        m = Math.floor((deg * 3600) / 60) % 60;      // get component min
        s = (deg * 3600 % 60).toFixed(dp);           // get component sec & round/right-pad
        if (Math.round(s) === 60) {                 // check for rounding up
          s = (0).toFixed(dp); m++;
        }
        if (m === 60) { m = 0; d++; }                // check for rounding up
        d = ('000' + d).slice(-3);                   // left-pad with leading zeros
        m = ('00' + m).slice(-2);                    // left-pad with leading zeros
        if (s < 10) { s = '0' + s; };                // left-pad with leading zeros (note may include decimals)
        // 90159 join with or without spaces, depending on lat/lon (this.separator)
        dms = d + this.separator + m + this.separator + s;
        break;
    }

    return dms;
  }
}
