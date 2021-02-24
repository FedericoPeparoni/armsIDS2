// should be only for cesium related interfaces

/**
 * Cesium Specific Interfaces
 */

export interface IViewer { // https://cesiumjs.org/Cesium/Build/Documentation/Viewer.html
  layer: ICesiumLayer;
  camera: any; // todo not `any` // https://cesiumjs.org/Cesium/Build/Documentation/Camera.html
  flyTo: any; // todo not `any`,
  imageryLayers: any; // https://cesiumjs.org/Cesium/Build/Documentation/ImageryLayer.html
  entities: any; // individual entities
  dataSources: {
    add: (ICesiumLayer) => void;
    remove: (ICesiumLayer) => void;
    load: (url: string, opts: ISettings) => Promise<ICesiumLayer>;
    _dataSources: ICesiumLayer[];
    length: number; // https://cesiumjs.org/Cesium/Build/Documentation/DataSourceCollection.html?classFilter=datas#get
    get: (number) => any; // https://cesiumjs.org/Cesium/Build/Documentation/DataSourceCollection.html?classFilter=datas#get
  };
  scene: any;
  drawToolPolyline: any;
  canvas: any;
  extend: any;
  clock: any;
}

export interface IViewerParameters { // https://cesiumjs.org/Cesium/Build/Documentation/Viewer.html
  timeline?: boolean;
  selectionIndicator?: boolean;
  animation?: boolean;
  fullscreenButton?: boolean;
  baseLayerPicker?: boolean; // whether they can choose the globe terrain // https://cesiumjs.org/Cesium/Build/Documentation/BaseLayerPicker.html
  imageryProvider?: Object; // https://cesiumjs.org/Cesium/Build/Documentation/ImageryProvider.html?classFilter=imageryProvider
  sceneMode?: number; // 2d/3d/etc. // https://cesiumjs.org/Cesium/Build/Documentation/SceneMode.html?classFilter=sceneMode
}

export interface IFlyToOptions { // https://cesiumjs.org/Cesium/Build/Documentation/Camera.html#flyTo
  destination: any;
  orientation?: any;
  duration?: number;
  maximumHeight?: number;
  pitchAdjustHeight?: number;
  flyOverLongitude?: number;
  flyOverLongitudeWeight?: number;
  easingFunction?: any;
}

export interface ISettings {
  fillOpacity?: number;
  markerSymbol?: string;
  markerColor?: ICesiumColor;
  markerSize?: string;
  stroke?: ICesiumColor;
  fill?: ICesiumColor;
  strokeWidth?: number;
  show?: boolean;
}

export interface ICesiumLayer {
  alpha: number;
  entities: ICesiumEntity;
  layerName: string;
  displayName: string;
  cssValue: string;
  show: boolean;
}

export interface IImageryLayer {
  alpha: number;
  brightness: number;
  imageryProvider: any;
  layerName: string;
  displayName: string;
  show: boolean;
}

export interface ICesiumEntity {
  id: string;
  show: boolean;
  values: Array<any>;
  polygon: ICesiumPolygonGraphic;
  polyline: ICesiumPolylineGraphic;
}

export interface ICesiumPolygonGraphic {
  hierarchy: ICesiumPolygonHierarchyProperty;
}

export interface ICesiumPolylineGraphic {
  positions: ICesiumCartesian3Property;
}

export interface ICesiumPolygonHierarchyProperty {
  getValue(time: any, result?: any): ICesiumPolygonHierarchy;
}

export interface ICesiumCartesian3Property {
  getValue(time: any, result?: any): Array<ICesiumCartesian3>;
}

export interface ICesiumPolygonHierarchy {
  holes: Array<ICesiumCartesian3>;
  positions: Array<ICesiumCartesian3>;
}

export interface ICesiumCartesian3 {
  x: Number;
  y: Number;
  z: Number;
}

export interface ICesiumColor { // https://cesiumjs.org/Cesium/Build/Documentation/Color.html
  red: number;
  green: number;
  blue: number;
  alpha: number;
}

/**
 * Constructor
 */
export interface ICesiumConstructorParams {
  debug?: boolean;
  flytoDuration?: number;
  flytoPadding?: number;
  flytoMaxZoom?: number;
}
