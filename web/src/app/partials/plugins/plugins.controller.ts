// constants
import { DISPLAY } from '../../angular-ids-project/src/components/input/input.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IPlugin, IPluginParams, IPluginScope } from './plugins.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ISystemConfigurationSpring } from '../system-configuration/system-configuration.interface';

// services
import { PluginsService } from './service/plugins.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

export class PluginsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IPluginScope, private pluginsService: PluginsService,
    private systemConfigurationService: SystemConfigurationService, private $stateParams: IPluginParams) {

    // define and setup super class
    super($scope, pluginsService);

    super.setup({ refresh: false });

    this.getFilterParameters();

    // set hidden property from param
    this.$scope.showHidden = $stateParams.hidden === 'hidden' ? true : false;

    // expose necessary methods to scope
    this.$scope.edit = (plugin: IPlugin) => this.edit(plugin);
    this.$scope.refresh = () => this.refresh();
    this.$scope.add = (plugin: IPlugin, id: number) => this.add(plugin, id);
    this.$scope.update = (plugin: IPlugin, id: number) => this.update(plugin, id);
    this.$scope.remove = (plugin: IPlugin, id: number) => this.remove(plugin, id);

    // call super list method with visible parameter
    super.list({ visible: !this.$scope.showHidden });
  }

  protected edit(plugin: IPlugin): void {
    this.systemConfigurationService.listByPluginId(plugin.id)
      .then((systemConfiguration: ISystemConfigurationSpring) => {
        plugin.configurations = systemConfiguration.content;
        super.edit(plugin);
      });
  }

  /**
   * Adding a plugin simply shows it on the main view.
   * 
   * @param plugin plugin to show
   * @param id plugin id to show
   */
  protected add(plugin: IPlugin, id: number): angular.IPromise<any> {
    plugin.visible = true;
    return this.update(plugin, id);
  }

  /**
   * Updating plugin values and system configuration items.
   * 
   * @param plugin plugin to update
   * @param id plugin to update id
   */
  protected update(plugin: IPlugin, id: number): angular.IPromise<any> {
    return this.systemConfigurationService.update(plugin.configurations, null)
      .then(() => super.update(plugin, id))
      .catch((error: IRestangularResponse) => this.displayError(error));
  }

  /**
   * Removing a plugin simply disables and hides it from the main view.
   * 
   * @param plugin plugin to remove
   * @param id plugin to remove id
   */
  protected remove(plugin: IPlugin, id: number): angular.IPromise<any> {
    plugin.enabled = false;
    plugin.visible = false;
    return this.update(plugin, id);
  }

  protected refresh(): angular.IPromise<void> {
    this.getFilterParameters();

    // hold search value to update highlight text after promise returned
    // this forces highlighting to use the same search value even if the 
    // search value changes after the promise was sent
    // and finally update next retry cycle
    return super
      .list(this.$scope.filterParameters, this.$scope.getSortQueryString())
      .then(() => this.updateHighlight(this.$scope.search));
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      visible: !this.$scope.showHidden
    };
  }

  /**
   * Display error message and highlight form feilds.
   * 
   * @param error restangular response
   */
  private displayError(error: IRestangularResponse): void {
    this.$scope.error = { error: error };
    if (typeof error.data.field_errors !== 'undefined' && error.data.field_errors !== null) {
      for (let i = 0; i < error.data.field_errors.length; i++) { // binds field errors if there are any
        if (this.$scope.form[error.data.field_errors[i].field] !== undefined) {
          this.$scope.form[error.data.field_errors[i].field].$setValidity(DISPLAY.API_RESPONSE_CLASS, false);
        } else {
          console.warn('issue with backend sending input binding that does not exist on $scope');
        }
      }
    }
  }

  /**
   * Update scope highlight search with new search value.
   * 
   * @param search search text
   */
  private updateHighlight(search: string): void {
    this.$scope.highlightSearch = search;
  }
}
