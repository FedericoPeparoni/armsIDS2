import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';

export interface ILocalAircraftRegistry {
  id?: number;
  registration_number: string;
  owner_name: string;
  analysis_type: string;
  mtow_weight: number;
  coa_date_of_renewal: string;
  coa_date_of_expiry: string;
}

export interface ILocalAircraftRegistryScope {
  control: IStartEndDates;
  convertMtowProperty: Function;
  organizationName: string | number;
  create: (localAircraftRegistry: ILocalAircraftRegistry, startDate: string, endDate: string) => void;
  edit: (localAircraftRegistry: ILocalAircraftRegistry) => void;
  addDate: (localAircraftRegistry: ILocalAircraftRegistry) => void;
  editable: ILocalAircraftRegistry;
  mtowUnitOfMeasure: string | number;
  pattern: string;
  pagination: any;
  search: string;
  registrationFilter: string;
  update: (localAircraftRegistry: ILocalAircraftRegistry, startDate: string, endDate: string) => void;
  refreshOverride: () => void;
  getSortQueryString: () => string;
  customDate: string;
  filterParameters: object;
}
