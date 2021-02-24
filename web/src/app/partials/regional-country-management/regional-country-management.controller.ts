// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRegionalCountryScope } from './regional-country-management.interface';
import { ICountrySpring } from '../country-management/country-management.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IRegionalCountrySpring } from './regional-country-management.interface';

// services
import { RegionalCountryManagementService } from './service/regional-country-management.service';
import { CountryManagementService } from '../country-management/service/country-management.service';

export class RegionalCountryManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IRegionalCountryScope, private regionalCountryManagementService: RegionalCountryManagementService, private countryManagementService: CountryManagementService) {
    super($scope, regionalCountryManagementService);
    // setupParams needed so params can be applied from outside controller
    super.setup({ refresh: false });

    $scope.editableCountryList = {
      content: []
    };
    $scope.regionalCountryList = {
      content: []
    };
    countryManagementService.listAll().then((listOfCountries: ICountrySpring) => $scope.countryList = listOfCountries.content); // retrieve total country list
    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();
    this.list({});
  }

  public list(params: ISpringPageableParams, queryString?: string): ng.IPromise<void> {
    return super.list(params, queryString).then((data: IRegionalCountrySpring) => {
      this.$scope.regionalCountryList = angular.copy(data);
      this.$scope.editableCountryList = angular.copy(data);
    });
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      searchFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString()).then((data: IRegionalCountrySpring) => {
      this.$scope.regionalCountryList = angular.copy(data);
      this.$scope.editableCountryList = angular.copy(data);
    });
  }
}
