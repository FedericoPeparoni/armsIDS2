export interface ISystemSummaryValue {
    total: number;
    val: number;
    percent: number;
    name: string;
    date: string;
}

export interface ISystemSummaryFlightCategiriesValue {
    category_name: string;
    flight_movement_vo: ISystemSummaryValue
}

export interface ISystemSummary {
    flight_movement_aircraft_type: ISystemSummaryValue;
    flight_movement_all: ISystemSummaryValue;
    flight_movement_blacklisted_account: ISystemSummaryValue;
    flight_movement_blacklisted_movement: ISystemSummaryValue;
    flight_movement_categories: ISystemSummaryFlightCategiriesValue;
    flight_movement_domestic_active_account: ISystemSummaryValue;
    flight_movement_international_active_account: ISystemSummaryValue;
    flight_movement_latest: ISystemSummaryValue;
    flight_movement_inside: ISystemSummaryValue;
    flight_movement_outside: ISystemSummaryValue;
    flight_movement_parking_time_domestic: ISystemSummaryValue;
    flight_movement_parking_time_internationa_arrivals: ISystemSummaryValue;
    flight_movement_parking_time_total: ISystemSummaryValue;
    flight_movement_rejected: ISystemSummaryValue;
    flight_movement_unknown_aircraft_type: ISystemSummaryValue;
    outstanding_bill: ISystemSummaryValue;
    overdue_bill: ISystemSummaryValue;
}

export interface ISystemSummaryScope {
    data: ISystemSummary;
    list: Array<ISystemSummaryValue>;
    shouldShowCharge: Function;
    formatName: Function;
}
