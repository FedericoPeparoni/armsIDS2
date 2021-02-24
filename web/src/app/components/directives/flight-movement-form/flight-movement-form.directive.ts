// service
import { SystemConfigurationService } from '../../../partials/system-configuration/service/system-configuration.service';
import { CountryManagementService } from '../../../partials/country-management/service/country-management.service';
import { UsersService } from '../../../partials/users/service/users.service';
import { CurrencyManagementService } from '../../../partials/currency-management/service/currency-management.service';
import { BillingCentreManagementService } from '../../../partials/billing-centre-management/service/billing-centre-management.service';

// constants
import { SysConfigConstants } from '../../../partials/system-configuration/system-configuration.constants';
import { FlightMovementManagementService } from '../../../partials/flight-movement-management/service/flight-movement-management.service';
import { IAccount } from '../../../partials/accounts/accounts.interface';
import { IAircraftRegistration } from '../../../partials/aircraft-registration/aircraft-registration.interface';
import { ICountry, IAerodromePrefix } from '../../../partials/country-management/country-management.interface';
import { IUser } from '../../../partials/users/users.interface';
import { ICurrency } from '../../../partials/currency-management/currency-management.interface';
import { IFlightMovement } from '../../../partials/flight-movement-management/flight-movement-management.interface';

/** @ngInject */
export function flightMovementForm(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/flight-movement-form/flight-movement-form.directive.html',
    controller: FlightMovementFormController,
    scope: {
      flightMovement: '=',
      manual: '=',
      disabled: '=?',
      shouldShowCharge: '=?',
      distanceUnitOfMeasure: '=?',
      showPassengerCounts: '=?', // boolean; defaults to true
      showDeletionReason: '=?', // boolean; defaults to true
      showRadarRoute: '=?', // boolean; defaults to true
      showFlightNotes: '=?' // boolean; defaults to true
    },
    replace: true
  };
}

/** @ngInject */
class FlightMovementFormController {

  public taspLabel: string;
  private permissions: Array<string>;

  constructor(protected $scope: angular.IScope, systemConfigurationService: SystemConfigurationService,
    protected flightMovementManagementService: FlightMovementManagementService, countryManagementService: CountryManagementService,
    private usersService: UsersService, private currencyManagementService: CurrencyManagementService,
    private billingCentreManagementService: BillingCentreManagementService) {

    $scope.$watch('flightMovement.item18_reg_num', (val: string) => {
      if ($scope.flightMovement) {
        $scope.flightMovement.item18_reg_num = this.cleanDataField($scope.flightMovement.item18_reg_num, val);
        this.resetValues();
      }
    });

    $scope.$watch('flightMovement.flight_id', (val: string) => {
      if ($scope.flightMovement) {
        $scope.flightMovement.flight_id = this.cleanDataField($scope.flightMovement.flight_id, val);
        this.resetValues();
      }
    });

    $scope.$watch('flightMovement.id', () => {
      this.resetValues();
    });

    $scope.$watch('flightMovement.item18_operator', (val: string) => {
      if ($scope.flightMovement) {
        this.resetValues();
      }
    });

    $scope.$watch('flightMovement.dest_ad', () => {
      this.checkIfDomesticAerodrome();
    });

    this.usersService.current.then((user: IUser) => {
        this.$scope.user = user ? user : {};
        this.permissions = user ? user.permissions : [];
        this.$scope.hasPermission = (permission: string) => this.hasPermission(permission);
    });

    // get tasp/adhoc label text
    $scope.taspLabel = systemConfigurationService.getValueByName(<any>SysConfigConstants.TASP_FEES_LABEL);
    this.currencyManagementService.getANSPCurrencyAndUSD().then((currencies: ICurrency[]) => {
      $scope.taspCurrenciesList = currencies;
      if ($scope.flightMovement && $scope.flightMovement.tasp_charge_currency) {
        $scope.flightMovement.tasp_charge_currency = $scope.taspCurrenciesList.content.find((currency: ICurrency) => currency.currency_code === 'USD');
      }
    });

    const countryCode = systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_COUNTRY_CODE);
    if (countryCode) {
      countryManagementService.findCountryByCountryCode(countryCode).then((domesticCountry: ICountry) => {
          if (domesticCountry) {
              $scope.aerodromePrefixes = domesticCountry.aerodrome_prefixes;
              this.checkIfDomesticAerodrome();
          }
      });
    }

    $scope.flightLevelRequired = systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.VALIDATE_FLIGHT_LEVEL_AIRSPACE);

    // default value for distance units
    if (!$scope.distanceUnitOfMeasure) {
      $scope.distanceUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.DISTANCE_UNIT_OF_MEASURE) as string;
    }

    // default lookup function
    if (!$scope.shouldShowCharge) {
      $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge (chargeType);
    }

    // default showPassengerCharges
    if (typeof $scope.showPassengerCounts !== 'boolean') {
      $scope.showPassengerCounts = true;
    }

    // default showDeletionReason
    if (typeof $scope.showDeletionReason !== 'boolean') {
      $scope.showDeletionReason = true;
    }

    // default showRadarRoute
    if (typeof $scope.showRadarRoute !== 'boolean') {
      $scope.showRadarRoute = true;
    }

    // default showFlightNotes
    if (typeof $scope.showFlightNotes !== 'boolean') {
      $scope.showFlightNotes = true;
    }

    $scope.checkAccountMatchIdentifier = (selectedAccountId: string) => this.checkAccountMatchIdentifier(selectedAccountId);
    $scope.checkAircraftTypeMatchIdentifier = (selectedAircraftType: string) => this.checkAircraftTypeMatchIdentifier(selectedAircraftType);
    $scope.checkBillingCentreMismatch = (flightMovement: IFlightMovement) => this.checkBillingCentreMismatch(flightMovement);
    $scope.getDataByIdentifier = () => this.setDataByIdentifier();
  }

  private resetValues(): void {
    this.$scope.showAccountMismatchError = false;
    this.$scope.showAircraftTypeMismatchError = false;
    this.$scope.showBillingCentreMismatchError = false;
    this.$scope.accountIdByIdentifier = null;
    this.$scope.aircraftTypeByIdentifier = null;
    this.$scope.accountIdByOperator = null;
  }

  // only numbers and letters allowed, no special characters
  // field must also be uppercase and without spaces
  private cleanDataField(field: any, val: any): string {
    if (val) {
      return val.replace(/[^A-Za-z0-9]+/g, '').trim();
    }
    return null;
  }

  // set account and aircraft type based on Flight Id or Registration Number
  private async setDataByIdentifier(): Promise<any> {

    await this.getDataByIdentifier();

    // set associated account by operator or flightId/regNum identifier if found, skip if account selection is disabled
    if (!this.$scope.disabled || this.$scope.disabled && !this.$scope.disabled.account) {
      this.$scope.flightMovement.associated_account_id = this.$scope.accountIdByOperator || this.$scope.accountIdByIdentifier || this.$scope.flightMovement.associated_account_id;
    }

    // set aircraft type from regNum identifier if found
    this.$scope.flightMovement.aircraft_type = this.$scope.aircraftTypeByIdentifier
      ? this.$scope.aircraftTypeByIdentifier
      : this.$scope.flightMovement.aircraft_type;

    // display warning message if selected account does not match flightId or regNum
    if (this.$scope.flightMovement.associated_account_id && this.$scope.accountIdByIdentifier) {
          this.$scope.showAccountMismatchError = this.$scope.flightMovement.associated_account_id !== this.$scope.accountIdByIdentifier;
    }
  }

  // get account and aircraft type based on Flight Id or Registration Number
  // the account returned based on the Registration Number overrides the account returned based on the Flight Id
  private async getDataByIdentifier(): Promise<any> {

    if (this.$scope.flightMovement.item18_reg_num && this.$scope.flightMovement.date_of_flight) {
      await this.getAircraftRegistrationFromRegNumber();
    }

    if (!this.$scope.accountIdByIdentifier && this.$scope.flightMovement.flight_id) {
      await this.getAccountFromIdentifier();
    }

    if (this.$scope.flightMovement.item18_operator) {
      await this.getAccountFromOperator();
    }
  }

  private async getAircraftRegistrationFromRegNumber(): Promise<any> {
    await this.flightMovementManagementService.getAircraftRegistrationFromRegNumber(
      this.$scope.flightMovement.item18_reg_num, this.$scope.flightMovement.date_of_flight.toISOString())
      .then((aircraftRegistration: IAircraftRegistration) => {
        this.$scope.accountIdByIdentifier = aircraftRegistration.account.id;
        this.$scope.aircraftTypeByIdentifier = aircraftRegistration.aircraft_type.aircraft_type;
      }).catch(() => {
        this.$scope.accountIdByIdentifier = null;
        this.$scope.aircraftTypeByIdentifier = null;
      });
  }

  private async getAccountFromIdentifier(): Promise<any> {
    await this.flightMovementManagementService.getAccountFromIdentifier(this.$scope.flightMovement.flight_id).then((account: IAccount) => {
      this.$scope.accountIdByIdentifier = account.id;
    }).catch(() => {
      this.$scope.accountIdByIdentifier = null;
    });
  }

  private async getAccountFromOperator(): Promise<any> {
    await this.flightMovementManagementService.getAccountFromOperator(this.$scope.flightMovement.item18_operator)
      .then((account: IAccount) => this.$scope.accountIdByOperator = account.id)
      .catch(() => this.$scope.accountIdByOperator = null);
  }

  private checkAccountMatchIdentifier(selectedAccountId: string): void {
    if (!this.$scope.accountIdByIdentifier) {
      // check for match based on flight id or reg num
      this.getDataByIdentifier();
      this.$scope.showAccountMismatchError = this.$scope.accountIdByIdentifier && this.$scope.accountIdByIdentifier !== selectedAccountId;
    } else {
      this.$scope.showAccountMismatchError = this.$scope.accountIdByIdentifier && this.$scope.accountIdByIdentifier !== selectedAccountId;
    }
  }

  private checkAircraftTypeMatchIdentifier(selectedAircraftType: string): void {
    if (!this.$scope.aircraftTypeByIdentifier && this.$scope.flightMovement.item18_reg_num && this.$scope.flightMovement.date_of_flight) {
      // check for match based on reg num
      this.getAircraftRegistrationFromRegNumber();
      this.$scope.showAircraftTypeMismatchError = this.$scope.aircraftTypeByIdentifier && this.$scope.aircraftTypeByIdentifier !== selectedAircraftType;
    } else {
      this.$scope.showAircraftTypeMismatchError = this.$scope.aircraftTypeByIdentifier && this.$scope.aircraftTypeByIdentifier !== selectedAircraftType;
    }
  }

  private checkIfDomesticAerodrome(): void {
      this.$scope.domesticDestAd = this.$scope.aerodromePrefixes && this.$scope.aerodromePrefixes.length
        && this.$scope.flightMovement && this.$scope.flightMovement.dest_ad ?
          this.$scope.aerodromePrefixes.some(((aerodromePrefix: IAerodromePrefix) =>
          this.$scope.flightMovement.dest_ad.startsWith(aerodromePrefix.aerodrome_prefix))) || this.$scope.flightMovement.dest_ad === 'ZZZZ' : false;
  }

  private async checkBillingCentreMismatch(fm: IFlightMovement): Promise<any> {
    const hasAerodromes = Boolean((fm.dep_ad && fm.dep_ad.length) && (fm.dest_ad && fm.dest_ad.length));
    const hasItem18Aerodromes = Boolean((fm.item18_dep && fm.item18_dep.length) && (fm.item18_dest && fm.item18_dest.length));
    const hasItem18Dep = Boolean ((fm.item18_dep && fm.item18_dep.length) && (fm.dest_ad && fm.dest_ad.length));
    const hasItem18Dest = Boolean((fm.dep_ad && fm.dep_ad.length) && (fm.item18_dest && fm.item18_dest.length));

    let flightBillingCentre = null;
    if (hasAerodromes) {
      flightBillingCentre = await this.billingCentreManagementService.getBillingCentreByAerodromes(fm.dep_ad, fm.dest_ad);
    } else if (hasItem18Aerodromes) {
      flightBillingCentre = await this.billingCentreManagementService.getBillingCentreByAerodromes(fm.item18_dep, fm.item18_dest);
    } else if (hasItem18Dep) {
      flightBillingCentre = await this.billingCentreManagementService.getBillingCentreByAerodromes(fm.item18_dep, fm.dest_ad);
    } else if (hasItem18Dest) {
      flightBillingCentre = await this.billingCentreManagementService.getBillingCentreByAerodromes(fm.dep_ad, fm.item18_dest);
    }

    if (flightBillingCentre && flightBillingCentre.id !== this.$scope.user.billing_center.id) {
      this.$scope.showBillingCentreMismatchError = true;
    } else {
      this.$scope.showBillingCentreMismatchError = false;
    }
  }

  /**
   * Returns whether the current user has this permission.
   */
  private hasPermission(permission: string): boolean {
    return this.permissions && this.permissions.indexOf(permission) > -1;
  }
}
