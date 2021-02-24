export interface ISCFlightSearch {
    id?: number;
    account: string;
    item18_reg_num: string;
    flight_id: string;
    date_of_flight: string;
    status: string;
    dep_time: string;
    dep_ad: string;
    dest_ad: string;
    total_charges_usd: number;
    amount_prepaid: number;
    flight_notes: string;
  }