// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { EnrouteAirNavigationChargesManagementService } from './service/enroute-air-navigation-charges-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { FlightMovementCategoryService } from '../flight-movement-category/service/flight-movement-category.service';

// interfaces
import {IEnrouteAirNavigationCharge, IEnrouteAirNavigationChargeScope} from './enroute-air-navigation-charges-management.interface';
import { IExtendableError, IError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IFlightMovementCategory } from '../flight-movement-category/flight-movement-category.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class EnrouteAirNavigationChargesManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(
      protected $scope: IEnrouteAirNavigationChargeScope,
      private enrouteAirNavigationChargesManagementService: EnrouteAirNavigationChargesManagementService,
      private systemConfigurationService: SystemConfigurationService, private flightMovementCategoryService: FlightMovementCategoryService) {

    super($scope, enrouteAirNavigationChargesManagementService);
    super.setup();
    $scope.showDWFactor = true;
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);

    flightMovementCategoryService.listAll().then((categories: Array<IFlightMovementCategory>) => {
      this.$scope.categories = categories;
      this.getEditable();
    });

    $scope.reset = () => this.reset();
    $scope.validate = () => this.validate();
    $scope.isValidate = () => this.$scope.ifValidate = true;
    $scope.edit = (item: IEnrouteAirNavigationCharge) => { super.edit(systemConfigurationService.convertMtowProperty(item, 'mtow_category_upper_limit')); };
    $scope.addToFormula = (text: string) => this.addToFormula(text);
    $scope.setField = (index: number, field: string) => this.setField(index, field);
    this.getFilterParameters();
  }

  protected reset(): void {
    super.reset();
    this.getEditable();
    this.$scope.error = <IExtendableError>{};
  }

  private getEditable(): void {
    this.$scope.editable.enroute_air_navigation_charge_formulas = [];
    if (this.$scope.categories) {
      this.$scope.categories.forEach((category: IFlightMovementCategory) => {
        if (category.name !== 'OTHER') {
          this.$scope.editable.enroute_air_navigation_charge_formulas.push({ flightmovement_category: category, formula: null, d_factor_formula: null });
        } else {
          this.$scope.editable.enroute_air_navigation_charge_formulas.push({ flightmovement_category: category, formula: 0, d_factor_formula: 0 });
        }
      });
    }
  }

  private validate(): void {
    this.service.validate(this.$scope.editable).then((data: any) => {
      for (let i = 0; i < data.length; i++) {
          if (data[i].formula_valid === false) {
            this.$scope.error = <IExtendableError>{};
            let errorObj = <IError>{};
            this.$scope.error.error = {data: errorObj};
            this.$scope.error.error.data.error = data[i].formula;
            this.$scope.error.error.data.error_description = data[i].issue;
            return;
          }
      }
      this.$scope.ifValidate = false;
    });
  }

  private addToFormula(text: string): void {
    this.$scope.ifValidate = true;

    if (!this.$scope.formula) {
      this.$scope.formula = text;
    } else {
      this.$scope.formula += text;
    }

    if (this.$scope.index >= 0 && this.$scope.field) {
      this.$scope.editable.enroute_air_navigation_charge_formulas[this.$scope.index][this.$scope.field] = this.$scope.formula;
    } else {
      this.$scope.editable.w_factor_formula = this.$scope.formula;
    }
  }

  private setField(index: number, field: string): void {
    this.$scope.showDWFactor = field === 'formula';

    this.$scope.index = index;
    this.$scope.field = field;

    if (index >= 0 && field) {
      this.$scope.formula = this.$scope.editable.enroute_air_navigation_charge_formulas[index][field];
    } else {
      this.$scope.formula = this.$scope.editable.w_factor_formula;
    }
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }
}
