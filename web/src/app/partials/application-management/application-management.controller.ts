// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ApplicationManagementService } from './service/application-management.service';

// interfaces
import { IRouteCache } from './application-management.interface';

export class ApplicationManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private applicationManagementService: ApplicationManagementService) {
    super($scope, applicationManagementService);
    super.setup({ refresh: false });

    this.getCurrentEntries();
    this.getNumberToRetain();

    $scope.clearCache = () => applicationManagementService.clearCurrentCache().then(() => this.getCurrentEntries());
    $scope.updateNumberToRetain = (numberToRetain: number) => applicationManagementService.updateNumberToRetain(numberToRetain)
          .then((data: IRouteCache) => $scope.numberToRetain = data.number_of_retention);
  }

  private getCurrentEntries(): void {
    super.list().then((data: IRouteCache) => {
      this.$scope.currentEntries = data.count;
    });
  }

  private getNumberToRetain(): void {
    this.applicationManagementService.getNumberToRetain()
      .then((data: IRouteCache) => this.$scope.numberToRetain = data.number_of_retention);
  }

}
