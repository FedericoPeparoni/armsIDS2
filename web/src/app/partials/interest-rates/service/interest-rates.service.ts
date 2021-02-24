// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// interfaces
import { IInterestRate } from '../interest-rates.interface';

export const endpoint: string = 'interest-rate';

export class InterestRatesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IInterestRate = {
    id: null,
    default_interest_specification: null,
    default_interest_application: null,
    default_interest_grace_period: null,
    default_foreign_interest_specified_percentage: null,
    default_national_interest_specified_percentage: null,
    default_foreign_interest_applied_percentage: null,
    default_national_interest_applied_percentage: null,
    punitive_interest_specification: null,
    punitive_interest_application: null,
    punitive_interest_grace_period: null,
    punitive_interest_specified_percentage: null,
    punitive_interest_applied_percentage: null,
    start_date: null,
    end_date: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
