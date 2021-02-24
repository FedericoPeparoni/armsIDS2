import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IAerodrome, IAerodromeServiceType } from '../aerodromes/aerodromes.interface';

export interface IServiceOutages {
    id?: number;
    aerodrome: string;
    aerodrome_service_type: string;
    start_date_time: string;
    end_date_time: string;
    approach_discount_type: string;
    approach_discount_amount: number;
    aerodrome_discount_type: string;
    aerodrome_discount_amount: number;
    flight_notes: string;
}

export interface IAerodromeServiceTypeMap {
    id: {
        aerodrome: IAerodrome;
        aerodrome_service_type: IAerodromeServiceType;
    }
}

export interface IServiceOutagesScope {
    refresh: Function;
    create: Function;
    update: Function;
    delete: Function;
    edit: Function;
    reset: Function;
    clearTime: Function;
    refreshAerodromeServiceTypeMap: Function;
    refreshAerodromeServiceOutages: Function;
    editable: IServiceOutages;
    search: string;
    pagination: ISpringPageableParams;
    getSortQueryString: () => string;
    customDate: string;
    aerodromeWithServices: IAerodrome[];
    allServiceTypes: IAerodromeServiceType[];
    aerodromeServiceTypes: IAerodromeServiceType[];
    getAerodromeServiceTypes: Function;
    setDefaultData: Function;
    editOutages: Function;
    showOutages: Function;
    list: IServiceOutages[];
    outages: IServiceOutages[];
    aerodromeServiceTypeMap: any;
    serviceType: string;
    aerodromeStatus: string;
    aerodromeName: string;
    startDateFilter: string;
    startTimeFilter: string;
    endDateFilter: string;
    endTimeFilter: string;
    start_date: string;
    start_time: string;
    end_date: string;
    end_time: string;
    startDateTimeFilter: string;
    endDateTimeFilter: string;
    getMapSortQueryString: any;
    filterParameters: object;
    $watchGroup: any;
}