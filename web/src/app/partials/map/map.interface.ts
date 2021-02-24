// should be only for angular map related interfaces

import {IViewer, ICesiumLayer, ISettings} from './cesium.interface';

export interface IMapScope extends ng.IScope {
  aerodromesLayerName: string;
  flightMovementLoaded: boolean;
  viewer: IViewer;
  layers: Array<ICesiumLayer>;
  showDetails: boolean;
  showInformationTab: boolean;
  toggleLayer: Function;
  changeLayerAlpha: Function;
  closeMapWindow: () => void;
  measureMap: () => void;
  cesiumVersion: () => void;
  useLocalBase: boolean;
  setBaseSource: (loadLocalBaseLayer: boolean) => void;
}

export interface IMapLayers {
  airports: IMapLayerParams;
  aerodromes: IMapLayerParams;
  airspacesNavDB: IMapLayerParams;
  airspaces: IMapLayerParams;
  airspaceCurrent: IMapLayerParams;
  airspaceSelect: IMapLayerParams;
  routeSegments: IMapLayerParams;
  flightMovements: IMapLayerParams;
  flightMovementsOutsideSP: IMapLayerParams;
  flightMovementsInsideSP: IMapLayerParams;
  flightMovementsRadar: IMapLayerParams;
  flightMovementsATC: IMapLayerParams;
  flightMovementsTower: IMapLayerParams;
  flightMovementsScheduled: IMapLayerParams;
  flightMovementsI: IMapLayerParams;
}

export interface ISegment {
  id: string;
  label: string;
  position: any;
  point: number;
}

export interface ISegmentOptions {
  pointColor: string;
  visible: boolean;
}

interface IMapLayerParams {
  displayName: string;
  layerName: string;
  options: ISettings;
}
