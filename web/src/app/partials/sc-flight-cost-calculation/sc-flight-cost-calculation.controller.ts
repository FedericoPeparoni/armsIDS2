// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ScFlightCostCalculationService } from './service/sc-flight-cost-calculation.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { CurrencyExchangeRatesService } from '../currency-exchange-rates/service/currency-exchange-rates.service';

// interfaces
import { IScFlightCostCalculation } from './sc-flight-cost-calculation.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IFlightMovement } from '../flight-movement-management/flight-movement-management.interface';
import { ISystemConfiguration } from '../system-configuration/system-configuration.interface';

// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';
import { RecaptchaService } from '../../angular-ids-project/src/components/recaptcha/service/recaptcha.service';

const DISTANCE_UNIT_OF_MEASURE = `SystemConfiguration:${SysConfigConstants.DISTANCE_UNIT_OF_MEASURE}`;
const MTOW_UNIT_OF_MEASURE = `SystemConfiguration:${SysConfigConstants.MTOW_UNIT_OF_MEASURE}`;
const AIR_NAVIGATION_CHARGES_CURRENCY = `SystemConfiguration:${SysConfigConstants.AIR_NAVIGATION_CHARGES_CURRENCY}`;
const ANSP_CURRENCY = `SystemConfiguration:${SysConfigConstants.ANSP_CURRENCY}`;

export class ScFlightCostCalculationController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private recaptchaService: RecaptchaService, private scFlightCostCalculationService: ScFlightCostCalculationService,
    private systemConfigurationService: SystemConfigurationService, private currencyExchangeRatesService: CurrencyExchangeRatesService) {
    super($scope, scFlightCostCalculationService);
    super.setup({ refresh: false });

    if (!LocalStorageService.get(DISTANCE_UNIT_OF_MEASURE)) {
      this.getUnitsOfMeasureConfiguration();
    }

    if (!LocalStorageService.get(AIR_NAVIGATION_CHARGES_CURRENCY)) {
      this.getAirNavigationChargesCurrency();
    }

    if (!LocalStorageService.get(ANSP_CURRENCY)) {
      this.getANSPCurrency();
    }

    $scope.distanceUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.DISTANCE_UNIT_OF_MEASURE) as string;
    $scope.mtowUnitsOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE) as string;
    $scope.airNavigationChargesCurrency = systemConfigurationService.getValueByName(<any>SysConfigConstants.AIR_NAVIGATION_CHARGES_CURRENCY) as string;
    $scope.anspCurrency = systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY) as string;

    $scope.calculate = (flightCostCalculation: IScFlightCostCalculation) => {
      $scope.cost = null;
      scFlightCostCalculationService.calculate(flightCostCalculation).then((response: IFlightMovement) => {
        $scope.cost = response;
        $scope.adapCharges = this.getAdapCharges($scope.cost);
        $scope.totalCostUSD = $scope.cost.enroute_charges + $scope.adapCharges;
        if ($scope.anspCurrency && $scope.anspCurrency !== 'USD') {
          currencyExchangeRatesService.getCurrentExchangeRatesByCurrencyCode($scope.anspCurrency).then((rate: number) => {
            $scope.totalCostAnsp = $scope.totalCostUSD / rate;
          });
        };
        $scope.noBillableRoute = $scope.cost.resolution_errors.includes('ZERO_LENGTH_BILLABLE_TRACK');
        if ($scope.widgetId) {
          recaptchaService.reload($scope.widgetId);
        }
        this.reset();
      },
        (error: IRestangularResponse) => {
          $scope.error = { error };
          recaptchaService.reload($scope.widgetId);
        }
      );
    };
  }

  private getUnitsOfMeasureConfiguration(): void {
    this.systemConfigurationService.getUnitsOfMeasureConfiguration().then((resp: Array<ISystemConfiguration>) => {
      if (resp) {
        LocalStorageService.set(DISTANCE_UNIT_OF_MEASURE, resp[0].current_value);
        LocalStorageService.set(MTOW_UNIT_OF_MEASURE, resp[1].current_value);
        this.$scope.distanceUnitOfMeasure = resp[0].current_value as string;
        this.$scope.mtowUnitsOfMeasure = resp[1].current_value as string;
      }
    });
  }

  private getAirNavigationChargesCurrency(): void {
    this.systemConfigurationService.getAirNavigationChargesCurrency().then((resp: ISystemConfiguration) => {
      if (resp) {
        LocalStorageService.set(AIR_NAVIGATION_CHARGES_CURRENCY, resp.current_value);
        this.$scope.airNavigationChargesCurrency = resp.current_value as string;
      }
    });
  }

  private getANSPCurrency(): void {
    this.systemConfigurationService.getANSPCurrency().then((resp: ISystemConfiguration) => {
      if (resp) {
        LocalStorageService.set(ANSP_CURRENCY, resp.current_value);
        this.$scope.anspCurrency = resp.current_value as string;
        this.$scope.exchangeRateANSPtoUSD = this.currencyExchangeRatesService.getCurrentExchangeRatesByCurrencyCode(this.$scope.anspCurrency);
      }
    });
  }

  private getAdapCharges(data: IFlightMovement): number {
    if (this.$scope.airNavigationChargesCurrency !== 'USD') {
      return (data.approach_charges || 0) / this.$scope.exchangeRateANSPtoUSD;
    } else {
      return data.approach_charges || 0;
    }
  }
}
