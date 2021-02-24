// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { RevenueProjectionService } from './service/revenue-projection.service';
import { UsersService } from '../../partials/users/service/users.service';

// interfaces
import { IRevenueProjectionScope } from './revenue-projection.interface';

export class RevenueProjectionController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IRevenueProjectionScope, private revenueProjectionService: RevenueProjectionService,
      private usersService: UsersService, private $sce: angular.ISCEService, private $translate: angular.translate.ITranslateService) {
    super($scope, revenueProjectionService);
    super.setup();

    $scope.changeStatus = (status: string, enabled: boolean) => this.changeStatus(status, enabled);
    $scope.addToFormula = (text: string, type: string) => this.addToFormula(text, type);
    $scope.getTrustedHtml = (text: string) => this.getTrustedHtml(text);
    $scope.setFocusInput = (text: string) => this.setFocusInput(text);
    $scope.doGenerate = (format: string) => this.doGenerate(format);
    $scope.getFormulas = () => this.getFormulas();
    $scope.validate = () => this.validate();
    $scope.clearForm = () => this.clearForm();

    this.clearForm();
    this.getFormulas();
  }

  private changeStatus(status: string, enabled: boolean): void {
    if (!enabled) {
      if (typeof this.$scope.editable[status] === 'number') {
        this.$scope.editable[status] = 0;
      } else {
        this.$scope.editable[status] = '';
      };
    };

    this.checkParameters();
  }

  private clearForm(): void {
    this.clearChecks();

    this.$scope.editable.upper_limit = 900;
    this.$scope.canGenerate = false;
    this.$scope.formula = null;
    this.$scope.validated = false;
    this.$scope.processing = false;
    this.$scope.noFormulas = false;
  }

  private getFormulas(): void {
    this.revenueProjectionService.getFormulas(this.$scope.editable.upper_limit).then((resp: any) => {
      const keys = Object.keys(this.$scope.editable);

      keys.map((key: any) => {
        if (this.$scope.editable.hasOwnProperty(key) && resp.hasOwnProperty(key)) {
          this.$scope.enabled[`${key}_enabled`] = true;
          this.$scope.editable[key] = resp[key];
        }
      });

      this.checkParameters();
    });
  }

  private checkParameters(): void {
    let set: number = 0;

    Object.keys(this.$scope.enabled).reduce((p: any, c: string) => {
      if (this.$scope.enabled[c]) {
        return set++;
      };
    }, {});

    if (set > 0) {
      this.$scope.canGenerate = true;
    } else {
      this.$scope.canGenerate = false;
    };
  }

  private doGenerate(format: string): void {
    this.$scope.editable.format = format;
    this.$scope.processing = true;
    this.$scope.validated = false;

    this.revenueProjectionService.doGenerate(this.$scope.editable).then((resp: any) => {
      this.$scope.processing = false;
      let blob;
      let filename;

      if (format === 'PDF') {
        blob = new Blob([resp], { type: 'application/pdf' });
        filename = 'revenue_projection.pdf';
      } else {
        blob = new Blob([resp], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        filename = 'revenue_projection.xlsx';
      };

      let downloadLink = angular.element('<a></a>');
      downloadLink.attr('href', window.URL.createObjectURL(blob));
      downloadLink.attr('download', filename);
      downloadLink[0].click();

    }, (err: any) => {
      this.$scope.processing = false;
    });
  }

  private setFocusInput(text: string): void {
    if (text) {
      this.$scope.formula = angular.element(document.getElementById(text))[0];

      angular.element(document.getElementById(text))[0].focus();
    } else {
      this.$scope.formula = null;
    };
  }

  private addToFormula(text: string, type: string): void {
    let name = this.$scope.formula.name;
    let eolReg = new RegExp(/[\+\-\*\/\(\)\√]$/);
    let eolTest = eolReg.test(this.$scope.editable[name]);
    this.$scope.formulaError = null;

    if (!this.$scope.editable[name]) {
      if (type === 'op') {
        this.$scope.formulaError = 'Formula cannot start with operator.';
      };
    } else {
      if (eolTest && type === 'op') {
        this.$scope.formulaError = 'Operator already selected.';
      } else if (!eolTest && type !== 'op') {
        this.$scope.formulaError = 'Operator required.';
      };
    };

    if (!this.$scope.formulaError) {
      this.$scope.editable[this.$scope.formula.name] += text;
    };
  }

  private validate(): void {
    this.$scope.validated = false;

    this.revenueProjectionService.validateSingle(this.$scope.editable).then((resp: any) => {
      this.$scope.validated = true;
      this.$scope.data_results = resp;
    });
  }

  private getTrustedHtml(text: string): string {
    let output;

    if (text) {
      output = this.$sce.trustAsHtml(text);
    } else {
      output =  'None';
    }

    return this.$translate.instant(output);
  }

  private clearChecks(): void {
    this.$scope.enabled = {
      upper_limit_enabled: true,
      domestic_formula_enabled: false,
      regional_departure_formula_enabled: false,
      regional_arrival_formula_enabled: false,
      regional_overflight_formula_enabled: false,
      international_departure_formula_enabled: false,
      international_arrival_formula_enabled: false,
      international_overflight_formula_enabled: false,
      w_factor_formula_enabled: false,
      domestic_d_factor_formula_enabled: false,
      reg_dep_d_factor_formula_enabled: false,
      reg_arr_d_factor_formula_enabled: false,
      reg_ovr_d_factor_formula_enabled: false,
      int_dep_d_factor_formula_enabled: false,
      int_arr_d_factor_formula_enabled: false,
      int_ovr_d_factor_formula_enabled: false,
      charges_passenger_enabled: false,
      charges_approach_enabled: false,
      charges_aerodrome_enabled: false,
      charges_late_arrival_enabled: false,
      charges_late_departure_enabled: false,
      charges_vol_flights_enabled: false,
      charges_vol_passengers_enabled: false
    };
  }

}
