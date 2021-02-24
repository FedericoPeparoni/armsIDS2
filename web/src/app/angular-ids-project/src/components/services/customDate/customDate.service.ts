// services
import { SystemConfigurationService } from '../../../../../partials/system-configuration/service/system-configuration.service';

// constants
import { SysConfigConstants } from '../../../../../partials/system-configuration/system-configuration.constants';

export class CustomDate {

  private systemConfigurationService: SystemConfigurationService;

  private dateStr: SysConfigConstants = SysConfigConstants.DATE_FORMAT;

  /** @ngInject */
  constructor(systemConfigurationService: SystemConfigurationService) {
    this.systemConfigurationService = systemConfigurationService;
  }

  public returnDateFormatStr(remove: boolean): string {
    const dateStr = this.dateStr || 'DATE_RANGE';
    const dateFormatSys: string | number = this.systemConfigurationService.getValueByName(dateStr.toString());
    const dateFormatSysStr: string | number = dateFormatSys || 'yyyy-MMM-dd';

    if (remove) {
      return dateFormatSysStr.toString().replace(/(-dd|dd-)/gi, '');
    } else {
      return dateFormatSysStr.toString();
    };
  }

}
