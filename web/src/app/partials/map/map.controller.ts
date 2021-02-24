// note: this Map Controller should be for Angular related code

// constants
import { MapFlyToEntityType } from './map.constants';

// controllers
import { CesiumClass } from './cesium.class';

// interface
import { ICesiumLayer } from './cesium.interface';
import { IMapScope, IMapLayers } from './map.interface';
import { IFlightMovement } from '../flight-movement-management/flight-movement-management.interface';
import { IAirspace } from '../airspace-management/airspace-management.interface';
import { ICancellablePromise, ICancellablePromiseException } from '../../angular-ids-project/src/components/cancellable/cancellable.interface';

// services
import { ConfigService } from '../../angular-ids-project/src/components/services/config/config.service';
import { SystemConfigurationService } from '../../partials/system-configuration/service/system-configuration.service';

// utils
import { Cancellable } from '../../angular-ids-project/src/components/cancellable/cancellable.class';
import { makePointEntity, middlePointCreation } from './map.utils';

// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';

import { MapButton, MapCloseButton, MapRulerButton } from './map.buttons';

const cesium = new CesiumClass({
  flytoDuration: 0.8,
  flytoPadding: 0.1,
  flytoMaxZoom: 0.05
});

export class MapController {

  /**
   * Holder for cesium helper class.
   */
  private cesium: CesiumClass;

  /**
   * Used to cancel in-flight flight movement layer requests.
   */
  private flightMovementRequest: ICancellablePromise = null;

  /**
   * Cesium map layer display options.
   */
  private layers: IMapLayers = {
    airports: {
      layerName: 'V_AIRPORT_SP',
      displayName: 'Airports NavDB',
      options: {
        markerSymbol: 'airport',
        markerColor: cesium.fromColorString('#435888', 1.0),
        strokeWidth: 3,
        stroke: cesium.getColor('WHITE'),
        fill: cesium.getColor('WHITE')
      }
    },
    aerodromes: {
      layerName: 'V_Aerodromes',
      displayName: 'Aerodromes',
      options: {
        markerSymbol: 'airport',
        markerColor: cesium.getColor('CORNFLOWERBLUE'),
        strokeWidth: 3,
        stroke: cesium.getColor('CORNFLOWERBLUE'),
        fill: cesium.getColor('WHITE')
      }
    },
    airspacesNavDB: {
      layerName: 'V_AIRSPACE_SP',
      displayName: 'Airspaces NavDB',
      options: {
        stroke: cesium.fromColorString('#734388', 0.5),
        fill: cesium.fromColorString('#734388', 0.5),
        strokeWidth: 3,
        fillOpacity: 0.5
      }
    },
    airspaces: {
      layerName: 'V_Airspaces',
      displayName: 'Airspaces',
      options: {
        // styling is done on imagery layer by geoserver
        // this is used only to calculate viewport and shouldn't
        // be used to display airspace as features due to issue
        // https://github.com/AnalyticalGraphicsInc/cesium/issues/4871
        stroke: cesium.getColor('TRANSPARENT'),
        fill: cesium.getColor('TRANSPARENT'),
        strokeWidth: 0,
        fillOpacity: 0,
        show: false
      }
    },
    airspaceCurrent: {
      layerName: 'V_Airspace_Current',
      displayName: 'Current Airspace',
      options: {
        // styling is done on imagery layer by geoserver
        // this is used only to calculate viewport and shouldn't
        // be used to display airspace as features due to issue
        // https://github.com/AnalyticalGraphicsInc/cesium/issues/4871
        stroke: cesium.getColor('TRANSPARENT'),
        fill: cesium.getColor('TRANSPARENT'),
        strokeWidth: 0,
        fillOpacity: 0,
        show: false
      }
    },
    airspaceSelect: {
      layerName: 'V_Airspace_Select',
      displayName: 'Selected Airspace',
      options: {
        // styling is done on imagery layer by geoserver
        // this is used only to calculate viewport and shouldn't
        // be used to display airspace as features due to issue
        // https://github.com/AnalyticalGraphicsInc/cesium/issues/4871
        stroke: cesium.getColor('TRANSPARENT'),
        fill: cesium.getColor('TRANSPARENT'),
        strokeWidth: 0,
        fillOpacity: 0,
        show: false
      }
    },
    routeSegments: {
      layerName: 'V_RouteSegments',
      displayName: 'Billed',
      options: {
        show: true,
        stroke: cesium.getColor('RED'),
        strokeWidth: 2
      }
    },
    flightMovements: {
      layerName: 'V_FlightMovement',
      displayName: 'Flight Movements',
      options: {
        stroke: cesium.fromColor(1.0, 0.55, 0, 1),
        strokeWidth: 2
      }
    },
    flightMovementsInsideSP: {
      layerName: 'V_FlightMovementInside_SP',
      displayName: 'Billed',
      options: {
        show: false,
        stroke: cesium.getColor('DARKRED'),
        strokeWidth: 2
      }
    },
    flightMovementsOutsideSP: {
      layerName: 'V_FlightMovementOutside_SP',
      displayName: 'Not Billed',
      options: {
        stroke: cesium.getColor('BLUE'),
        strokeWidth: 2
      }
    },
    flightMovementsRadar: {
      layerName: 'V_FlightMovementRadar',
      displayName: 'FM Radar',
      options: {
        show: false,
        stroke: cesium.getColor('DARKORANGE'),
        strokeWidth: 2
      }
    },
    flightMovementsATC: {
      layerName: 'V_FlightMovementATC',
      displayName: 'FM ATC',
      options: {
        show: false,
        stroke: cesium.getColor('YELLOW'),
        strokeWidth: 2
      }
    },
    flightMovementsTower: {
      layerName: 'V_FlightMovementTower',
      displayName: 'FM Tower',
      options: {
        show: false,
        stroke: cesium.getColor('DARKVIOLET'),
        strokeWidth: 2
      }
    },
    flightMovementsScheduled: {
      layerName: 'V_FlightMovementScheduled',
      displayName: 'FM Scheduled (FPL)',
      options: {
        show: false,
        stroke: cesium.getColor('GREEN'),
        strokeWidth: 2
      }
    },
    flightMovementsI: {
      layerName: 'G_FlightMovement_I',
      displayName: 'Route Segment, Waypoint',
      options: {
      }
    }
  };

  /* @ngInject */
  constructor(private $scope: IMapScope, private $rootScope: ng.IRootScopeService, private $compile: ng.ICompileService, private configService: ConfigService,
    private $q: ng.IQService, private systemConfigurationService: SystemConfigurationService) {

    this.cesium = cesium;

    const container: string = 'cesiumContainer';
    const toolbarEl: string = '.cesium-viewer-toolbar';

    let imageryProvider = this.cesium.createOpenStreetMap(configService.get('BASE_LOCAL_URL'), 'base_map', 'Base Map');
    const sceneMode = this.cesium.getSceneMode(configService.get('SCENE_MODE')); // 2D, 3D or Columbus View (default)

    this.cesium.cesiumKey = configService.get('CESIUM_KEY');

    // base widget - contains all standard cesium widgets
    this.cesium.createViewer(container, {
      timeline: false,
      selectionIndicator: true,
      animation: false,
      fullscreenButton: false,
      baseLayerPicker: false,
      imageryProvider: imageryProvider,
      sceneMode: sceneMode
    });

    // ensure the base layer has a name so it displays in the template
    if (this.cesium.viewer.imageryLayers.get(0).imageryProvider.displayName) {
      this.cesium.viewer.imageryLayers.get(0).displayName = this.cesium.viewer.imageryLayers.get(0).imageryProvider.displayName;
    }

    // listening on airspaces or flight movements to display
    $scope.$on('map.flyToEntity', (broadcastedEvent: ng.IAngularEvent, type: MapFlyToEntityType, objectsToDisplay: any[]): void => {
      if (objectsToDisplay.length > 0) {
        $rootScope.$broadcast('contentModeChange', 'split-mode'); // changes map to split-mode viewing
      }

      switch (type) {
        case MapFlyToEntityType.AIRSPACES:
          this.showAirspaces(objectsToDisplay);
          break;
        case MapFlyToEntityType.SINGLE_AIRSPACE:
          this.showSingleAirspace(objectsToDisplay);
          break;
        case MapFlyToEntityType.SINGLE_FLIGHT_MOVEMENT:
        case MapFlyToEntityType.FLIGHT_MOVEMENT_INTERSECTION:
          this.showSingleFlightMovement(objectsToDisplay);
          break;
        case MapFlyToEntityType.SELECT_AIRSPACE:
          this.showSelectedAirspace(objectsToDisplay);
          break;
        case MapFlyToEntityType.SELECT_AIRSPACE_NAVDB:
          this.showSelectedAirspace(objectsToDisplay, true);
          break;
      }
    });

    $scope.$on('map.toggle', (broadcastedEvent: ng.IAngularEvent, type: string, objectsToDisplay: any[]): void => {
      switch (type) {
        case 'Layers Off':
          this.removeAll();
          $rootScope.$broadcast('contentModeChange', 'content-mode'); // changes map to content-mode since nothing will be showing
          break;
        default:
          $rootScope.$broadcast('contentModeChange', 'split-mode'); // changes map to split-mode viewing
      }
    });

    $scope.aerodromesLayerName = this.layers.aerodromes.layerName;
    $scope.flightDisplayed = false;
    $scope.useLocalBase = (imageryProvider !== null); // set up of checkbox
    $scope.layers = this.cesium.dataSources; // connects layers to dataSources of Cesium Controller
    $scope.imgLayers = this.cesium.imageryLayers; // connects layers to imagery layers of Cesium Controller
    $scope.changeLayerAlpha = (layer: ICesiumLayer) => this.cesium.changeLayerAlpha(layer);
    $scope.closeMapWindow = () => $rootScope.$broadcast('contentModeChange', 'content-mode');
    $scope.measureMap = () => this.cesium.toggleRuler();
    $scope.cesiumVersion = () => this.cesium.version;

    // replace home button functionality with own flyhome action
    // reason being, flyhome only works on 3D scene - https://cesiumjs.org/Cesium/Build/Documentation/Camera.html#flyHome
    angular.element(document.querySelectorAll(toolbarEl + ' .cesium-home-button')).bind('click', (event: JQueryEventObject) => {
      this.cesium.flyHome();
      event.preventDefault();
    });

    // appends buttons to the cesium toolbar
    angular.element(document.querySelectorAll(toolbarEl)).append($compile(MapButton)($scope));
    angular.element(document.querySelectorAll(toolbarEl)).append($compile(MapRulerButton)($scope));
    angular.element(document.querySelectorAll(toolbarEl)).append($compile(MapCloseButton)($scope));

    const evtStart = this.cesium.viewer.camera.moveStart.addEventListener(() => {
      this.setDefaultCamera();
      evtStart();
    });
  }

  private setDefaultCamera(): void {
    const west = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.MAP_WEST_LONGITUDE) || -180;
    const south = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.MAP_SOUTH_LATITUDE) || -90;
    const east = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.MAP_EAST_LONGITUDE) || 180;
    const north = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.MAP_NORTH_LATITUDE) || 90;
    this.cesium.setDefaultCameraPosition(west, south, east, north);
  }

  // display airspace(s)
  private showAirspaces(airspacesToDisplay: IAirspace[]): void {
    // airspacesNavDB
    const navdb: string = this.configService.get('GEOSERVER_ABMSDB'); // geoserver navdb url
    // remove previously loaded airspace layer
    this.cesium.removeDataSourceByName(this.layers.airspaces.layerName);
    // add all airsapces from geoserver abmsdb
    this.cesium.addLayer(navdb, null, this.layers.airspaces.layerName, this.layers.airspaces.displayName, this.layers.airspaces.options)
      .then((layer: ICesiumLayer) => this.showEntitiesAndFlyTo(layer));
  }

  private showSelectedAirspace(airspacesToDisplay: IAirspace[], isNavdb: boolean = false): void {

    // airspace to select identity
    const airspaceId = airspacesToDisplay[0].id;
    const airspaceIdent = airspacesToDisplay[0].airspace_name;
    const airspaceType = airspacesToDisplay[0].airspace_type;

    // current airsapce geoserver parameters
    const currentDb: string = this.configService.get('GEOSERVER_ABMSDB');
    const currentDbParams: string = `condition:id<>'${airspaceId}';`;

    // selected airsapce geoserver parameters
    let selectDb: string;
    let selectDbParams: string;
    let selectLayerNameDb: string;

    // determine based on provided setting (default to abmsdb geosever)
    if (isNavdb) {
      selectDb = this.configService.get('GEOSERVER_NAVDB');
      selectDbParams = airspaceType
        ? `where:IDENT='${airspaceIdent}' AND TYP='${airspaceType}';`
        : `where:IDENT='${airspaceIdent}';`;
      selectLayerNameDb = this.layers.airspacesNavDB.layerName;
    } else {
      selectDb = currentDb;
      selectDbParams = `condition:id='${airspaceId}';`;
      selectLayerNameDb = this.layers.airspaces.layerName;
    }

    let dbBase: string = this.configService.get('GEOSERVER_BASE_PT1');
    let selectWorkspace: string = isNavdb ? 'NAVDB' : 'ABMSDB';

    // remove previously loaded airspace layers
    this.cesium.removeImageryLayerByName(this.layers.airspaceCurrent.layerName);
    this.cesium.removeImageryLayerByName(this.layers.airspaceSelect.layerName);

    // add airsapce imagry layers from geoserver
    // we must use imagery layers as Cesium has issues rendering large polygons with fill and out lines near the polls
    this.cesium.addWMSLayer(dbBase, 'ABMSDB', this.layers.airspaceCurrent.layerName, this.layers.airspaceCurrent.displayName,
                            currentDbParams, this.layers.airspaces.layerName, this.layers.airspaceCurrent.layerName);
    this.cesium.addWMSLayer(dbBase, selectWorkspace, this.layers.airspaceSelect.layerName, this.layers.airspaceSelect.displayName,
                            selectDbParams, selectLayerNameDb, this.layers.airspaceSelect.layerName);

    // load layer features and fly to without displaying
    // required to calculate visible location of airspace in imagry layer
    this.$q.all([
      this.cesium.loadLayer(currentDb, null, this.layers.airspaceCurrent.layerName, this.layers.airspaceCurrent.options, currentDbParams, this.layers.airspaces.layerName),
      this.cesium.loadLayer(selectDb, null, this.layers.airspaceSelect.layerName, this.layers.airspaceSelect.options, selectDbParams, selectLayerNameDb)
    ]).then((layers: Array<ICesiumLayer>): void => this.flyToEntities(...layers));
  }

  // remove all loaded datasources & imagery layers
  private removeAll(): void {
    const ds = this.cesium.dataSourcesInstance;

    // remove any loaded map entities which were from datasources (geojson)
    for (var i = ds.length - 1; i >= 0; i--) {
      ds.remove(ds.get(i), true);
    }
    // remove any loaded map layers which where from imagery providers (WMS)
    for (var i = this.cesium.viewer.imageryLayers.length - 1; i >= 0; i--) {
      let imgLayer = this.cesium.viewer.imageryLayers.get(i);
      // only imagery layer that is not the base map layer
      if (imgLayer.imageryProvider.layerName !== 'base_map') {
        this.cesium.viewer.imageryLayers.remove(imgLayer);
      }
    }
  }

  // routeSegment layer should only return segments that are used for billing
  private viewParamsForSegmentLayer(fmID: number, enrouteChargeBasis: string): string {
    const segmentTypes = ['SCHED', 'RADAR', 'ATC', 'TOWER', 'USER', 'NOMINAL'];

    // use the flight movement's enroute charge basis to match to an expected segment type
    const segmentType = enrouteChargeBasis
      ? segmentTypes.find((sT: string) => enrouteChargeBasis.toLowerCase().includes(sT.toLowerCase()))
      : null;

    // pass segment type to geoserver to return filtered route segments by segment type
    return `i_condition:flight_movement='${fmID}'${segmentType ? ` AND segment_type='${segmentType}'` : ``};`;
  }

  // todo does not flyTo
  private showSingleAirspace(airspacesToDisplay: IAirspace[]): void {
    // airspacesNavDB
    const airspaceID = airspacesToDisplay[0].id;
    const viewParams = `condition:id='${airspaceID}';`;
    const abmsdb: string = this.configService.get('GEOSERVER_ABMSDB'); // geoserver navdb url
    // remove previously loaded airspace layer
    this.cesium.removeDataSourceByName(this.layers.airspaces.layerName);
    // add airsapce from geoserver abmsdb
    this.cesium.addLayer(abmsdb, null, this.layers.airspaces.layerName, this.layers.airspaces.displayName, this.layers.airspaces.options, viewParams)
      .then((layer: ICesiumLayer) => this.showEntitiesAndFlyTo(layer));
  }

  /**
   * Loads and formats a single flight movement display and fly to.
   *
   * @param flightMovementsToDisplay flight movement to display
   */
  private showSingleFlightMovement(flightMovementsToDisplay: IFlightMovement[]): void {

    // clear out any previous flight movement loaded flag
    this.$scope.flightMovementLoaded = false;

    const cesiumBase: string = this.configService.get('GEOSERVER_BASE_PT1');
    const fmID = flightMovementsToDisplay[0].id;
    const abmsdb: string = this.configService.get('GEOSERVER_ABMSDB'); // geoserver navdb url
    const viewParams = `where:id='${fmID}';`;
    const viewParamsCond = `i_condition:flight_movement='${fmID}';`;
    const enrouteChargeBasis = flightMovementsToDisplay[0].enroute_charges_basis;
    const viewParamsForSegmentLayer = this.viewParamsForSegmentLayer(fmID, enrouteChargeBasis);

    // airspaces WMS Layer
    // load and display the imagery layer if it has not yet already been loaded
    // this layer never changes so it only needs to be loaded once
    if (!this.cesium.getImageryLayersByLayerName(this.layers.airspaces.layerName)) {
      this.cesium.addWMSLayer(cesiumBase, 'ABMSDB', this.layers.airspaces.layerName, this.layers.airspaces.displayName, viewParamsCond);
    }

    // aerodromes WFS Layer
    // load and display the imagery layer if it has not yet already been loaded
    // this layer never changes so it only needs to be loaded once
    if (!this.cesium.getDataSourceByLayerName(this.layers.aerodromes.layerName)) {
      this.cesium.addLayer(abmsdb, null, this.layers.aerodromes.layerName, this.layers.aerodromes.displayName, this.layers.aerodromes.options, viewParams)
        .then((layer: ICesiumLayer) => {
          const that = this;
          const entities = layer.entities.values;
          /**
           * Shortcut method to create the new label and pin billboard entity
           * @param ent Cesium entity
           * @param pin Generated pin marker image
           */
          const updateEnt = (ent: any = {}, pin: any): any => {
            ent.billboard.image = pin;
            ent.label = new that.cesium.mainCesium.LabelGraphics({
              text: ent.name,
              font: '13px sans-serif',
              fillColor: cesium.getColor('BLACK'),
              horizontalOrigin: that.cesium.mainCesium.HorizontalOrigin.LEFT,
              pixelOffset: new that.cesium.mainCesium.Cartesian2(15, -5),
              pixelOffsetScaleByDistance: new that.cesium.mainCesium.NearFarScalar(1.4e9, 0.3, 1.5e5, 0.4),
              translucencyByDistance: new that.cesium.mainCesium.NearFarScalar(1.0e6, 1.0, 1.3e6, 0.0)
            });
          };

          // iterate over each aerodrome entity to update its image
          for (var i = entities.length - 1; i >= 0; i--) {
            let ent = entities[i];
            // check for the an existing custom pin image
            if (!this.cesium.customPins.get('airport')) {
              // create the custom pin image
              this.cesium.labelMaker('airport', window.location.pathname + 'assets/images/map/airport_30x30.png', cesium.getColor('CORNFLOWERBLUE'))
                .then((pin: string) => {
                  // the pin image is not generated, update the entity image now with it
                  updateEnt(ent, pin);
                });
            } else {
              // the pin image already exists, go ahead and update the entity image with it
              updateEnt(ent, this.cesium.customPins.get('airport'));
            }

          }
        });
    }

    // cancel pending flight movement request, only one flight movement can be disabled at one time
    if (this.flightMovementRequest !== null && this.flightMovementRequest.pending()) {
      this.flightMovementRequest.cancel();
    }

    // flight Movement WFS Layers
    // ensure the previously loaded dataSource layers are removed
    this.cesium.removeDataSourceByName(this.layers.flightMovementsRadar.layerName);
    this.cesium.removeDataSourceByName(this.layers.flightMovementsATC.layerName);
    this.cesium.removeDataSourceByName(this.layers.flightMovementsTower.layerName);
    this.cesium.removeDataSourceByName(this.layers.flightMovementsScheduled.layerName);
    this.cesium.removeDataSourceByName(this.layers.flightMovementsInsideSP.layerName);
    this.cesium.removeDataSourceByName(this.layers.flightMovementsOutsideSP.layerName);
    this.cesium.removeDataSourceByName(this.layers.routeSegments.layerName);
    this.cesium.removeDataSourceByName('waypoints');

    // build single flight movement cancelable promise request
    this.flightMovementRequest = Cancellable.promise(this.$q.all([
        this.cesium.addLayer(abmsdb, null, this.layers.flightMovementsRadar.layerName, this.layers.flightMovementsRadar.displayName,
          this.layers.flightMovementsRadar.options, viewParams),
        this.cesium.addLayer(abmsdb, null, this.layers.flightMovementsATC.layerName, this.layers.flightMovementsATC.displayName,
          this.layers.flightMovementsATC.options, viewParams),
        this.cesium.addLayer(abmsdb, null, this.layers.flightMovementsTower.layerName, this.layers.flightMovementsTower.displayName,
          this.layers.flightMovementsTower.options, viewParams),
        this.cesium.addLayer(abmsdb, null, this.layers.flightMovementsScheduled.layerName, this.layers.flightMovementsScheduled.displayName,
          this.layers.flightMovementsScheduled.options, viewParams),
        this.cesium.addLayer(abmsdb, null, this.layers.flightMovementsInsideSP.layerName, this.layers.flightMovementsInsideSP.displayName,
          this.layers.flightMovementsInsideSP.options, viewParams),
        this.cesium.addLayer(abmsdb, null, this.layers.flightMovementsOutsideSP.layerName, this.layers.flightMovementsOutsideSP.displayName,
          this.layers.flightMovementsOutsideSP.options, viewParams),
        this.cesium.addLayer(abmsdb, null, this.layers.routeSegments.layerName, this.layers.routeSegments.displayName,
          this.layers.routeSegments.options, viewParamsForSegmentLayer)
      ]));

    // invoke promise and format single flight movement display if successful
    // if error and layers supplied, remove layers from viewer
    this.flightMovementRequest.promise
      // format single flight movement layers
      .then((layers: Array<ICesiumLayer>) => this.formatSingleFlightMovement(layers, enrouteChargeBasis))
      // remove any flight movement layers loaded
      .catch((exception: ICancellablePromiseException) => {
        console.error('Could not format single flight movment layers: ', exception);
        this.removeSingleFlightMovement(exception.response);
      });
  }

  private flyToEntities(...layers: Array<ICesiumLayer>): void {

    // list of entities to fly to
    let flytoEntities: Array<any> = [];

    // loop through each layer and get entities that are defined
    layers.forEach((layer: ICesiumLayer) => {
      if (layer && layer.entities && layer.entities.values) {
        flytoEntities = flytoEntities.concat(layer.entities.values);
      }
    });

    // change ma viewport to layer entities
    this.cesium.flyToEntities(flytoEntities);
  }

  private showEntitiesAndFlyTo(...layers: Array<ICesiumLayer>): void {

    // show all layer entities
    layers.forEach((layer: ICesiumLayer) => {
      this.cesium.showEntitiesByDataSource(layer.layerName, null, false);
    });

    // change map viewport to layer entities
    this.flyToEntities(...layers);
  }

  private displayLayer(layer: ICesiumLayer): boolean {
    if (layer.entities.values[0].polyline) {
      this.cesium.showEntitiesByDataSource(layer.layerName, null, false);
      return true;
    } else {
      this.cesium.removeDataSourceByName(layer.layerName);
      return false;
    }
  }

  /**
   * Format single flight movemet in cesium view.
   */
  private formatSingleFlightMovement(layers: Array<ICesiumLayer>, enrouteChargeBasis: string): void {

    // loop through each added layer and display
    // if inside/outside billing layers, skip and use route segments instead
    let routeLayer: ICesiumLayer = null;
    layers.forEach((layer: ICesiumLayer) => {

      switch (layer.layerName) {
        case this.layers.flightMovementsInsideSP.layerName:
        case this.layers.flightMovementsOutsideSP.layerName:
          break;
        case this.layers.routeSegments.layerName:
          routeLayer = layer;
          break;
        default:
          this.displayLayer(layer);
          break;
      }
    });

    // if no 'not billable' portion, remove the layer
    let notBillableDS = this.cesium.getDataSourceByLayerName(this.layers.flightMovementsOutsideSP.layerName);
    if (!notBillableDS.entities.values[0] || !notBillableDS.entities.values[0].polyline) {
      notBillableDS = null;
      this.cesium.removeDataSourceByName(this.layers.flightMovementsOutsideSP.layerName);
    }

    // parse and resolve route segment layer as billable layer with waypoints if exists
    let that = this;
    this.cesium.showEntitiesByDataSource(routeLayer.layerName, null, false);

    // create a new data custom source to manage the new custom waypoints
    let customWaypointsLayer = new this.cesium.mainCesium.CustomDataSource('Custom Waypoints');

    // set the usual map route layer properties
    customWaypointsLayer.displayName = 'Waypoints';
    customWaypointsLayer.layerName = 'waypoints';
    customWaypointsLayer.cssValue = cesium.getCSSColor(cesium.getColor('ORANGE'));

    // billable route segments
    const trueBillableDS = this.cesium.getDataSourceByLayerName(this.layers.flightMovementsInsideSP.layerName);
    const trueBillableEnts = trueBillableDS.entities.values;
    const firstBillableEnt = trueBillableEnts[0];

    // if no 'billable' portion, show all source layers instead
    if (!trueBillableDS.entities.values[0] || !trueBillableDS.entities.values[0].polyline) {
      this.cesium.toggleDataSourceLayers([
        this.layers.flightMovementsATC.layerName,
        this.layers.flightMovementsRadar.layerName,
        this.layers.flightMovementsScheduled.layerName,
        this.layers.flightMovementsTower.layerName
      ]);
    }

    // not billable route segments
    const notBillableEnts = notBillableDS ? notBillableDS.entities.values : null;
    const firstNotBillableEnt = notBillableEnts ? notBillableEnts[0] : null;
    const lastNotBillableEnt = notBillableEnts ? notBillableEnts[notBillableEnts.length - 1] : null;
    const firstNotBillableEntPosition = firstNotBillableEnt && firstNotBillableEnt.polyline
     ? firstNotBillableEnt.polyline.positions.getValue()[0] : null;
    const lastNotBillableEntPosition = lastNotBillableEnt && lastNotBillableEnt.polyline
     ? lastNotBillableEnt.polyline.positions.getValue()[lastNotBillableEnt.polyline.positions.getValue().length - 1] : null;

    // all segments of enrouteChargeBasis type (ie. radar-summary)
    const segEntities = routeLayer.entities.values;
    const lastSegment = segEntities[segEntities.length - 1];

    // radar route (used when enrouteChargeBasis is radar-summary and flight is international)
    let firstbillableRadarRouteText: string = this.propertyValueOf(firstBillableEnt, 'radar_route_text');
    const radarRouteList = firstbillableRadarRouteText ? firstbillableRadarRouteText.split(' ') : null;
    const radarRouteFirstWaypoint = radarRouteList ? radarRouteList[0] : null;
    const radarRouteFinalWaypoint = radarRouteList ? radarRouteList[radarRouteList.length - 1] : null;

    const movementType = this.propertyValueOf(trueBillableDS.entities.values[0], 'movement_type');

    // if there are no returned route segments
    if (segEntities.length === 0) {
      // hide the route segments route layer as there is nothing to show
      // that.cesium.getDataSourceByLayerName(this.layers.routeSegments.layerName).show = false;
      this.cesium.removeDataSourceByName(this.layers.routeSegments.layerName);
    }

    for (let i = segEntities.length - 1; i >= 0; i--) {
      const currentSegment = segEntities[i];

      // skip if segment polyline value does not exists
      if (!currentSegment.polyline) {
        continue;
      }

      const id = this.propertyValueOf(currentSegment, 'id');
      const startlabel = this.propertyValueOf(currentSegment, 'startlabel');
      const endlabel = this.propertyValueOf(currentSegment, 'endlabel');
      const segment_number = this.propertyValueOf(currentSegment, 'segment_number');

      const firstCurrentSegmentPosition = currentSegment.polyline.positions.getValue()[0];
      const secondCurrentSegmentPosition = currentSegment.polyline.positions.getValue()[1];

      // flight movement: destination inside FIR (arrival)
      if (movementType === 'INT_ARRIVAL' || movementType === 'REG_ARRIVAL') {
        // this is a arrival flight movement, the last route segement will be the ending segment
        // verify if the last segements endlabel matches the billable exit point of the flight movement. IT SHOULD MATCH
        // verify if the the segment number property matches the total number of segments to indicate it is the last segement.
        if (endlabel === this.propertyValueOf(firstBillableEnt, 'billable_exit_point') && segment_number === segEntities.length) {
          // we can assume that the this is the ending of the route segement and therefore
          // the ending segment should be used for the destination (arrival)
          //          0   1  (2)
          //  depart --- --- ---> destination (use this end)
          // use the last position point in the segment polyline as the destination point position

          // make the end point (red arrival/dest)(inside FIR)
          makePointEntity(
            {
              id: id + 'e',
              label: endlabel,
              position: lastSegment.polyline.positions.getValue()[1],
              point: i
            },
            { pointColor: 'RED', visible: movementType === 'REG_ARRIVAL' },
            customWaypointsLayer,
            that
          );

          if (enrouteChargeBasis.includes('radar')) {
            makePointEntity(
              {
                id: id + 's',
                label: notBillableEnts ? radarRouteFirstWaypoint : startlabel,
                position: notBillableEnts ? firstNotBillableEntPosition : firstCurrentSegmentPosition,
                point: i
              },
              { pointColor: 'ORANGE', visible: true },
              customWaypointsLayer,
              that
            );
          } else {
            // make the start point (green departure)(outside FIR)
            makePointEntity(
              {
                id: id + 's',
                label: notBillableEnts ? this.propertyValueOf(firstNotBillableEnt, 'dep_ad') : startlabel,
                position: notBillableEnts ? firstNotBillableEntPosition : firstCurrentSegmentPosition,
                point: i
              },
              { pointColor: 'GREEN', visible: true },
              customWaypointsLayer,
              that
            );
          }
        }

        /* **middle point creation** */
        middlePointCreation(segEntities, customWaypointsLayer, i, that);

        // flight movement: destination outside FIR (departure)
      } else if (movementType === 'INT_DEPARTURE' || movementType === 'REG_DEPARTURE') {
        // this is a departure flight movement, the first route segment will be the starting segment.
        // verify the start label with the billable entry point. THEY SHOULD MATCH.
        // verify the segment number property as it should be 1 which indicates its the first segment
        if (segment_number === 1 && startlabel === this.propertyValueOf(firstBillableEnt, 'billable_entry_point')) {
          if (enrouteChargeBasis.includes('radar')) {
            makePointEntity(
              {
                id: id + 'e',
                label: notBillableEnts ? radarRouteFinalWaypoint : endlabel,
                position: notBillableEnts ? lastNotBillableEntPosition : secondCurrentSegmentPosition,
                point: i
              },
              { pointColor: 'ORANGE', visible: true },
              customWaypointsLayer,
              that
            );
          } else {
            // we can assume that the this is the start of the route segement and therefore
            // the starting segment should be used for the departure
            //                       (0)  1   2
            // (use this end) depart --- --- ---> destination
            // use the first position point in the segment polyline as the departure point position

            // make the end point (red arrival/destination) (outside FIR)
            makePointEntity(
              {
                id: id + 'e',
                label: notBillableEnts ? this.propertyValueOf(firstNotBillableEnt, 'dest_ad') : endlabel,
                position: notBillableEnts ? lastNotBillableEntPosition : secondCurrentSegmentPosition,
                point: i
              },
              { pointColor: 'RED', visible: true },
              customWaypointsLayer,
              that
            );
          }

          // make the start point (green departure) (inside FIR)
          makePointEntity(
            {
              id: id + 's',
              label: startlabel,
              position: firstCurrentSegmentPosition,
              point: i
            },
            { pointColor: 'GREEN', visible: movementType === 'REG_DEPARTURE' },
            customWaypointsLayer,
            that
          );
        }

        /* **middle point creation** */
        middlePointCreation(segEntities, customWaypointsLayer, i, that);

        // flight movement: destination & departure outside FIR
      } else if (movementType === 'OVERFLIGHT') {

        if (enrouteChargeBasis.includes('radar')) {
          if (segment_number === 1) {

            if (radarRouteFirstWaypoint !== startlabel) {
              // start
              makePointEntity(
                {
                  id: id + 'e',
                  label: radarRouteFirstWaypoint,
                  position: notBillableEnts ? firstNotBillableEntPosition : firstCurrentSegmentPosition,
                  point: i
                },
                { pointColor: 'ORANGE', visible: true },
                customWaypointsLayer,
                that
              );
            }

            if (radarRouteFinalWaypoint !== this.propertyValueOf(lastSegment, 'endlabel') && radarRouteFinalWaypoint !== radarRouteFirstWaypoint) {
              // end
              makePointEntity(
                {
                  id: id + 'e',
                  label: radarRouteList.length > 1 ? radarRouteFinalWaypoint : '',
                  position: notBillableEnts ? lastNotBillableEntPosition : secondCurrentSegmentPosition,
                  point: i

                },
                { pointColor: 'ORANGE', visible: true },
                customWaypointsLayer,
                that
              );
            }
          }

        } else if (startlabel === this.propertyValueOf(firstBillableEnt, 'billable_entry_point') && segment_number === 1) {
          // if the segments start label matches the billable entry point
          // then the first point in the first flight movement outside (not billed) first position point is the departure (start point)
          // and the last flight movement outside (not billed) last position point is the destination (end point)

          // start
          makePointEntity(
            {
              id: id + 's',
              label: notBillableEnts ? this.propertyValueOf(firstNotBillableEnt, 'dep_ad') : startlabel,
              position: notBillableEnts ? firstNotBillableEntPosition : firstCurrentSegmentPosition,
              point: i
            },
            { pointColor: 'GREEN', visible: true },
            customWaypointsLayer,
            that
          );

          // end
          makePointEntity(
            {
              id: id + 'e',
              label: notBillableEnts ? this.propertyValueOf(firstNotBillableEnt, 'dest_ad') : endlabel,
              position: notBillableEnts ? lastNotBillableEntPosition : secondCurrentSegmentPosition,
              point: i
            },
            { pointColor: 'RED', visible: true },
            customWaypointsLayer,
            that
          );
        }

        /* **middle point creation** */
        middlePointCreation(segEntities, customWaypointsLayer, i, that);

      } else if (movementType === 'DOMESTIC') {

        if (segment_number === 1) {

          // make the start point (green departure)(inside FIR)
          let firstSegmentStartLabel: string = this.propertyValueOf(currentSegment, 'startlabel');
          makePointEntity(
            {
              id: id + 's',
              label: firstSegmentStartLabel === 'ZZZZ' || firstSegmentStartLabel === 'AFIL'
                ? this.propertyValueOf(firstBillableEnt, 'item18_dep') : firstSegmentStartLabel,
              position: currentSegment.polyline.positions.getValue()[0],
              point: i
            },
            { pointColor: 'GREEN', visible: true },
            customWaypointsLayer,
            that
          );

        } else if (segment_number === segEntities.length) {

          // we can assume that the this is the ending of the route segement and therefore
          // the ending segment should be used for the destination (arrival)
          //          0   1  (2)
          //  depart --- --- ---> destination (use this end)
          // use the last position point in the segment polyline as the destination point position

          // make the end point (red arrival/dest)(inside FIR)
          makePointEntity(
            {
              id: id + 'e',
              label: endlabel === 'ZZZZ' ? this.propertyValueOf(firstBillableEnt, 'arrival_ad') : endlabel,
              position: currentSegment.polyline.positions.getValue()[1],
              point: i
            },
            { pointColor: 'RED', visible: true },
            customWaypointsLayer,
            that
          );
        }

        /* **middle point creation** */
        middlePointCreation(segEntities, customWaypointsLayer, i, that);

      } else {
        // flight movement: unknown waypoints, movement_type === 'OTHER'

        // if we are at the end of the segments then also add the end waypoint
        if (segment_number === segEntities.length) {
          makePointEntity(
            {
              id: id + 'e',
              label: endlabel,
              position: secondCurrentSegmentPosition,
              point: i
            },
            { pointColor: 'RED', visible: true },
            customWaypointsLayer,
            that
          );
        } else if (i === 0) {
          // add the waypoint from the start of the segments
          makePointEntity(
            {
              id: id + 's',
              label: startlabel,
              position: firstCurrentSegmentPosition,
              point: i
            },
            { pointColor: 'GREEN', visible: true },
            customWaypointsLayer,
            that
          );
        }

        /* **middle point creation** */
        middlePointCreation(segEntities, customWaypointsLayer, i, that);

      } // end if
    } // end for loop

    if (segEntities.length > 0) {
      // add the list of waypoints to the new custom waypoint datasource instance which will then add to the map and display
      that.cesium.viewer.dataSources.add(customWaypointsLayer);
    } else {
      customWaypointsLayer = null;
    }

    // remove unnessary inside layer as route segments replaces it
    this.cesium.removeDataSourceByName(this.layers.flightMovementsInsideSP.layerName);

    // remove and readd outside layer if it exists
    // this is necessary so route segments is displayed overtop
    if (notBillableDS) {
      this.cesium.removeDataSourceByName(this.layers.flightMovementsOutsideSP.layerName);
      this.cesium.addDataSource(notBillableDS);
    }

    // sort data source by display name
    this.cesium.sortDateSourceByDisplayName();

    // update flight movement loaded flag
    this.$scope.flightMovementLoaded = true;

    // fly display to newly added layers that are visible
    this.flyToEntities(...layers.filter((layer: ICesiumLayer) => layer.show));
  }

  /**
   * Removes flight movement layers from cesium view.
   */
  private removeSingleFlightMovement(layers: Array<ICesiumLayer>): void {
    if (layers) {
      for (const layer of layers) {
        this.cesium.removeDataSourceLayer(layer);
      }
    }
  }

  /**
   * Entiry property values are no longer raw values as of Cesium v1.32. This checks
   * the entitry properties for the value safely with proper null checks and then 
   * returns the value if found, else returns null.
   */
  private propertyValueOf(entity: any, name: string): any {
    return entity && entity.properties && entity.properties[name] && entity.properties[name].valueOf()
      ? entity.properties[name].valueOf() : null;
  }
}
