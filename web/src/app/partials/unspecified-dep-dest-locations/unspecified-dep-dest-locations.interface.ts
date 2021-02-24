// interfaces
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IUnspecifiedDepartureLocationTypeSpring extends ISpringPageableParams {
  content: Array<IUnspecifiedDepartureLocationType>;
}

export interface IUnspecifiedDepartureLocationType {
  id?: number;
  text_identifier: string;
  name: string;
  maintained: Boolean;
  aerodrome_identifier: string;
  latitude: number;
  longitude: number;
  status: string;
}

export interface IStatusObject {
  name: string;
  value: string;
}

export interface IUnspecifiedDepartureLocationScope extends angular.IScope {
  aerodromeList: Array<IAerodrome>;
  statusList: Array<IStatusObject>;
  edit: (editable: IUnspecifiedDepartureLocationType) => void;
  latitudeRegex: RegExp;
  longitudeRegex: RegExp;
  booleanList: Array<any>;
}
