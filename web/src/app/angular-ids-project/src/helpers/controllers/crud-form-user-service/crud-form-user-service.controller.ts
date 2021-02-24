// controllers
import { CRUDFormController } from '../crud-form/crud-form.controller';

// interfaces
import { IUser } from '../../../../../partials/users/users.interface';

// services
import { UsersService } from '../../../../../partials/users/service/users.service';

// decorators
import { Inject } from '../../../../../components/decorators/inject';

// extends CRUDFormController to also include User Service (due to permissions that most controllers need)
@Inject(UsersService)
export class CRUDFormControllerUserService extends CRUDFormController {

  protected permissions: Array<string> = [];

  // because we don't know what the $scope or service will be from, it has to be of type `any`
  constructor(protected $scope: any, protected service: any, ... services: Array<any>) {
    super(service);
    new services[0](service.restangular).current.then((user: IUser) => {
      if (user) {
        this.permissions = user.permissions;
        $scope.hasPermission = (permission: string): boolean => this.hasPermission(permission);
      } else {
        this.permissions = null;
      }
    });
  }

  // returns whether the current user has this permission
  private hasPermission(permission: string): boolean {
    return this.permissions.indexOf(permission) > -1;
  }
}
