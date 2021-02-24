// interfaces
import { IFlightReassignment } from '../flight-reassignment.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'flight-reassignments';

export class FlightReassignmentService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IFlightReassignment = {
    id: null,
    applies_to_type_arrival: false,
    applies_to_type_departure: false,
    applies_to_type_domestic: false,
    applies_to_type_overflight: false,
    applies_to_scope_domestic: false,
    applies_to_scope_regional: false,
    applies_to_scope_international: false,
    applies_to_nationality_national: false,
    applies_to_nationality_foreign: false,
    identification_type: null,
    identifier_text: null,
    aerodrome_identifiers: [],
    start_date: null,
    end_date: null,
    account: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
