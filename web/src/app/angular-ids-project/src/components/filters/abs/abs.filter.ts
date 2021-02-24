// converts a number to its absolute positive

/** @ngInject */
export function abs(): (val: number) => number {

  return (val: number) => {
    if (val !== null && !isNaN(val)) {
      return Math.abs(val);
    }
    return val;
  };
}
