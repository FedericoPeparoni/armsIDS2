import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';

export interface IInterestRate {
    id?: number;
    default_interest_specification: string;
    default_interest_application: string;
    default_interest_grace_period: number;
    default_foreign_interest_specified_percentage: number;
    default_national_interest_specified_percentage: number;
    default_foreign_interest_applied_percentage: number;
    default_national_interest_applied_percentage: number;
    punitive_interest_specification: string;
    punitive_interest_application: string;
    punitive_interest_grace_period: number;
    punitive_interest_specified_percentage: number;
    punitive_interest_applied_percentage: number;
    start_date: string;
    end_date: string;
}

export interface IInterestRateScope {
    customDate: string;
    refresh: Function;
    create: Function;
    update: Function;
    control: IStartEndDates;
    editable: IInterestRate;
    search: string;
    pagination: ISpringPageableParams;
    getSortQueryString: () => string;
    filterParameters: object;
}

