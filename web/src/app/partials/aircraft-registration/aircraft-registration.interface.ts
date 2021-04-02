// interfaces
import { IAccount } from '../accounts/accounts.interface';
import { IAircraftType } from '../aircraft-type-management/aircraft-type-management.interface';
import { ICountry } from '../country-management/country-management.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IAircraftRegistration {
  id?: number;
  registration_number: string;
  registration_start_date: string;
  registration_expiry_date: string;
  mtow_override: number;
  country_override: boolean;
  account: IAccount;
  aircraft_type: IAircraftType;
  country_of_registration: ICountry;
  created_by_self_care: boolean;
  coa_expiry_date: string;
  coa_issue_date: string;
  aircraft_service_date: string;
  is_local: boolean;
  aircraft_scope : string;

}

export interface IAircraftRegistrationScope extends ng.IScope {
  form: any;
  list: Array<IAircraftType>;
  organizationName: string | number;
  refresh: () => void;
  accountsList: Array<IAccount>;
  control: IStartEndDates;
  control_coa: IStartEndDates;
  editable: IAircraftRegistration;
  aircraftTypesList: Array<IAircraftType>;
  countryList: Array<ICountry>;
  error: IExtendableError;
  countryMatch: boolean;
  aircraftTypeManagementService: Array<IAircraftType>;
  mtowUnitOfMeasure: string | number;
  getCountryByRegistrationNumberPrefix: (prefix: string) => void;
  country: any;
  $watch: any;
  addDate: (aircraft: IAircraftRegistration) => void;
  convertMtowProperty: Function;
  getAircraftTypeByLatestRegistrationNumber: (registrationNumber: string) => void;
  update: (aircraft: IAircraftRegistration, startDate: string, endDate: string) => void;
  create: (aircraft: IAircraftRegistration, startDate: string, endDate: string) => void;
  edit: (aircraft: IAircraftRegistration) => void;
  setEditable: (aircraft: IAircraftRegistration) => void;
  setLocal: (country: ICountry) => void;
}

export interface IAircraftRegistrationSpring {
  content: Array<IAircraftRegistration>;
}
