import { ISpringPageableParams } from "../../angular-ids-project/src/helpers/services/crud.service";

export interface INominalType {
  id?: number;
  type: string;
  pointa: string;
  pointb: string;
  nominal_distance: number;
  bi_directional: boolean;
  nominal_route_floor: number;
  nominal_route_ceiling: number;
}

export interface IRouteType {
  id: number;
  name: string;
  value: string;
}

export interface INominalroutesScope {
  listRouteTypes: Function;
  routeTypes: Array<IRouteType>;
  list: Array<INominalType>;
  filterParameters: object;
  refresh: Function;
  textFilter: string;
  pagination: ISpringPageableParams;
  getSortQueryString: () => string;
}
