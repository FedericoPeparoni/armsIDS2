export interface IFlightMovementCategory {
  id: number;
  name: string;
  sort_order: number;
  short_name: string;
  enroute_currency_calculated: number;
  enroute_currency_invoice: number;
}

export interface IFlightMovementCategorySpring {
  content: Array<IFlightMovementCategory>;
}
