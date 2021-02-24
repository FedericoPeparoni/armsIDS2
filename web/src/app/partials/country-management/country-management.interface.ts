export interface IAircraftRegistrationPrefix {
  aircraft_registration_prefix: string;
  country_code: ICountryForPrefix;
}

export interface IAerodromePrefix {
  aerodrome_prefix: string;
  country_code: ICountryForPrefix;
}

export interface ICountry {
  id?: number;
  country_code: string;
  country_name: string;
  aircraft_registration_prefixes: Array<IAircraftRegistrationPrefix>;
  aerodrome_prefixes: Array<IAerodromePrefix>;
  aircraft_registration_prefixes_input?: string | Array<IAircraftRegistrationPrefix>;
  aerodrome_prefixes_input?: string | Array<IAerodromePrefix>;
}

export interface ICountryForPrefix {
  id?: number;
  country_code: string;
  country_name: string;
}

export interface ICountrySpring {
  content: Array<ICountry>;
}

export interface ICountryManagementScope extends ng.IScope {
  editable: ICountry;
}
