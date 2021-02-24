// interfaces
import { IAirNavigationChargeType } from '../air-navigation-charges.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';
import { SystemConfigurationService } from '../../system-configuration/service/system-configuration.service';

export let endpoint: string = 'air-navigation-charges-schedules';

export class AirNavigationChargesService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IAirNavigationChargeType = {
    id: null,
    charges_type: null,
    aerodrome_category_name: null,
    aerodrome_category_id: null,
    document_filename: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private systemConfigurationService: SystemConfigurationService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public listChargeTypes(): Array<IStaticType> {
    let chargeTypes = [
      { name: 'Approach', value: 'approach_charges' },
      { name: 'Aerodrome', value: 'aerodrome_charges' },
      { name: 'Late Arrival', value: 'late_arrival_charges' },
      { name: 'Late Departure', value: 'late_departure_charges' },
      { name: 'Parking', value: 'parking_charges' },
      { name: 'Extended Hours Service', value: 'extended_hours_service_charge' }
    ];

    return chargeTypes.filter((chargeType: any) => {
      const chargeTypeSplit = chargeType.name.split(' ');

      if (chargeTypeSplit.length > 1) {
        return this.systemConfigurationService.shouldShowCharge(chargeTypeSplit[1].toLowerCase());
      } else {
        return this.systemConfigurationService.shouldShowCharge(chargeType.name.toLowerCase());
      }
    });
  }
}
