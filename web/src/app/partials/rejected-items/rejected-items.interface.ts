import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';

export interface IRejectedItem {
  id?: number;
  filter_by: string;
  filter_by_record_type: string;
  filter_by_date: Date;
  filter_by_status: string;
  filter_by_originator: string;
  filter_by_file_name: string;
  record_type: string;
  rejected_date_time: string;
  error_message: string;
  error_details: string;
  rejected_reason: string;
  raw_text: string;
  header: string;
  json_text: string;
  originator: string;
  status: string;
  file_name: string
}

export interface IRejectedItemScope extends ICRUDScope {
  rejectedItem: Array<IRejectedItem>
  item: IRejectedItem;
  displayed_data: Array<IDisplayedObj>;
  result: any;
  userInput: Array<string>;
  button: boolean;
  data: Array<string>;
  id: number;
  getRejectedItemType: (item: string) => string;
  setFilterModel: (rejectedItem: IRejectedItem) => void;
  getSortQueryString: () => string;
  filter_by_file_name: string;
  filter_by_originator: string;
  filter_by_record_type: string;
  filter_by_status: string;
  search: string;
  error: object;
  merge_upload: boolean;
}

export interface IDisplayedObj {
  header: string;
  data: string | number;
  type: string;
  isMultiLineField: boolean;
}
