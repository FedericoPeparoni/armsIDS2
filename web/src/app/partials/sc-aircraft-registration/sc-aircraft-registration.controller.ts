// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// services
import { ScAircraftRegistrationService } from './service/sc-aircraft-registration.service';
import { FlightMovementManagementService } from '../flight-movement-management/service/flight-movement-management.service';
import { AircraftTypeManagementService } from '../aircraft-type-management/service/aircraft-type-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interface
import { IAircraftType, IAircraftTypeSpring } from '../aircraft-type-management/aircraft-type-management.interface';
import { ICountry } from '../country-management/country-management.interface';
import { IAircraftRegistration } from '../aircraft-registration/aircraft-registration.interface';
import { IExtendableError, IError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class ScAircraftRegistrationController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: ng.IScope,
    private flightMovementManagementService: FlightMovementManagementService,
    private scAircraftRegistrationService: ScAircraftRegistrationService,
    private aircraftTypeManagementService: AircraftTypeManagementService,
    private systemConfigurationService: SystemConfigurationService,
    private $state: angular.ui.IStateService,
    protected $translate: angular.translate.ITranslateService, private customDate: CustomDate) {

    // setup
    super($scope, scAircraftRegistrationService, null, 'File Name');
    super.setup();
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);
    aircraftTypeManagementService.listAll().then((data: IAircraftTypeSpring) => this.$scope.aircraftTypesList = data.content);

    $scope.customDate = this.customDate.returnDateFormatStr(false);
    $scope.needAdminApproval = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION);

    $scope.notification_text = 'I am accepting responsibility for all aviation charges for that aircraft over the ANSP’s FIR for the period of the registration';

    $scope.getAircraftTypeByLatestRegistrationNumber = (registrationNumber: string) => this.getAircraftTypeByLatestRegistrationNumber(registrationNumber);
    $scope.getCountryByRegistrationNumberPrefix = (prefix: string) => this.getCountryByRegistrationNumberPrefix(prefix);

    this.getFilterParameters();

    // functions
    $scope.addDate = (aircraft: IAircraftRegistration) => this.addDate(aircraft); // avoid using edit function to prevent table from automatically updating
    $scope.convertMtowProperty = (aircraftType: IAircraftType) => this.convertMtowProperty(aircraftType);
    $scope.create = (aircraft: IAircraftRegistration, control: any, controlCoa: any) => this.createOverride(aircraft, control, controlCoa);
    $scope.edit = (item: IAircraftRegistration) => { super.edit(systemConfigurationService.convertMtowProperty(item, 'mtow_override')); };
    $scope.setEditable = (aircraft: IAircraftRegistration) => this.setEditable(aircraft);
    $scope.update = (aircraft: IAircraftRegistration, control: any, controlCoa: any) => this.updateOverride(aircraft, control, controlCoa);
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.setStatus = (item: IAircraftRegistration) => this.setStatus(item);
    $scope.setLocal = (country: ICountry) => this.setLocal(country);

    // matching
    $scope.country = null;
    $scope.countryMatch = false;
    $scope.error = null;

    // if country override field is modified, check if form is valid
    $scope.$watch('editable.country_override', () => this.handleOverrideError());

    $scope.$watch('editable.country_of_registration', () => {
      const { country } = this.$scope;

      // if a country is returned from the registration number and its name matches the country of registration
      if (country && country.country_name === this.$scope.editable.country_of_registration.country_name) {
        this.$scope.editable.country_override = false;
        this.$scope.countryMatch = true;
      } else {
        this.$scope.countryMatch = false;
        this.handleOverrideError();
      }
    });
  }

  protected reset(): void {
    if (typeof this.$scope.control !== 'undefined') {
      this.$scope.control.reset();
      this.$scope.countryMatch = false;
    }

    if (this.$scope.control_coa) {
      this.$scope.control_coa.reset();
    }

    super.reset();
  }

  protected createOverride(aircraft: IAircraftRegistration, control: any, controlCoa: any): void {
    this.setDates(aircraft, control, controlCoa);
    super.create(aircraft).then((createdAircraft: IAircraftRegistration) => {
      if (this.$state && this.$state.params.after) { // go back to previous page (if state params exist)
        this.$state.go(this.$state.params.after.create.goTo.state, {
          accountId: createdAircraft.account.id
        });
      }
    });
  }

  protected updateOverride(aircraft: IAircraftRegistration, control: any, controlCoa: any): void {
    this.setDates(aircraft, control, controlCoa);
    super.update(aircraft, aircraft.id);
  }

  protected addDate(aircraftRegistration: IAircraftRegistration): void {
    if (aircraftRegistration.registration_start_date) {
      const startDate = new Date(aircraftRegistration.registration_start_date);
      this.$scope.control.setUTCStartDate(startDate);
    }

    if (aircraftRegistration.registration_expiry_date) {
      const endDate = new Date(aircraftRegistration.registration_expiry_date);
      this.$scope.control.setUTCEndDate(endDate);
    }

    if (aircraftRegistration.coa_issue_date && aircraftRegistration.coa_expiry_date) {
      const coaStartDate = new Date(aircraftRegistration.coa_issue_date);
      this.$scope.control_coa.setUTCStartDate(coaStartDate);

      const coaEndDate = new Date(aircraftRegistration.coa_expiry_date);
      this.$scope.control_coa.setUTCEndDate(coaEndDate);
    } else {
      this.$scope.control_coa.reset();
    }
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      searchFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  // when a user chooses an entry to edit
  private setEditable(aircraft: IAircraftRegistration): void {
    // await API call for reg number to check for country match
    this.scAircraftRegistrationService.getCountryByRegistrationNumberPrefix(aircraft.registration_number).then((country: ICountry) => {
      this.$scope.countryMatch = true;
      this.$scope.country = country;
    }).catch(() => {
      this.$scope.countryMatch = false;
      this.$scope.country = null;
    }).finally(() => {
      this.$scope.edit(aircraft);
      this.$scope.addDate(aircraft);
      this.$scope.form.$setUntouched();
    });
  }

  private convertMtowProperty(aircraftType: IAircraftType): void {
    this.$scope.editable.mtow_override = this.systemConfigurationService
      .convertMtowProperty(aircraftType, 'maximum_takeoff_weight')
      .maximum_takeoff_weight;
  }

  private getAircraftTypeByLatestRegistrationNumber(registrationNumber: string): void {
    if (registrationNumber && registrationNumber.length >= 2) {
      this.flightMovementManagementService.getAircraftTypeByLatestRegistrationNumber(registrationNumber).then((aircraftType: IAircraftType) => {
        if (aircraftType) {
          this.$scope.editable.mtow_override = aircraftType.maximum_takeoff_weight;

          this.$scope.editable = this.systemConfigurationService.convertMtowProperty(this.$scope.editable, 'mtow_override');
          // we have to search to see if we can find the right aircraft type
          for (let value of this.$scope.aircraftTypesList) {
            if (value.aircraft_type === aircraftType.aircraft_type) {
              // update the aircraft type field
              this.$scope.editable.aircraft_type = value;
            }
          }
        }
      });
    }
  }

  // if not overridden, attempts to find a
  // country by the registration number prefix
  private getCountryByRegistrationNumberPrefix(prefix: string): void {
    if (prefix && prefix.length >= 2 && !this.$scope.editable.country_override) {
      this.scAircraftRegistrationService.getCountryByRegistrationNumberPrefix(prefix).then((country: ICountry) => {
        if (country) {
          this.setLocal(country);
          this.$scope.editable.country_of_registration = country;
          this.$scope.countryMatch = true;
          this.$scope.country = country;
        }
      }).catch(() => {
        if (!this.$scope.editable.country_override) { this.$scope.editable.country_of_registration = null; }
        this.$scope.countryMatch = false;
        this.$scope.country = null;
      }).finally(() => {
        this.handleOverrideError();
      });
    }
  }

  private handleOverrideError(): void {
    const { country, countryMatch, editable } = this.$scope;
    const { registration_number, country_of_registration, country_override } = editable;

    // if country is overriden or there is a country match
    if (country_override || (country && countryMatch)) {
      this.$scope.error = null;
      // if no country match or override but the user has entered in registration number and country
    } else if (registration_number && country_of_registration && country_of_registration.id) {
      this.$scope.error = <IExtendableError>{
        error: {
          data: <IError>{
            error: this.$translate.instant('Override country of registration'),
            error_description: this.$translate.instant('Registration number and country of registration match error. Override country of registration to continue.'),
            field_errors: null
          }
        }
      };
    }
  }

  private setStatus(item: any): string {
    if (item.sc_request_type === 'create') {
      return 'create pending';
    } else if (item.sc_request_type === 'update') {
      return 'update pending';
    } else if (item.sc_request_type === 'delete') {
      return 'delete pending';
    } else if (!item.sc_request_type) {
      return 'approved';
    } else {
      return '';
    }
  }

  private setDates (aircraft: IAircraftRegistration, control: any, controlCoa: any): void {
    aircraft.registration_start_date = control.getUTCStartDate();
    aircraft.registration_expiry_date = control.getUTCEndDate();

    aircraft.coa_issue_date = controlCoa.getUTCStartDate();
    aircraft.coa_expiry_date = controlCoa.getUTCEndDate();
  }

  private setLocal(country: ICountry): void {
    const countryANSP = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_COUNTRY_CODE);
    this.$scope.editable.is_local = country.country_code === countryANSP;
  }
}

