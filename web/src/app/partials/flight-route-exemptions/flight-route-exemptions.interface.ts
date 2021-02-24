export interface IFlightRouteExemptionType {
  id?: number;
  departure_aerodrome: string;
  destination_aerodrome: string;
  exemption_in_either_direction: boolean;
  enroute_fees_are_exempt: number;
  approach_fees_are_exempt: number;
  aerodrome_fees_are_exempt: number;
  late_arrival_fees_are_exempt: number;
  late_departure_fees_are_exempt: number;
  parking_fees_are_exempt: number;
  international_pax: number;
  domestic_pax: number;
  extended_hours: number;
  flight_notes: string;
  exempt_route_floor: number;
  exempt_route_ceiling: number;
}
