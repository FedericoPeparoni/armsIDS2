// keeps UTC time received from server, note: better than built in angular UTC filter

/** @ngInject */
export function utc() {

  return function (val: string) {
    if (val !== null && val.length > 16) {
      return val.substring(0, 16);
    }
    return val;
  };

}
