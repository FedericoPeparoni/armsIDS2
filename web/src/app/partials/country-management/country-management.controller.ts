// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { ICountryManagementScope, ICountry } from './country-management.interface';

// services
import { CountryManagementService } from './service/country-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

export class CountryManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(
    protected $scope: ICountryManagementScope,
    private countryManagementService: CountryManagementService,
    private systemConfigurationService: SystemConfigurationService
  ) {
    super($scope, countryManagementService);
    super.setup();

    $scope.create = (editable: ICountry) => this.createOverride(editable);
    $scope.update = (editable: ICountry, id: number) => this.updateOverride(editable, id);
    $scope.edit = (editable: ICountry) => this.editOverride(editable);
    $scope.refresh = () => this.refreshOverride();
    $scope.isLocaleEnglish = () => systemConfigurationService.isLocaleEnglish();
    $scope.prefixArrayToString = (list: any, key: string) => this.prefixArrayToString(list, key);
    this.getFilterParameters();
  }

  /**
   * Override create to format prefixes
   * @param  {} editable
   * @returns void
   */
  private createOverride(editable: ICountry): void {
    super.create(this.formatPrefixes(editable));
  }

  /**
   * Override update to format prefixes
   * @param  {} editable
   * @param  {} id
   * @returns void
   */
  private updateOverride(editable: ICountry, id: number): void {
    super.update(this.formatPrefixes(editable), id);
  }

  /**
   * Override edit to display
   * prefixes as a comma separated
   * string
   * @param  {ICountry} editable
   * @returns void
   */
  private editOverride(editable: ICountry): void {
    const country = angular.copy(editable);
    const { aircraft_registration_prefixes, aerodrome_prefixes } = country;

    country.aircraft_registration_prefixes_input = aircraft_registration_prefixes
      ? this.prefixArrayToString(aircraft_registration_prefixes, 'aircraft_registration_prefix')
      : '';

    country.aerodrome_prefixes_input = aerodrome_prefixes
      ? this.prefixArrayToString(aerodrome_prefixes, 'aerodrome_prefix')
      : '';

    super.edit(country);
  }

  /**
   * Changes comma-separated strings of 
   * prefixes to arrays of prefixes
   * @param  {ICountry} editable
   * @returns ICountry
   */
  private formatPrefixes(editable: ICountry): ICountry {
    const country = angular.copy(editable);
    const { aircraft_registration_prefixes_input, aerodrome_prefixes_input } = country;

    if (this.isString(aircraft_registration_prefixes_input)) {
      country.aircraft_registration_prefixes = this.prefixStringToArray(
        aircraft_registration_prefixes_input,
        'aircraft_registration_prefix',
        country
      );
    }

    if (this.isString(aerodrome_prefixes_input)) {
      country.aerodrome_prefixes = this.prefixStringToArray(
        aerodrome_prefixes_input,
        'aerodrome_prefix',
        country
      );
    }

    return country;
  }

  private prefixArrayToString(list: any, key: string): string {
    return list.map((item: any) => item[key]).join(', ');
  }

  private prefixStringToArray(list: string, key: string, country: ICountry): any {
    const { id, country_name, country_code } = country;

    return list.split(',').map((prefix: string) => {
      if (prefix.trim().length) {
        return { [key]: prefix.trim(), country_code: { id, country_name, country_code } };
      }
    });
  }

  /**
   * type guard checks if
   * value is a string
   * @param  {string|string[]} prefix
   * @returns prefix is string
   */
  private isString(prefix: any): prefix is string {
    return typeof prefix === 'string';
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * This will add appropriate filters from scope
   * for parent refresh parameters.
   */
  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
