// this class should contain NO FRAMEWORK SPECIFIC CODE
// we want to continue using this for future projects

// cesium viewer is private and not to be directly accessed outside
// dataSources are accessible outside via `dataSources` method, this could be refactored to not have direct dataSource access in the future

import {
  ICesiumColor, ICesiumEntity, ICesiumLayer, IImageryLayer, IViewer, ISettings,
  IViewerParameters, IFlyToOptions, ICesiumConstructorParams, ICesiumCartesian3
} from './cesium.interface';

declare let DrawToolPolyline: any;
declare let Cesium;

export class CesiumClass {

  private _viewer: IViewer = null;
  private _cesiumKey: string = null;
  private _debug: boolean = false;
  private _customPins: Map<{}, {}> = new Map();
  private _flytoDuration: number = null;
  private _flytoPadding: number = null;
  private _flytoMaxZoom: number = null;

  constructor(params: ICesiumConstructorParams = {}) {
    this.debug = params.debug;
    this.flytoDuration = params.flytoDuration || null;
    this.flytoPadding = params.flytoPadding || null;
    this.flytoMaxZoom = params.flytoMaxZoom || null;
  }

  public toggleRuler(): void {
    if (!this._viewer.drawToolPolyline) {
      this._viewer.extend(DrawToolPolyline.viewerCesiumDrawPoly, {});
      this._viewer.drawToolPolyline.startDrawing();
    } else {
      this._viewer.drawToolPolyline.destroy();
    }
  }
  // returns cesium color object from hexadecimal string
  public fromColorString(hexColor: string = '#000000', alpha: number = 1.0): ICesiumColor {
    return new Cesium.Color.fromCssColorString(hexColor).withAlpha(alpha);
  }

  // returns cesium color object
  fromColor(red: number = 0, green: number = 0, blue: number = 0, alpha: number = 1.0): ICesiumColor {
    return new Cesium.Color(red, green, blue, alpha);
  }

  // returns a matching predefined cesium color object
  getColor(theColor: string = 'WHITE'): ICesiumColor {
    return Cesium.Color[theColor];
  }

  // returns a matching predefined cesium color object
  getCSSColor(theColor: any): string {
    return theColor.toCssColorString();
  }

  set cesiumKey(key: string) {

    // we do not use Ion, must remove default access token
    // https://groups.google.com/d/msg/cesium-dev/OPYLmP4iCe8/Uy788PJqDwAJ
    Cesium.Ion.defaultAccessToken = null;

    // setup cesium key with bing maps only
    Cesium.BingMapsApi.defaultKey = key;

    this._cesiumKey = key;
  }

  get customPins(): Map<{}, {}> {
    return this._customPins;
  }

  createViewer(cesiumContainerElement: string, cesiumViewerParameters: IViewerParameters = {}): void {
    this.viewer = new Cesium.Viewer(cesiumContainerElement, cesiumViewerParameters);
  }

  // set the default camera position for the viwer based on the position defined in .env
  setDefaultCameraPosition(west: number = 0, south: number = 0, east: number = 0, north: number = 0): void {

    // if longitude values are too small for max zoom
    // find midpoint and add/minus max zoom
    if (east - west < this.flytoMaxZoom) {
      const midpoint = (east + west) / 2;
      east = midpoint + this.flytoMaxZoom / 2;
      west = midpoint - this.flytoMaxZoom / 2;
    }

    // if latitude values are too small for max zoom
    // find midpoint and add/minus max zoom
    if (north - south < this.flytoMaxZoom) {
      const midpoint = (north + south) / 2;
      north = midpoint + this.flytoMaxZoom / 2;
      south = midpoint - this.flytoMaxZoom / 2;
    }

    Cesium.Camera.DEFAULT_VIEW_RECTANGLE = new Cesium.Rectangle.fromDegrees(west, south, east, north);

    this.viewer.scene.camera.setView({
      destination: Cesium.Camera.DEFAULT_VIEW_RECTANGLE
    });
  }

  /**
   * Adds a layer and makes an AJAX call to navdb/geoserver
   *
   * @param navdb         URL to navdb/geoserver
   * @param boundingBox   if it's only to a certain section of the map (bounding box)
   * @param layerName         the layer to add
   * @param displayName   display name to give layer
   * @param opts          options used for the datasource
   * @param layerNameDb   geoserver layer name, if null uses layerName
   */
  addLayer(navdb: string = null, boundingBox: string = null, layerName: string = '', displayName: string = '', opts: ISettings = {},
           viewParams: string = '', layerNameDb: string = null): ng.IPromise<ICesiumLayer> {
    return this.loadLayer(navdb, boundingBox, layerName, opts, viewParams, layerNameDb)
      .then((layer: ICesiumLayer): ICesiumLayer => {
        layer.layerName = layerName;
        layer.displayName = displayName;
        layer.cssValue = this.getCSSColor(opts.markerColor || opts.stroke);
        layer.alpha = opts.fillOpacity;
        layer.show = opts.show === undefined ? true : opts.show;
        this.addDataSource(layer);
        return layer;
      });
  }

  /**
   * Loads a layer and makes an AJAX call to navdb/geoserver
   *
   * @param navdb         URL to navdb/geoserver
   * @param boundingBox   if it's only to a certain section of the map (bounding box)
   * @param layerName     the layer to add
   * @param opts          options used for the datasource
   * @param layerNameDb   geoserver layer name, if null uses layerName
   */
  loadLayer(navdb: string = null, boundingBox: string = null, layerName: string = '', opts: ISettings = {}, viewParams: string = '',
            layerNameDb: string = null): ng.IPromise<ICesiumLayer> {

    let url = `${navdb}${layerNameDb || layerName}`;

    const dataSource = new Cesium.GeoJsonDataSource(layerName);

    if (boundingBox !== null) {
      url += `&bbox=${encodeURIComponent(boundingBox)}&`;
    }

    if (viewParams !== null) {
      url += '&viewparams=' + encodeURIComponent(viewParams);
    }

    return dataSource.load(url, opts);
  }

  /**
   *
   * @param db The database name for part of the request url in Geosever
   * @param workspace The name of the workspace for part of the request url in Geoserver
   * @param layerName The layer name to load from Geoserver
   * @param displayName The display name used for tracking purposes
   * @param viewParams Any view paramaters used for querying against Geoserver
   */
  addWMSLayer(db: string = null, workspace: string = '', layerName: string = '', displayName: string = '', viewParams: string = '',
              layerDbName: string = null, layerDbStyle: string = null): IImageryLayer {
    // the request url
    let url = `${db}${workspace}/wms`;
    // the WMS request params
    let params = {
      service: 'WMS',
      version: '1.1.1',
      request: 'GetMap',
      styles: layerDbStyle || 'V_Airspaces',
      // format_options : "antialiasing:false",
      tiled: true,
      transparent: true,
      format: 'image/png',
      viewParams: viewParams || ''
    };

    // construct the imagery provider with the new url and params
    let provider = new Cesium.WebMapServiceImageryProvider({
      url: url,
      layers: `${workspace}:` + (layerDbName || layerName),
      parameters: params,
      getFeatureInfoParameters: {
      }
    });

    // add the imagery layer to the collection
    let imageryLayers = this.viewer.imageryLayers.addImageryProvider(provider);
    // set the display name property to the new imagery layer
    imageryLayers.displayName = displayName;
    imageryLayers.layerName = layerName;
    // return the new imagery layer
    return imageryLayers;
  }

  /**
   * Tries to match an `id` with the `entities` property `id` and fly viewer camera
   * to found entities. If none found, viewer camera will fly home.
   *
   * Note: `options.destination` is ignored and overwritten with layer entities as a rectangle.
   */
  flyToFindByID(layerName: string, id: number, options: IFlyToOptions = { destination: null }): void {
    const entities = this.getEntityValuesByLayerName(layerName);
    const flyToTarget = entities.find((entity : any) => entity.properties.id === id);

    // fly to found target entities
    this.flyToEntities(flyToTarget, options);
  }

  /**
   * Fly viewer camera to list of cesium entities, padding and max/min zoom is applied. If no entity points found,
   * viewer camera will fly home.
   *
   * Note: `options.destination` is ignored and overwritten with entities as a rectangle.
   */
  flyToEntities(target: ICesiumEntity[], options: IFlyToOptions = { destination: null }): void {

    // get all points from each target
    let points: Array<ICesiumCartesian3> = [];
    target.forEach((entity: ICesiumEntity) => {
      if (entity.polygon) {
        points = points.concat(entity.polygon.hierarchy.getValue(this.viewer.clock.currentTime).positions);
      } else if (entity.polyline) {
        points = points.concat(entity.polyline.positions.getValue(this.viewer.clock.currentTime));
      }
    });

    // if no points found, fly home
    if (points.length === 0) {
      this.flyHome();
      return;
    }

    // create rectangular viewpoint from each point and fly to resulting viewport
    this.flyToViewport(Cesium.Rectangle.fromCartesianArray(points, Cesium.Ellipsoid.WGS84), options);
  }

  /**
   * Fly viewer camera to visiable viewport. Padding and max/min zoom is applied.
   *
   * Note: `options.destination` is ignored and overwritten with viewport.
   */
  flyToViewport(viewport: any, options: IFlyToOptions = { destination: null }): void {

    // get viewport computed height and width
    var viewportHeight = Cesium.Rectangle.computeHeight(viewport);
    var viewportWidth = Cesium.Rectangle.computeWidth(viewport);

    // if viewport dimensions are less then max zoom level
    // set to max zoom level
    viewportHeight = viewportHeight > this.flytoMaxZoom ? viewportHeight : this.flytoMaxZoom;
    viewportWidth = viewportWidth > this.flytoMaxZoom ? viewportWidth : this.flytoMaxZoom;

    // calculate padding from height and width
    var horizontalPadding = viewportHeight * this.flytoPadding;
    var verticalPadding = viewportWidth * this.flytoPadding;

    // add padding to viewpoint
    viewport.west = viewport.west - horizontalPadding;
    viewport.north = viewport.north + verticalPadding;
    viewport.east = viewport.east + horizontalPadding;
    viewport.south = viewport.south - verticalPadding;

    // ensure viewport does not exceed valid range - https://cesiumjs.org/Cesium/Build/Documentation/Rectangle.html
    // valid range [west, east]: [-Pi, Pi]
    // valid range [south, north]: [-Pi/2, Pi/2]
    viewport.west = viewport.west < -Math.PI ? -Math.PI : viewport.west;
    viewport.north = viewport.north > Math.PI / 2 ? Math.PI / 2 : viewport.north;
    viewport.east = viewport.east > Math.PI ? Math.PI : viewport.east;
    viewport.south = viewport.south < -Math.PI / 2 ? -Math.PI / 2 : viewport.south;

    // set options destination to viewport
    options.destination = viewport;

    // move camera using defined fly to options
    this.flyTo(options);
  }

  /**
   * Fly viewer camera home using system configuration settings.
   *
   * Note: `options.destination` is ignored and overwritten with default view rectangle.
   */
  flyHome(options: IFlyToOptions = { destination: null }): void {

    // set destination to default view rectangle defined from system configuration settings
    options.destination = Cesium.Camera.DEFAULT_VIEW_RECTANGLE;

    // move camera using defined fly to options
    this.flyTo(options);
  }

  /**
   * Fly viewer camera to destination using provided fly to options or default values. No
   * padding or max/min zoom is applied. If no `options.destination` value supplied, viewer
   * camera will fly home.
   * 
   * https://cesiumjs.org/Cesium/Build/Documentation/Viewer.html?classFilter=viewer#flyTo
   */
  flyTo(options: IFlyToOptions = { destination: null }): void {

    // if no destination provided, fly to default view rectangle defined from system configuration settings
    if (!options.destination) {
      options.destination = Cesium.Camera.DEFAULT_VIEW_RECTANGLE;
    }

    // default option duration to 800 milliseconds
    if (!options.duration) {
      options.duration = this.flytoDuration;
    }

    // move camera to viewpoint
    this.viewer.camera.flyTo(options);
  }

  /**
   * Returns the index of the imagery layer from the list.
   * @param layerName Name of the imagery layer
   */
  getImageryLayersIndexByLayerName(layerName: string): number {
    for (let i = 0; i < this.viewer.imageryLayers.length; i++) {
      if (layerName === this.viewer.imageryLayers.get(i).layerName) {
        return i;
      }
    }
  }
  /**
   * Returns the matching imagery layer if it exists in the list.
   * @param layerName Name of the imagery layer
   */
  getImageryLayersByLayerName(layerName: string): ICesiumLayer {
    const index = this.getImageryLayersIndexByLayerName(layerName);
    return this.viewer.imageryLayers.get(index);
  }

  sortDateSourceByDisplayName(asc: boolean = true): void {
    this.dataSources.sort((a: ICesiumLayer, b: ICesiumLayer): any => {
      if (asc) {
        return a.displayName > b.displayName;
      } else {
        return a.displayName < b.displayName;
      }
    });
  }

  // returns the index of a dataSource from it's `layerName`
  getDataSourceIndexByLayerName(layerName: string): number {
    this.sortDateSourceByDisplayName();
    for (let i = 0; i < this.dataSources.length; i++) {
      if (layerName === this.dataSources[i].layerName) {
        return i;
      }
    }
  }
  getDataSourceByLayerName(layerName: string): ICesiumLayer {
    const index = this.getDataSourceIndexByLayerName(layerName);
    return this.dataSources[index];
  }

  getEntityValuesByLayerName(layerName: string): any[] {
    const ds = this.getDataSourceByLayerName(layerName);
    return ds ? ds.entities.values : [];
  }

  // changes the layer opacity
  changeLayerAlpha(layer: ICesiumLayer = null): void {

    let entities = layer.entities.values;

    for (let i = 0, len = entities.length; i < len; i++) {
      let entity = entities[i];
      let alpha: number = layer.alpha;

      if (alpha >= 1) {
        alpha = 1.00;
      }

      if (entity.polyline) {
        let color = entity.polyline.material.color.getValue();
        entity.polyline.material.color.setValue(new Cesium.Color(color.red, color.green, color.blue, alpha));
      } else if (entity.polygon) {
        let color = entity.polygon.material.color.getValue();
        entity.polygon.material = this.fromColor(color.red, color.green, color.blue, alpha);
      } else if (entity.billboard) {
        entity.billboard.color = this.fromColorString('#435888', alpha); // todo can't find key to retrieve entity color
      }
    }
  }

  getSceneMode(scene: string = null): number {
    return Cesium.SceneMode[scene];
  }
  // static datasources list. BAD
  get dataSources(): ICesiumLayer[] {
    return this.viewer.dataSources._dataSources;
  }
  // datasources instance. GOOD
  // todo: set proper interface
  get dataSourcesInstance(): any {
    return this.viewer.dataSources;
  }

  // remove a loaded data source by name if it exists
  removeDataSourceByName(layerName: string = null): void {
    var layer = null;
    do {
      layer = this.getDataSourceByLayerName(layerName);
      if (layer) {
        this.viewer.dataSources.remove(layer);
      }
    } while (layer);
  }

  // remove a loaded imagery layer by layer name if present
  removeImageryLayerByName(layerName: string = null): void {
    for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
      if (this.viewer.imageryLayers.get(i).layerName === layerName) {
        return this.viewer.imageryLayers.remove(this.viewer.imageryLayers.get(i));
      }
    }
  }

  // if objectsToDisplay is `null`, will display/hide ALL
  showEntitiesByDataSource(layerName: string = null, objectsToDisplay: any[], hide: boolean = false): void {
    const entities = this.getEntityValuesByLayerName(layerName);
    for (let i = 0; i < entities.length; i++) {

      if (objectsToDisplay !== null) {
        for (let o = 0; o < objectsToDisplay.length; o++) {
          if (objectsToDisplay[o].id === entities[i].properties.id) {
            entities[i].show = !hide;
          }
        }
      } else {
        entities[i].show = !hide; // show/hide ALL
      }
    }
  }

  /**
   * Custom label creation util.
   * @param name Easy lookup name
   * @param assetUrl Url for which the asset can be located
   * @param color The background color of the new custom pin
   */
  labelMaker(name: string = null, assetUrl: string = null, color: any): ng.IPromise<any> {
    var self = this;
    if (!this.customPins.get(name)) {
      var pinBuilder = new Cesium.PinBuilder();

      return Cesium.when(pinBuilder.fromUrl(assetUrl, color, 40),
        function (canvas: any): any {
          // once canvas image is generated, set it to the map object by key name
          self.customPins.set(name, canvas.toDataURL());
          return canvas.toDataURL();
        }
      );
    }
  }


  /**
   * Shows/hides layers
   * null means show/hide all
   * array length of zero means do nothing
   *
   * @param layersToShow
   * @param layersToHide
   */
  toggleDataSourceLayers(layersToShow: string[] = [], layersToHide: string[] = []): void {

    // ensures that there are no duplicates, priority given to `layersToShow`
    if (layersToShow !== null && layersToHide !== null) {
      layersToHide = layersToHide.filter((val: string) => layersToShow.indexOf(val) !== -1);
    }

    for (let i = 0; i < this.dataSources.length; i++) {

      let show = false; // ensures we do not hide layers, we have just toggled to show

      if (layersToShow === null || layersToShow.indexOf(this.dataSources[i].layerName) !== -1) {
        show = true;
        this.dataSources[i].show = true;
      }

      if (!show && (layersToHide === null || layersToHide.indexOf(this.dataSources[i].layerName) !== -1)) {
        this.dataSources[i].show = false;
      }
    }
  }

  createOpenStreetMap(url: string = null, layerName: string = null, displayName: string = null): any {
    if (url === null) {
      url = 'https://a.tile.openstreetmap.org/';
    }
    let osmProvider = new Cesium.createOpenStreetMapImageryProvider({
      url: url
    });
    osmProvider.layerName = layerName;
    osmProvider.displayName = displayName;
    return osmProvider;
  }

  removeDataSource(layerName: string = null): void {
    const dataSource = this.getDataSourceByLayerName(layerName);
    this.viewer.dataSources.remove(dataSource);
  }

  removeDataSourceLayer(layer: ICesiumLayer): void {
    this.viewer.dataSources.remove(layer);
  }

  get imageryLayers(): IImageryLayer[] {
    /* var arr = [];
     for (var i = this.viewer.imageryLayers.length - 1; i >= 0; i--) {
       arr.push(this.viewer.imageryLayers.get(i));
     }
     //return arr; */
    return this.viewer.imageryLayers._layers;

  }

  addDataSource(layer: ICesiumLayer = null): void {
    this.viewer.dataSources.add(layer);
  }

  set viewer(viewer: IViewer) {
    this._viewer = viewer;
  }

  get viewer(): IViewer {
    return this._viewer;
  }

  get version(): string {
    return Cesium.VERSION;
  }

  // the main Cesium accessor
  get mainCesium(): any {
    return Cesium;
  }

  private get debug(): boolean {
    return this._debug;
  }

  private set debug(debug: boolean) {
    this._debug = debug;
  }

  private get flytoDuration(): number {
    return this._flytoDuration;
  }

  private set flytoDuration(flytoDuration: number) {
    this._flytoDuration = flytoDuration;
  }

  private get flytoPadding(): number {
    return this._flytoPadding;
  }

  private set flytoPadding(flytoPadding: number) {
    this._flytoPadding = flytoPadding;
  }

  private get flytoMaxZoom(): number {
    return this._flytoMaxZoom;
  }

  private set flytoMaxZoom(flytoMaxZoom: number) {
    this._flytoMaxZoom = flytoMaxZoom;
  }
}
