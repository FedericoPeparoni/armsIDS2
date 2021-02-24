// interfaces
import { IScUserRegistration } from '../sc-user-registration.interface';
import { ISystemConfiguration } from '../../../partials/system-configuration/system-configuration.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { UsersService } from '../../users/service/users.service';

export let endpoint: string = 'sc-user-registration';

export class ScUserRegistrationService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IScUserRegistration = {
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
    registration_status: false,
    url: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private usersService: UsersService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public create(editable: IScUserRegistration): ng.IPromise<any> {
    return this.restangular.all(endpoint).customPOST(editable);
  }

  public activate(key: string): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/activate`).customPOST(key);
  }

  public getSettings(): ng.IPromise<ISystemConfiguration[]> {
    return this.restangular.one(`system-configurations/noauth/getPasswordSettings`).get().then((resp: ISystemConfiguration) => {
      return this.restangular.stripRestangular(resp);
    }, (err: any) => {
      return null;
    });
  }

}
