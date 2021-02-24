import { ICurrency } from '../currency-management/currency-management.interface';

export interface IAerodromeCategory {
  id?: number;
  category_name: string;
  international_passenger_fee_adult: number;
  international_passenger_fee_child: number;
  domestic_passenger_fee_adult: number;
  domestic_passenger_fee_child: number;
  domestic_fees_currency: ICurrency;
  international_fees_currency: ICurrency;
}
