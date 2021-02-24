// interface
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IUnspecifiedDepartureLocationType } from '../unspecified-dep-dest-locations/unspecified-dep-dest-locations.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IRepositioningAerodromeCluster {
    id?: number;
    repositioning_aerodrome_cluster_name: string;
    enroute_fees_are_exempt: number;
    approach_fees_are_exempt: number;
    aerodrome_fees_are_exempt: number;
    late_arrival_fees_are_exempt: number;
    late_departure_fees_are_exempt: number;
    parking_fees_are_exempt: number;
    international_pax: number;
    domestic_pax: number;
    extended_hours: number;
    aerodrome_identifiers: Array<string>;
    flight_notes: string;
}

export interface IRepositioningAerodromeClustersScope {
    aerodromesList: Array<IAerodrome>;
    unknownAerodromesList: Array<IUnspecifiedDepartureLocationType>;
    unknownAerodromesModel: Array<IUnspecifiedDepartureLocationType>
    aerodromesModel: Array<IAerodrome>;
    enteredAerodromes: string|Array<string>;
    createOverride: Function;
    updateOverride: Function;
    refreshOverride: Function;
    reset: Function;
    populateDropdowns: Function;
    editable: IRepositioningAerodromeCluster;
    filterParameters: object;
    textFilter: string;
    pagination: ISpringPageableParams;
    getSortQueryString: () => string;
    shouldShowCharge: Function;
}
