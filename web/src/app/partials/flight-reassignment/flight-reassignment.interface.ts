// interface
import { IAerodrome } from '../aerodromes/aerodromes.interface';

export interface IFlightReassignment {
  id?: number;
  applies_to_type_arrival: boolean;
  applies_to_type_departure: boolean;
  applies_to_type_domestic: boolean;
  applies_to_type_overflight: boolean;
  applies_to_scope_domestic: boolean;
  applies_to_scope_regional: boolean;
  applies_to_scope_international: boolean;
  applies_to_nationality_national: boolean;
  applies_to_nationality_foreign: boolean;
  identification_type: string;
  identifier_text: string;
  aerodrome_identifiers: Array<string>;
  start_date: Date;
  end_date: Date;
  account: number;
}

export interface IFlightReassignmentScope {
  aerodromesList: Array<IAerodrome>;
  aerodromesModel: Array<IAerodrome>;
  enteredAerodromes: string|Array<string>;
  refreshOverride: () => void;
  getSortQueryString: () => string;
  getApplication: (item : IFlightReassignment, type: string) => string;
  setMaxLength: (idType: string) => void;
  maxLength: number;
  pagination: any;
  search: string;
  textFilter: string;
  accountFilter: number;
  addAerodromeToList: Function;
  updateAerodromeMultiselect: Function;
  editable: IFlightReassignment;
  filterParameters: object;
  getAerodromeIdentifiers: Function;
}
