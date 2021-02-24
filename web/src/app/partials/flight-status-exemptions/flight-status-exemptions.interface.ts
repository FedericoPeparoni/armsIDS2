export interface IFlightStatusExemptionType {
  id?: number;
  flight_item_type: string;
  flight_item_value: string;
  aerodrome_fees_exempt: number;
  late_departure_fees_exempt: number;
  parking_fees_are_exempt: number;
  enroute_fees_are_exempt: number;
  approach_fees_exempt: number;
  late_arrival_fees_exempt: number;
  international_pax: number;
  domestic_pax: number;
  extended_hours: number;
  flight_notes: string;
}

