// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

//interfaces
import { ITuRateManagementScope, IUnifiedTaxManagement, IValidity } from './unified-tax-managment.interface';
import { IExtendableError, IError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

//services
import { UnifiedTaxManagementService } from './service/unified-tax-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { IUser } from "../users/users.interface";
import { UnifiedTaxValidityManagementService } from "./service/unified-tax-validity-management.service";
export class UnifiedTaxManagementController extends CRUDFormControllerUserService {

  protected service: any;

  protected serviceValidity: any;
  protected serviceTax: any;

  /* @ngInject */
  constructor(protected $scope: ITuRateManagementScope, protected unifiedTaxManagementService: UnifiedTaxManagementService,
    protected unifiedTaxValidityManagementService: UnifiedTaxValidityManagementService,
    protected systemConfigurationService: SystemConfigurationService, private customDate: CustomDate) {
    super($scope, unifiedTaxManagementService);
    this.service = unifiedTaxValidityManagementService;
    this.serviceValidity = unifiedTaxValidityManagementService;
    this.serviceTax = unifiedTaxManagementService;
    this.setup();
    $scope.reset = () => this.reset();
    $scope.addToFormula = (text: string) => this.addToFormula(text);
    this.getFilterParameters();

    //$scope.customDate = this.customDate.returnDateFormatStr(false);

    this.unifiedTaxValidityManagementService.getList().then((validities: Array<IValidity>) => {
      $scope.listUnifiedTaxValidity = validities;
      this.getFilterParameters();
    });

  }


  /**
  * Setup method override, add scope functions and properties.
  */
  protected setup(): void {
    this.$scope.customDate = this.customDate.returnDateFormatStr(false);


    // call parent setup method, required to override parent scope functions and properties
    super.setup();

    // override refresh scope method with refresh override above
    this.$scope.createValidity = (validity) => this.createValidity(validity);
    this.$scope.createTax = (tax) => this.createTax(tax);
    this.$scope.updateValidity = (validity, id) => this.updateValidity(validity, id);
    this.$scope.updateTax = (tax, id) => this.updateTax(tax, id);
    this.$scope.deleteValidity = (validity) => this.deleteValidity(validity);
    this.$scope.deleteTax = (tax) => this.deleteTax(tax);
    //this.$scope.refresh = () => this.refreshOverrideUnifiedTaxValidity();

    this.$scope.refreshOverrideUnifiedTaxValidity = () => this.refreshOverrideUnifiedTaxValidity();
    this.$scope.refreshOverrideUnifiedTax = () => this.refreshOverrideUnifiedTax();


    this.$scope.editValidity = (validity) => this.editValidity(validity);
    this.$scope.editTax = (tax) => this.editTax(tax);
    this.$scope.resetValidity = () => this.resetValidity();
    this.$scope.resetTax = () => this.resetTax();
    this.$scope.validateValidityDates = (fromValidityYear, toValidityYear) => this.validateValidityDates(fromValidityYear, toValidityYear);
    this.$scope.validateTaxDates = (fromManufactureYear, toManufactureYear) => this.validateTaxDates(fromManufactureYear, toManufactureYear);

    this.$scope.showTaxes = (validity) => this.showTaxes(validity);



    // add scope boolean flag for external identifier requirement
    this.$scope.requireExternalSystemId = this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_UNIFIED_TAX_EXTERNAL_SYSTEM_ID);

    this.resetValidity();
    this.resetTax();
  }

  /**
    * Sets the form to edit the single entry of tax validity
    * @param {Object} data   entity to edit
    */
  protected editValidity(data: Object): void {
    this.$scope.error = null;
    this.$scope.editableValidity = angular.copy(data);
    var fromString = this.$scope.editableValidity.from_validity_year;
    var toString = this.$scope.editableValidity.to_validity_year;
    if (fromString) {
      this.$scope.editableValidity.from_validity_year = new Date(fromString);
    }
    if (toString) {
      this.$scope.editableValidity.to_validity_year = new Date(toString);
    }
  }

  /**
   * Sets the form to edit the single entry of tax item
   * @param {Object} data   entity to edit
   */
  protected editTax(data: Object): void {
    this.$scope.error = null;
    this.$scope.editableTax = angular.copy(data);
    var fromString = this.$scope.editableTax.from_manufacture_year;
    var toString = this.$scope.editableTax.to_manufacture_year;
    if (fromString) {
      this.$scope.editableTax.from_manufacture_year = new Date(fromString);
    }
    if (toString) {
      this.$scope.editableTax.to_manufacture_year = new Date(toString);
    }
  }

  /**
   * Resets the form
   */
  protected resetValidity(): void {
    this.$scope.error = null;
    this.$scope.editableValidity = angular.copy(this.service.model);
    if (this.$scope.formValidity) {
      this.$scope.formValidity.$setUntouched();
    }
  }

  /**
   * Resets the form
   */
  protected resetTax(): void {
    this.$scope.error = null;
    this.$scope.editableTax = angular.copy(this.service.model);
    if (this.$scope.formTax) {
      this.$scope.formTax.$setUntouched();
    }
    this.$scope.editableTax.rate = "";
    this.$scope.formula ="";
  }




  /**
   * Refresh method override, adds scope filters, pagination, sort query.
   */

  //angular.IPromise<void>
  private refreshOverrideUnifiedTaxValidity(): ng.IPromise<any> {
    this.service = this.serviceValidity;
    this.$scope.selectedValidity = null;
    this.$scope.listUnifiedTax = null;
    this.$scope.listUnifiedTaxValidity = null;
    this.getFilterParameters();
    return this.unifiedTaxValidityManagementService.getList().then((validities: Array<IValidity>) => {
      this.$scope.listUnifiedTaxValidity = validities;
      this.getFilterParameters();
    });
  }

  private refreshOverrideUnifiedTax(): ng.IPromise<any> {
    this.service = this.serviceValidity;
    //this.$scope.selectedValidity = null;
    this.$scope.listUnifiedTax = null;
    //this.$scope.listUnifiedTaxValidity = null;
    this.getFilterParameters();

    if (this.$scope.selectedValidity != null) {
      return this.unifiedTaxManagementService.getListByValidityId(this.$scope.selectedValidity.id)
        .then((taxes: Array<IUnifiedTaxManagement>) => {
          this.$scope.listUnifiedTax = taxes;
          this.getFilterParameters();
        });
    }


  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }


  private showTaxes(validity: IValidity): void {
    this.unifiedTaxManagementService.getListByValidityId(validity.id)
      .then((taxes: Array<IUnifiedTaxManagement>) => {
        this.$scope.listUnifiedTax = taxes;
        this.getFilterParameters();
      });
    this.$scope.selectedValidity = validity;
    this.editValidity(validity)
  }

  protected createValidity(data: Object): ng.IPromise<any> {
    this.service = this.serviceValidity;
    var toRet = super.create(data);
    this.refreshOverrideUnifiedTaxValidity();
    this.resetValidity();
    return toRet;
  }

  protected createTax(data: IUnifiedTaxManagement): ng.IPromise<any> {
    this.service = this.serviceTax;
    data.validity = this.$scope.selectedValidity;
    var toRet = super.create(data).then(() => {
      this.showTaxes(this.$scope.selectedValidity);
    });
    this.resetTax();
    return toRet;
  }

  protected updateValidity(data: Object, id: number): ng.IPromise<any> {
    this.service = this.serviceValidity;
    var toRet = super.update(data, id);
    this.refreshOverrideUnifiedTaxValidity();
    this.resetValidity();
    return toRet;
  }

  protected updateTax(data: Object, id: number): ng.IPromise<any> {
    this.service = this.serviceTax;
    var toRet = super.update(data, id).then(() => {
      this.showTaxes(this.$scope.selectedValidity);
    });
    this.resetTax();
    return toRet;
  }

  protected list(data?: Object, queryString?: string, endpoint?: string): ng.IPromise<any> {
    this.service = this.serviceValidity;
    return super.list(<any>data, queryString, endpoint);
  }

  /**
   * Calls the service delete method then resets the form
   * @param {number} id  the id to delete
   */
  protected deleteValidity(id: number): ng.IPromise<void> {
    this.service = this.serviceValidity;
    var toRet = super.delete(id);
    this.refreshOverrideUnifiedTaxValidity();
    this.resetValidity();
    return toRet;
  }

  /**
   * Calls the service delete method then resets the form
   * @param {number} id  the id to delete
   */
  protected deleteTax(id: number): ng.IPromise<void> {
    this.service = this.serviceTax;
    var toRet = super.delete(id).then(() => {
      this.showTaxes(this.$scope.selectedValidity);
    });
    this.resetTax();
    return toRet;
  }

  protected validateValidityDates(fromValidityYear: String, toValidityYear: String): boolean {

    if (fromValidityYear != null || toValidityYear != null) {
      return true;
    }
    return false;
  }

  protected validateTaxDates(fromManufactureYear: String, toManufactureYear: String): boolean {

    if (fromManufactureYear != null || toManufactureYear != null) {
      return true;
    }
    return false;
  }


  private addToFormula(text: string): void {
    this.$scope.ifValidate = true;

    if (!this.$scope.formula) {
      this.$scope.formula = text;
    } else {
      this.$scope.formula += text;
    }

    if (this.$scope.index >= 0 && this.$scope.field) {
      this.$scope.editable.unified_tax_formulas[this.$scope.index][this.$scope.field] = this.$scope.formula;
    } else {
      this.$scope.editableTax.rate = this.$scope.formula;
    }
  }

}
