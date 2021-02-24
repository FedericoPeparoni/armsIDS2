// interfaces
import { IUser } from '../../users/users.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'sc-user-management';

export class ScUserManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IUser = {
    id: null,
    email: null,
    login: null,
    job_title: null,
    name: null,
    permissions: [],
    roles: [],
    contact_information: null,
    sms_number: null,
    billing_center: null,
    language: null,
    is_selfcare_user: true,
    force_password_change: false,
    registration_status: false
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
