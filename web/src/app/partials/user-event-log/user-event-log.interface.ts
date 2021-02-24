export interface IEvent {
  event_type: string;
  record_primary_key: string;
  unique_record_id: string;
}

export interface IUserEventLog extends IEvent {
  id?: number;
  date_time: string;
  ip_address: string;
  modified_column_names_values: string;
  user_name: string;
}
