// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { AMHSAccountsService } from './service/amhs-accounts.service';
import { IAMHSAccount } from './amhs-accounts.interface';

export class AMHSAccountsController extends CRUDFormControllerUserService {
  /* @ngInject */
  constructor(protected $scope: ng.IScope, private amhsAccountsService: AMHSAccountsService) {
    super($scope, amhsAccountsService);
    super.setup();

    // overrides
    $scope.reset = () => this.resetOverride();
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.create = (editable: IAMHSAccount) => this.createOverride(editable);
    $scope.update = (editable: IAMHSAccount, id: number) => this.updateOverride(editable, id);
    $scope.delete = (id: number) => this.deleteOverride(id);

    // finish setup
    this.getFilterParameters();
    this.setDefaults();
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

  private resetOverride(): void {
    super.reset();
    this.setDefaults();
  }

  private setDefaults(): void {
      // set defaults for the form
      this.$scope.editable.active = true;
      this.$scope.editable.addr = '';
      this.$scope.editable.descr = '';
      this.$scope.editable.passwd = '';
      this.$scope.editable.allow_mta_conn = true;
      this.$scope.editable.svc_hold_for_delivery = true;

      // validation defaults
      this.$scope.valid = true;
      this.$scope.pattern = '';
  }

  private async createOverride(editable: IAMHSAccount): Promise<any> {
    await super.create(editable);
    this.setDefaults();
  }

  private async updateOverride(editable: IAMHSAccount, id: number): Promise<any> {
    await super.update(editable, id);
    this.setDefaults();
  }

  private async deleteOverride(id: number): Promise<any> {
    await super.delete(id);
    this.setDefaults();
  }

}
