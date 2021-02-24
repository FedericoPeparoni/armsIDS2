// service
import { EnumService } from '../../../angular-ids-project/src/helpers/services/enum.service';

export enum FlightType {
  DOMESTIC = <any>'Domestic',
  REG_OVERFLIGHT = <any>'Regional Overflight',
  REG_ARRIVAL = <any>'Regional Arrival',
  REG_DEPARTURE = <any>'Regional Departure',
  INT_OVERFLIGHT = <any>'International Overflight',
  INT_ARRIVAL = <any>'International Arrival',
  INT_DEPARTURE = <any>'International Departure',
  OTHER = <any>'Other',
  // this is only for KCAA only - INT_OVERFLIGHT = <any>'Overflight'
}

export class FlightTypeService extends EnumService {

  /** @ngInject */
  constructor(private $q: ng.IQService) {
    super();
    this.$q = $q;
  }

  list(): any {
    return super.list(FlightType);
  }
}
