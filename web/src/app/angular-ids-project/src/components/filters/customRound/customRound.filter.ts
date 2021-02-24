// roungs a number using system configs

/** @ngInject */
export function customRound(): (val: number, direction: string) => number {

  return (num: number, direction: string) => {
    if (direction === 'Up') {
      return Math.ceil(num);
    } else if (direction === 'Down') {
      return Math.floor(num);
    } else {
      return num;
    }
  };
}
