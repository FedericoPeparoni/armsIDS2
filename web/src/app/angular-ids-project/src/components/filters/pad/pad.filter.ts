/** @ngInject */
export function pad() {

  /**
   * Left pads a string to meet a certain length
   *
   * @param  {string}         val             The incoming value to check against
   * @param  {number}         expectedLength  The expected length
   * @param  {string|number}  padCharacter    By default it will pad with 0 but can be padded with string|number
   * @return {string}                         Returns a string with a certain length
   */
  return function (val: string = '', expectedLength: number = 0, padCharacter: string | number = 0) {
    val = '' + val; // although we expect it to be a string, it might come in as a number
    while (val.length < expectedLength) {
      val = `${padCharacter}${val}`;
    }
    return val;
  };

}
