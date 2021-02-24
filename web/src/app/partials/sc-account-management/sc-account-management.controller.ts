// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ScAccountManagementService } from './service/sc-account-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { TypesService } from '../types/service/types.service';
import { UsersService } from '../users/service/users.service';

// interfaces
import { IAccount, IAccountSpring } from '../accounts/accounts.interface';
import { IType } from '../types/types.interface';
import { IUser, IUserSpring } from '../users/users.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class ScAccountManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, scAccountManagementService: ScAccountManagementService, private systemConfigurationService: SystemConfigurationService,
    private typesService: TypesService, private usersService: UsersService) {

    super($scope, scAccountManagementService);
    super.setup({ refresh: false });
    super.list({}, `filter=true&selfCareAccounts=true`);
    this.typesService.listAll().then((types: Array<IType>) => {
      types.forEach((element: IType) => {
        if (element.name === 'GeneralAviation') {
          this.$scope.accountType = element;
        }
      });
      this.setDefaultFields();
    });

    $scope.$watch('filter', () => this.getFilterParameters());

    $scope.needAdminApproval =
      this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_ADMIN_APPROVAL_FOR_SC_ACCOUNTS);

    $scope.selfCareUserList = [];

    usersService.listAll().then((users: IUserSpring) => {
      users.content.forEach((user: IUser) => {
        if (user.is_selfcare_user && user.registration_status) {
          $scope.selfCareUserList.push(user);
        }
      });
    });

    $scope.checkCreditLimit = () => this.checkCreditLimit();
    $scope.setDefaultFields = () => this.setDefaultFields();
    $scope.create = (account: IAccount) => super.create(account).then(() => this.setDefaultFields());
    $scope.update = (account: IAccount, id: number) => super.update(account, id).then(() => this.setDefaultFields());
    $scope.refresh = () => this.refreshOverride();
  }

  private setDefaultFields(): void {
    this.checkCreditLimit();
    this.$scope.editable.payment_terms = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_PAYMENT_TERMS);
    this.$scope.editable.monthly_overdue_penalty_rate = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_MONTHLY_PENALTY);
    this.$scope.anspCurrency = <string>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    this.$scope.editable.account_type = this.$scope.accountType;
  }

  private checkCreditLimit(): void {
    if (this.$scope.editable.cash_account === true) {
      this.$scope.editable.credit_limit = 0;
      this.$scope.minCreditLimit = 0;
      this.$scope.maxCreditLimit = 0;
    } else {
      this.$scope.editable.credit_limit = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_CREDIT_LIMIT);
      this.$scope.minCreditLimit = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_MIN_CREDIT_NOTE);
      this.$scope.maxCreditLimit = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_MAX_CREDIT_NOTE);
    }
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      filter: this.$scope.filter,
      cash: this.$scope.accountTypeFilter,
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      selfCareAccounts: this.$scope.selfCareAccounts,
      invoices: this.$scope.accountFilter
    };
  }

  private refreshOverride(): ng.IPromise<IAccountSpring> {
    let request: ng.IPromise<IAccountSpring>;
    this.getFilterParameters();
    request = super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());

    return request;
  }

}
