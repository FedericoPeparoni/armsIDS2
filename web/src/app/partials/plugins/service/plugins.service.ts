// interfaces
import { IPlugin } from '../plugins.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { SystemConfigurationService } from '../../system-configuration/service/system-configuration.service';

export let endpoint = 'plugins';

export class PluginsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IPlugin = {
    id: null,
    name: null,
    description: null,
    enabled: null,
    key: null,
    configurations: [],
    visible: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, protected systemConfigurationService: SystemConfigurationService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getPluginEnabled(organizationName: string, pluginName: string): ng.IPromise<boolean> {
    return this.restangular.one(`${this.endpoint}/isEnabled/${organizationName}/${pluginName}`).get();
  }
}
