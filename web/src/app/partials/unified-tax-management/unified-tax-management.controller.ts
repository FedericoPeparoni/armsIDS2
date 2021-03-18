 // controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

 //interfaces
import {ITuRateManagementScope, IUnifiedTaxManagement, IValidity} from './unified-tax-managment.interface';

 //services
import { UnifiedTaxManagementService } from './service/unified-tax-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
 import {IUser} from "../users/users.interface";
 import {UnifiedTaxValidityManagementService} from "./service/unified-tax-validity-management.service";
export class UnifiedTaxManagementController extends CRUDFormControllerUserService {
  
  protected service: any;
  
  protected serviceValidity: any;
  protected serviceTax: any;

  /* @ngInject */
  constructor(protected $scope: ITuRateManagementScope, protected unifiedTaxManagementService: UnifiedTaxManagementService,
              protected unifiedTaxValidityManagementService: UnifiedTaxValidityManagementService,
    protected systemConfigurationService: SystemConfigurationService,private customDate: CustomDate) {
    super($scope, unifiedTaxManagementService);
    this.service = unifiedTaxValidityManagementService;
    this.serviceValidity = unifiedTaxValidityManagementService;
    this.serviceTax = unifiedTaxManagementService;
    this.setup();
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
    this.$scope.refresh = () => this.refreshOverride();
    this.$scope.editValidity = (validity) => this.editValidity(validity);
    this.$scope.editTax = (tax) => this.editTax(tax);
    this.$scope.resetValidity = () => this.resetValidity();
    this.$scope.resetTax = () => this.resetTax();

    this.$scope.showTaxes = (validity) => this.showTaxes(validity);



     // add scope boolean flag for external identifier requirement
     this.$scope.requireExternalSystemId = this.systemConfigurationService
     .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_UNIFIED_TAX_EXTERNAL_SYSTEM_ID);
 }
 
 /**
   * Sets the form to edit the single entry of tax validity
   * @param {Object} data   entity to edit
   */
  protected editValidity(data: Object): void {
    this.$scope.error = null;
    this.$scope.editableValidity = angular.copy(data);
  }
  
  /**
   * Sets the form to edit the single entry of tax item
   * @param {Object} data   entity to edit
   */
  protected editTax(data: Object): void {
    this.$scope.error = null;
    this.$scope.editableTax = angular.copy(data);
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
  }

  /**
   * Refresh method override, adds scope filters, pagination, sort query.
   */

  //angular.IPromise<void>
  private refreshOverride(): ng.IPromise<any> {
    this.$scope.listUnifiedTax = null;
    this.$scope.listUnifiedTaxValidity = null;
    this.getFilterParameters();

    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }


  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }


  private showTaxes(validity: IValidity): void {
    console.log("showTaxes OK!!!")
    this.unifiedTaxManagementService.getListByValidityId(validity.id)
      .then((taxes: Array<IUnifiedTaxManagement>) => {
        this.$scope.listUnifiedTax = taxes;
      this.getFilterParameters();
    });
    this.editValidity(validity)
  }

  protected createValidity(data: Object): ng.IPromise<any> {
    this.service = this.serviceValidity;
    return super.create(data);
  }

  protected createTax(data: Object): ng.IPromise<any> {
    this.service = this.serviceTax;
    return super.create(data);
  }

  protected updateValidity(data: Object, id: number): ng.IPromise<any> {
    this.service = this.serviceValidity;
    return super.update(data, id);
  }

  protected updateTax(data: Object, id: number): ng.IPromise<any> {
    this.service = this.serviceTax;
    return super.update(data, id);
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
    return super.delete(id);
  }

  /**
   * Calls the service delete method then resets the form
   * @param {number} id  the id to delete
   */
  protected deleteTax(id: number): ng.IPromise<void> {
    this.service = this.serviceTax;
    return super.delete(id);
  }



}
