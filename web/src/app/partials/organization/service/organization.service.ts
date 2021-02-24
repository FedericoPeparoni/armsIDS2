// constants
import { OrganizationName } from '../organization.constants';
import { SysConfigConstants } from '../../system-configuration/system-configuration.constants';

// interfaces
import { IActiveOrganization } from '../organization.interface';

// services
import { SystemConfigurationService } from '../../system-configuration/service/system-configuration.service';

export class OrganizationService {

  /** @ngInject */
  constructor(private systemConfigurationService: SystemConfigurationService) {
  }

  /**
   * Retrieve active organization flags from systom configuration settings.
   */
  public active(): IActiveOrganization {

    // define active organization result and set all to false
    const result: IActiveOrganization = {
      isCaab: false,
      isDcAnsp: false,
      isEana: false,
      isInac: false,
      isKcaa: false,
      isZacl: false,
      isTTCAA: false
    };

    // set active organization flag from system configuration settings, only ever one
    switch (this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ORGANIZATION)) {
      case OrganizationName.CAAB:
        result.isCaab = true;
        break;
      case OrganizationName.DC_ANSP:
        result.isDcAnsp = true;
        break;
      case OrganizationName.EANA:
        result.isEana = true;
        break;
      case OrganizationName.INAC:
        result.isInac = true;
        break;
      case OrganizationName.KCAA:
        result.isKcaa = true;
        break;
      case OrganizationName.ZACL:
        result.isZacl = true;
        break;
      case OrganizationName.TTCAA:
        result.isTTCAA = true;
        break;
      default:
        // ignored
    }

    return result;
  }
}
