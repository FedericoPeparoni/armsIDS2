// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { ISystemConfigurationScope, ISystemConfiguration } from './system-configuration.interface';

// services
import { SystemConfigurationService } from './service/system-configuration.service';
import { TranslateService } from '../../run/translate.run';

export class SystemConfigurationController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ISystemConfigurationScope, private systemConfigurationService: SystemConfigurationService, private translateService: TranslateService) {
    super($scope, systemConfigurationService);
    super.setup();
    $scope.textFilter = '';
    $scope.shouldBeFilteredCategory = (textFilter: string, list: Array<ISystemConfiguration>, id: number, firstInCategory: boolean) => SystemConfigurationController.shouldBeFilteredCategory(textFilter, list, id, firstInCategory);
    $scope.shouldBeFilteredItem = (textFilter: string, itemName: string) => SystemConfigurationController.shouldBeFilteredItem(textFilter, itemName);
    $scope.reloadLanguageConfiguration = () => translateService.run();
    $scope.updateAndReloadLang = (data: any) => this.updateAndReloadLang(data);
  }

  /**
   * Filters the category name with these conditions
   * - if it's the first one of that category id
   * - if at least one item matches the textFilter (so we also go ahead and show it
   *
   * @param textFilter        the text entered into the input
   * @param list              returned list of system configurations (ordered by orderBy filter)
   * @param id                the class id for that item
   * @param firstInCategory   if it's the first one in that particular category
   * @returns {boolean}
   */
  static shouldBeFilteredCategory(textFilter: string, list: Array<ISystemConfiguration>, id: number, firstInCategory: boolean): boolean {
    for (let i = 0; i < list.length; i++) {
      if (list[i].item_class.id === id && list[i].item_name.toLowerCase().indexOf(textFilter.toLowerCase()) !== -1 && firstInCategory) {
        return true;
      }
    }

    return false;
  }

  /**
   * Filters the individual item to see if includes
   * NOTE: This can be removed and replace with a simple .includes() in the HTML when we support ES7
   *
   * @param textFilter    the text entered into the input
   * @param itemName      the name of the current item to check against
   * @returns {boolean}
   */
  static shouldBeFilteredItem(textFilter: string, itemName: string): boolean {
    return itemName.toLowerCase().indexOf(textFilter.toLowerCase()) !== -1;
  }

  private updateAndReloadLang(data: any): void {
    super.update(data, null).then(() => {
      this.$scope.reloadLanguageConfiguration();
    });
  }

}
