import { ITown } from '../utilities-towns/utilities-towns.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';

export interface IRange {
  id: number;
  schedule_id: number
  range_top_end: number;
  unit_price: number;
}

export interface ISchedule {
  schedule_id: number;
  schedule_type: string;
  minimum_charge: number;
  utilities_range_bracket: Array<IRange>;
  utilities_water_towns_and_village: ITown[];
  residential_electricity_utility_schedule: ITown[];
  commercial_electricity_utility_schedule: ITown[]
}

export interface ISchedulesScope extends ICRUDScope {
  scheduleTypes: Array<IStaticType>;
  editRange: Function;
  resetRange: Function;
  updateRange: Function;
  deleteRange: Function;
  createRange: Function;
  deleteEditableRangeBracket: Function;
  updateEditableRangeBracket: Function;
  getScheduleTypeName (scheduleValue: string): string;
  editableRangeBracket: IRange;
  ranges: Array<IRange>;
  filter: string;
}

export interface IScheduleSpring {
  content: Array<ISchedule>;
}