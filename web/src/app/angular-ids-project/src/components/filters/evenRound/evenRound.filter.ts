import { HelperService } from '../../services/helpers/helpers.service';

// roungs a number using even rounding (banker's rounding)

/** @ngInject */
export function evenRound(): (val: number, decimalPlaces: number) => number {

  return (num: number, decimals: number = 0) => {
    if (num !== null && !isNaN(num)) {
      return HelperService.evenRound(num, decimals);
    }
    return num;
  };
}
