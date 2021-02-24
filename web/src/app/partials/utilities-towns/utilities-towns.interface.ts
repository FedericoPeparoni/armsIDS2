import { ISchedule } from '../utilities-schedules/utilities-schedules.interface';
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { ISort } from '../../angular-ids-project/src/components/tableSort/tableSort.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface ITown {
    id: number;
    town_or_village_name: string;
    water_utility_schedule: ISchedule;
    residential_electricity_utility_schedule: ISchedule;
    commercial_electricity_utility_schedule: ISchedule;
}

export interface ITownsScope extends ICRUDScope {
    initTableSort: ISort[];
    schedules: ITownsSpring;
    waterSchedules: ISchedule[];
    commElectricitySchedules: ISchedule[];
    resElectricitySchedules: ISchedule[];
}

export interface ITownsSpring extends ISpringPageableParams {
  content: Array<ISchedule>;
}
