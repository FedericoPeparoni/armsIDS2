import { ICountry } from '../country-management/country-management.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';

export interface IRegionalCountry {
  id: number;
  country: ICountry;
}

export interface IRegionalCountrySpring extends ISpringPageableParams {
  content: Array<IRegionalCountry>;
}

export interface IRegionalCountryScope extends ICRUDScope {
  editableCountryList: IRegionalCountrySpring;
  regionalCountryList: IRegionalCountrySpring;
  countryList: Array<ICountry>;
}
