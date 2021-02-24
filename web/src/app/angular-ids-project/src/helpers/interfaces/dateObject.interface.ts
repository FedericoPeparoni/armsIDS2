// Date Object in Javascript
export interface IDateObject {
  getTime: Function;
  getTimezoneOffset: Function;
  getMonth: Function;
  getFullYear: Function;
  setTime: Function;
  toISOString: Function;
  getDate: () => number;
  getUTCMonth: () => number;
  getUTCDate: () => number;
  getUTCFullYear: () => number;
  setHours: Function;
  setMinutes: Function;
  setSeconds: Function;
  getHours: Function;
  getMinutes: Function;
  setUTCHours: Function;
}
