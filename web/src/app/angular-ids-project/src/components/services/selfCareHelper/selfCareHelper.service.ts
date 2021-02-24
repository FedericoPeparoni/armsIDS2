/*
* Helper class for common methods and requests used by the self-care portal
*/

// interfaces
import { ISystemConfiguration } from '../../../../../partials/system-configuration/system-configuration.interface';

// services
import { LocalStorageService } from '../localStorage/localStorage.service';
import { SysConfigBoolean } from '../sysConfigBoolean/sysConfigBoolean.service';

export class SelfCareHelper {

  protected restangular: restangular.IService;

  constructor(protected Restangular: restangular.IService, protected sysConfigBoolean: SysConfigBoolean) {
    this.restangular = Restangular;
  }
  /**
   * Fetch the system config item from the API
   *
   * @param  {string}               name      name of the system config item to look for
   * @return {string|number|Object}           Returns a string, number, or JSON 
   */
  public fetchConfig(name: string): ng.IPromise<any> {
    return this.restangular.one(`system-configurations/noauth/getSelfCareSettings`).customGET('', {param: name}).then((resp: ISystemConfiguration[]) => {
      this.parseReturn(name, this.restangular.stripRestangular(resp));
    });
  }

  /**
   * Format the current value of the system config
   *
   * @param  {ISystemConfiguration} resp    the system configuration item to parse
   */
  private parseReturn(name: string, resp: ISystemConfiguration[]): void {
    const type: string = resp[0].data_type.name;
    let val: string | number | Object;

    if (type === 'boolean') {
      val = this.sysConfigBoolean.parse(resp[0].current_value);
    } else {
      val = resp[0].current_value;
    };

    LocalStorageService.set(name, val);
  }

}

SelfCareHelper.$inject = ['Restangular', 'sysConfigBoolean'];
