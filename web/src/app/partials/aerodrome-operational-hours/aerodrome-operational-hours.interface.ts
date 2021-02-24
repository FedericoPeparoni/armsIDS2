import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IAerodrome } from '../aerodromes/aerodromes.interface';

export interface IAerodromeOperationalHours {
    id?: number;
    aerodrome: IAerodrome;
    operational_hours_monday: string;
    operational_hours_tuesday: string;
    operational_hours_wednesday: string;
    operational_hours_thursday: string;
    operational_hours_friday: string;
    operational_hours_saturday: string;
    operational_hours_sunday: string;
    operational_hours_holidays1: string;
    operational_hours_holidays2: string;
    holiday_dates_holidays1: string;
    holiday_dates_holidays2: string;
}

export interface IAerodromeOperationalHoursScope extends ng.IScope {
    refresh: Function;
    reset: Function;
    create: Function;
    update: Function;
    delete: Function;
    edit: Function;
    dateValidate: Function;
    timeValidate: Function;
    copyDataFromAerodrome: Function;
    copyFromAerodromeList: Array<IAerodrome>;
    copyFromAerodrome: IAerodrome;
    list: Array<IAerodromeOperationalHours>;
    editable: IAerodromeOperationalHours;
    search: string;
    pagination: ISpringPageableParams;
    getSortQueryString: () => string;
    form: any;
    showDateErrorMessage: Array<boolean>;
    showTimeErrorMessage: Array<boolean>;
    fromTimeIsAfterEndHours: Array<boolean>;
    dateIsAlreadyExist: Array<boolean>;
    timeIsAlreadyExist: Array<boolean>;
    timeFormatErrorMessage: string;
    timeIsAlreadyExistErrorMessage: string;
    timeStartEndErrorMessage: string;
    dateFormatErrorMessage: string;
    dateIsAlreadyExistErrorMessage: string;
    $watch: any;
}
