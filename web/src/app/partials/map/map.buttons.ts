export const MapButton = `<button type="button" id="cesium_layers_button" ng-click="showDetails = !showDetails"
      class="cesium-button cesium-toolbar-button"
      title="Cesium v{{ cesiumVersion() }}">
        <i class="fa fa-cog"></i>
      </button>`;

export const MapCloseButton = `<button type="button" id="cesium_close_button" ng-click="closeMapWindow()"
    class="cesium-button cesium-toolbar-button" title="{{ 'Close Map' | translate }}">
      <i class="fa fa-close"></i>
    </button>`;

export const MapRulerButton = `<button type="button" id="cesium_close_button" ng-click="measureMap()"
    class="cesium-button cesium-toolbar-button" title="{{ 'Map Measurment Tool' | translate }}">
    <i class="fa fa-fw icon-ruler">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64.73 64.7">
    <defs><style>.cls-1{fill:#fff;}</style></defs><title>icon-ruler</title><path class="cls-1"
    d="M68.72,21.58l-15-15a3.07,3.07,0,0,0-4.24,0L5.75,50.36a3,3,0,0,0,0,4.24l15,15a3,3,0,0,0,4.24,0L68.72,
    25.82A3,3,0,0,0,68.72,21.58ZM22.85,66,9.28,52.48l2.53-2.53,4.86,4.86A2,2,0,0,0,19.5,52l-4.86-4.86,2.65-2.65,
    9.28,9.28a2,2,0,0,0,2.83-2.83l-9.28-9.28,3-3L28,43.49a2,2,0,0,0,2.83-2.83L26,35.8l2.65-2.65,9.28,9.28a2,2,0,0,
    0,2.83-2.83l-9.28-9.28,3-3,4.86,4.86a2,2,0,0,0,2.83-2.83l-4.86-4.86,2.65-2.65,9.37,9.37a2,2,0,0,0,2.83-2.83L42.75,
    19l2.92-2.92L50.53,21a2,2,0,0,0,2.83-2.83l-4.86-4.86,3.13-3.13L65.19,23.7Z" transform="translate(-4.87 -5.75)"/></svg>
    </i>
  </button>`;
