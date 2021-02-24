// interface
import { IFlightStatusExemptionType } from '../flight-status-exemptions.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'exempt-flight-status';

export class FlightStatusExemptionsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IFlightStatusExemptionType = {
    id: null,
    aerodrome_fees_exempt: 0,
    flight_item_type: null,
    flight_item_value: null,
    late_departure_fees_exempt: 0,
    parking_fees_are_exempt: 0,
    enroute_fees_are_exempt: 0,
    approach_fees_exempt: 0,
    late_arrival_fees_exempt: 0,
    international_pax: 0,
    domestic_pax: 0,
    extended_hours: 0,
    flight_notes: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  listFlightTypes(): Array<IStaticType> {
    return [
      {
        name: 'Item 18 - Statuses',
        value: 'ITEM18-STS'
      },
      {
        name: 'Item 8 - Type',
        value: 'ITEM8-TYPE'
      },
      {
        name: 'Item 18 - RMK',
        value: 'ITEM18-RMK'
      }
    ];
  }

  listItem8Codes(): Array<IStaticType> {
    return [
      {
        name: 'General Aviation',
        value: 'G'
      },
      {
        name: 'Military',
        value: 'M'
      },
      {
        name: 'Non-scheduled Air Service',
        value: 'N'
      },
      {
        name: 'Other',
        value: 'X'
      },
      {
        name: 'Scheduled Air Service',
        value: 'S'
      }
    ];
  }

  listSTSCodes(): Array<IStaticType> {
    return [
      {
        name: 'Altitude Reservation',
        value: 'ALTRV'
      },
      {
        name: 'Approved by ATS Authorities',
        value: 'ATFMX'
      },
      {
        name: 'Firefighting',
        value: 'FFR'
      },
      {
        name: 'Hazardous Material',
        value: 'HAZMAT'
      },
      {
        name: 'Head of State',
        value: 'HEAD'
      },
      {
        name: 'Humanitarian Mission',
        value: 'HUM'
      },
      {
        name: 'Medical Evacuation',
        value: 'MEDEVAC'
      },
      {
        name: 'Medical Flight',
        value: 'HOSP'
      },
      {
        name: 'Military Authority',
        value: 'MARSA'
      },
      {
        name: 'Military, Customs or Police',
        value: 'STATE'
      },
      {
        name: 'Navaid Calibration',
        value: 'FLTCK'
      },
      {
        name: 'Non-RVSM Capable Flight',
        value: 'NONRVSM'
      },
      {
        name: 'Search and Rescue',
        value: 'SAR'
      }
    ];
  }

}
