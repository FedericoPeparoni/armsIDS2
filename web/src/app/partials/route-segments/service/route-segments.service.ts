// interface
import { IRouteSegment } from '../route-segments.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint = '/route-segments';

export class RouteSegmentsService extends CRUDService {

    protected restangular: restangular.IService;

    private _mod: IRouteSegment = {
        id: null,
        flight_record_id: null,
        segment_type: null,
        segment_number: null,
        location: null,
        segment_start_label: null,
        segment_length: null,
        segment_cost: null,
        destination: null
    };


    /** @ngInject */
    constructor(protected Restangular: restangular.IService) {
        super(Restangular, endpoint);
        this.model = angular.copy(this._mod);
    }

    public getRouteSegmentsByTypeAndFlightMovementId(id: number, segmentType: string): ng.IPromise<any> {
        return this.restangular.one(`${endpoint}/find-by-segmenttype-and-flightid/${segmentType}/${id}`).get();
    }
}
