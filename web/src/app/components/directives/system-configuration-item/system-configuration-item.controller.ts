// interfaces
import { IMultiSelectObj } from '../../../angular-ids-project/src/helpers/interfaces/multiselect.interface';
import { ISystemConfigurationItemScope } from './system-configuration-item.interface';
import { ISystemConfiguration } from '../../../partials/system-configuration/system-configuration.interface';
import { IRestangularResponse } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// services
import { SystemConfigurationService } from '../../../partials/system-configuration/service/system-configuration.service';
import { SysConfigBoolean } from '../../../angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';

// constants
import { SysConfigConstants } from '../../../partials/system-configuration/system-configuration.constants';

/** @ngInject */
export class SystemConfigurationItemController {

  constructor(private $scope: ISystemConfigurationItemScope, private systemConfigurationService: SystemConfigurationService,
    private sysConfigBoolean: SysConfigBoolean) {

    $scope.crossing = this.systemConfigurationService.setCrossingDistance();
    $scope.languageOptions = this.systemConfigurationService.getLanguageOptions();
    $scope.supportedLanguages = this.systemConfigurationService.getSupportedLanguages();
    $scope.verifyInProgress = false;
    this.parseRangeAndModel($scope.crossing);

    // expose necessary methods to scope
    $scope.addCDPToList = () => this.addCDPToList();
    $scope.updateSupportedLanguages = () => this.updateSupportedLanguages();
    $scope.mapRange = (item: any) => this.mapRange(item);
    $scope.languageMap = (item: any) => this.languageMap(item);
    $scope.itemValidation = (item: ISystemConfiguration) => this.itemValidation(item);
    $scope.itemValidationInvoiceByFm = (item: ISystemConfiguration) => this.itemValidationInvoiceByFm(item);

    // watch for changes and reset verify result
    $scope.$watch('item.current_value', () => this.$scope.verifyResult = null);
  }

  /**
   * This is the onChange event for the
   * crossing distance precedence multiselect
   *
   * For CDP, the current value must be a string
   * of comma separated values
   *
   * @returns void
   */
  private addCDPToList(): void {
    this.$scope.CDP = this.getItemName(this.$scope.CDPModel, this.$scope.baseCDPRange);
    this.$scope.item.current_value = this.$scope.CDP.join();
  }

  /**
   * This parses the Crossing Distance Precedence
   * values, changing them from strings to objects,
   * in order to populate the multiselect
   *
   * If upgrading, to v2.0.0 (`angularjs-dropdown-multiselect` currently in beta)
   * we can use an array of strings and this code may
   * be removed
   *
   * @param  {ISystemConfigurationSpring} data
   * @returns void
   */
  private parseRangeAndModel(crossing: ISystemConfiguration): void {
    if (!crossing) {
      return;
    }

    const ranges: string[] = crossing.range.split(',');
    let currentValues: string[];

    if (typeof crossing.current_value === 'string') {
      currentValues = crossing.current_value.split(',');
    }

    this.$scope.CDPRange = ranges.map((x: string, idx: number) => { return { id: idx + 1, label: x }; });
    this.$scope.baseCDPRange = angular.copy(this.$scope.CDPRange);
    this.$scope.CDPModel = [];

    for (let value of currentValues) {
      for (let range of this.$scope.CDPRange) {
        if (range.label === value) {
          this.$scope.CDPModel.push(<IMultiSelectObj>{ id: range.id });
        }
      }
    }

    this.$scope.CDP = this.getItemName(this.$scope.CDPModel, this.$scope.CDPRange);
  }

  /**
   * Populates the precedence list of user
   * selected crossing distances
   *
   * @param  {Array<Object>} models
   * @param  {Array<Object>} ranges
   * @returns Array<string>
   */
  private getItemName(models: Array<IMultiSelectObj>, ranges: Array<IMultiSelectObj>): Array<string> {
    let array = [];
    for (let model of models) {
      for (let range of ranges) {
        if (model.id === range.id) {
          array.push(range.label);
        }
      }
    }
    return array;
  }

  private mapRange(item: any): any {
    let range = item.range.split(',');
    let list = [];

    // test to make sure that INVOICE_CURRENCY_ENROUTE and INVOICE_FM_CATEGORY codependant settings are correct
    if (item.item_name === SysConfigConstants.INVOICE_CURRENCY_ENROUTE.toString()) {
      for (let i = 0, len = this.$scope.list.length; i < len; i++) {
        if (this.$scope.list[i].item_name === SysConfigConstants.INVOICE_FM_CATEGORY.toString()) {
          if (!this.sysConfigBoolean.parse(this.$scope.list[i].current_value)) {
            range.pop();

            item.current_value = range[0];
          };
        };
      };
    };
    range.forEach((entry: string, index: number) => list[entry] = range[index]);

    return list;
  }

  private languageMap(item: any): any {
    const range = JSON.parse(item.range);

    return range.reduce((formattedLanguages: any, language: any) => {
      formattedLanguages[JSON.stringify(language)] = language.label;

      return formattedLanguages;
    }, []);
  }

  private updateSupportedLanguages(): void {
    const selected = this.$scope.languageOptions.reduce((selectedLanguages: any, language: any) => {

      if (this.$scope.supportedLanguages.map((o: any) => o.id).includes(language.id)) {
        selectedLanguages.push({ code: language.id, label: language.label });
      }

      return selectedLanguages;
    }, []);

    this.$scope.item.current_value = JSON.stringify(selected);
  }

  private itemValidation(item: ISystemConfiguration): void {
    this.$scope.verifyInProgress = true;
    this.systemConfigurationService.validateItem(item)
      .then((response: boolean) => this.handleItemValidationResponse(response, null))
      .catch((error: IRestangularResponse) => this.handleItemValidationResponse(false, error));
  }

  private itemValidationInvoiceByFm(item: ISystemConfiguration): void {
    let data = [];

    this.$scope.verifyInProgress = true;

    this.$scope.list.map((item: ISystemConfiguration, index: number) => {
      if (item.item_name === SysConfigConstants.INVOICE_FM_CATEGORY.toString() || item.item_name === SysConfigConstants.INVOICE_CURRENCY_ENROUTE.toString()) {
        data.push(item);
      };
    });

    this.systemConfigurationService.validateInvoiceByFm(data)
      .then((response: boolean) => this.handleItemValidationResponse(response, null))
      .catch((error: IRestangularResponse) => this.handleItemValidationResponse(false, error));
  }

  private handleItemValidationResponse(result: boolean, error?: IRestangularResponse): void {
    this.$scope.verifyInProgress = false;
    this.$scope.verifyResult = result;
    this.$scope.validationError = error && error.data ? error.data : null;
  }

}
