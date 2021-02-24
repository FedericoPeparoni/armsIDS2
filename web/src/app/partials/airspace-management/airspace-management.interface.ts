import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';

export interface IAirspace {
  airspace_name: string; // is actually `ident`.  In NavDB this is `airspace.ident`
  airspace_full_name: string; // is actually the `name`.  In NavDB this is `airspace.nam`
  airspace_type: string;
  airspace_included: boolean;
  airspace_ceiling: number;
  id: number;
}

export interface IAirspaceManagementScope extends ICRUDScope {
  listFromNavDB: IAirspace[];
  navDBError: boolean;
  airspaceAlreadyExists: boolean;
  loadAirspaces: () => void;
  checkIfAirspaceAdded: (airspaceName: string) => boolean;
  filterAirspaceType: (airspaceType: string) => IAirspace[];
  showAirspaceOnMap: (airspace: IAirspace) => void;
}
