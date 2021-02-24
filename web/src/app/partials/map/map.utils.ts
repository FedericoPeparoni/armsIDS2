import { ISegment, ISegmentOptions } from './map.interface';

/**
 * Util to create point entities easily and add to the customWaypointsLayer collection
 * @param label Text to use for the label
 * @param pos Cartesian3 position to use
 * @param pointColor Display color to use
 * @param visible To display it once loaded
 * @param i The integer used to created unique Ids and names
 */

const PIXEL_OFFSET = [15, -5];
const PIXEL_OFFSET_SCALE_BY_DISTANCE = [1.4e9, 0.3, 1.5e5, 0.4];
const TRANSLUCENCY_BY_DISTANCE = [1.0e6, 1.0, 1.3e6, 0.0];

export const makePointEntity = (segment: ISegment, options: ISegmentOptions, customWaypointsLayer: any, that: any): void => {
    const { id, label, position, point } = segment;
    // visible defaults to true
    const { pointColor, visible = true } = options;

    const aerodromeNames = that.cesium.getEntityValuesByLayerName(that.layers.aerodromes.layerName).map((aerodrome: any) => aerodrome.name);

    // skip if waypoint is already in the customWaypointsLayer
    if (
        customWaypointsLayer.entities.values.find((entity: any) => entity.name.endsWith(label))
        || customWaypointsLayer.entities.getById('waypoint_' + point + '_' + label + '_' + id)
    ) {
        return;
    };

    customWaypointsLayer.entities.add({
        id: 'waypoint_' + point + '_' + label + '_' + id,
        name: 'waypoint_' + point + '_' + label,
        position: position,
        point: {
            color: that.cesium.getColor(pointColor),
            outlineColor: that.cesium.getColor('WHITE'),
            pixelSize: 8
        },
        label: {
            text: label,
            font: '13px sans-serif',
            fillColor: that.cesium.getColor('BLACK'),
            horizontalOrigin: that.cesium.mainCesium.HorizontalOrigin.LEFT,
            pixelOffset: new that.cesium.mainCesium.Cartesian2(...PIXEL_OFFSET),
            pixelOffsetScaleByDistance: new that.cesium.mainCesium.NearFarScalar(...PIXEL_OFFSET_SCALE_BY_DISTANCE),
            translucencyByDistance: new that.cesium.mainCesium.NearFarScalar(...TRANSLUCENCY_BY_DISTANCE),
            show: (aerodromeNames.includes(label) || label === 'ZZZZ') ? false : visible
        }
    });
};

export const middlePointCreation = (segments: any, customWaypointsLayer: any, i: number, that: any): void => {
    const { id, startlabel, endlabel, segment_number } = segments[i].properties;
    const polyline = segments[i].polyline;
    const segmentOptions = { pointColor: 'ORANGE', visible: true };
    const nextSegment = segments[i + 1];

    const customWaypointByStartlabel = customWaypointsLayer.entities.getById(
        'waypoint_' + i + '_' + startlabel + '_' + id + 's'
    );
    const customWaypointByEndlabel = customWaypointsLayer.entities.getById(
        'waypoint_' + i + '_' + endlabel + '_' + id + 'e'
    );

    const polylinePosition1 = polyline.positions.getValue()[0];
    const polylinePosition2 = polyline.positions.getValue()[1];

    // if we are at the end of the segments then also add the end waypoint
    if (segment_number === segments.length && segments.length > 1 && !customWaypointByEndlabel) {
        makePointEntity(
            {
                id: id + 'e',
                label: endlabel,
                position: polylinePosition2,
                point: i
            },
            segmentOptions,
            customWaypointsLayer,
            that
        );
    }

    // if the route segments are split (crossing FIR), add the other waypoint to show crossing
    if (nextSegment && (!nextSegment.polyline || !polylinePosition2.equals(nextSegment.polyline.positions.getValue()[0]))) {
        makePointEntity(
            {
                id: id + 'e',
                label: endlabel,
                position: polylinePosition2,
                point: i
            },
            segmentOptions,
            customWaypointsLayer,
            that
        );
    }

    // if the point doesn't match any in the current set that could already exist
    if (!customWaypointByStartlabel) {
        // add the middle waypoints from the start of the segments
        makePointEntity(
            {
                id: id + 's',
                label: startlabel,
                position: polylinePosition1,
                point: i
            },
            segmentOptions,
            customWaypointsLayer,
            that
        );
    }

    // if there is only one segment, make sure that its not the same as the end
    if (segments.length === 1 && !polylinePosition2.equals(customWaypointByEndlabel)) {
        makePointEntity(
            {
                id: id + 'e',
                label: endlabel,
                position: polylinePosition2,
                point: i
            },
            segmentOptions,
            customWaypointsLayer,
            that
        );
    }
};
