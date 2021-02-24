// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// interfaces
import { IUser } from '../users.interface';
import { IRestangularResponse } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IRole } from '../../roles/roles.interface';
import { OAuthService } from '../../../components/services/oauth/oauth.service';
import { IPasswordChange } from '../../password-change/password-change.interface';
import { IUserInfoChange } from '../../current-user/current-user.interface';

export const endpoint = 'users';

export class UsersService extends CRUDService {

  protected restangular: restangular.IService;

  private userModel: IUser = {
    email: null,
    id: null,
    login: null,
    job_title: null,
    name: null,
    permissions: [],
    roles: [],
    contact_information: null,
    sms_number: null,
    billing_center: null,
    language: null,
    is_selfcare_user: false,
    force_password_change: false,
    registration_status: true
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private $location: ng.ILocationService, private windowLocationService: Location, private oAuthService: OAuthService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this.userModel);
  }

  public get current(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/current`).get()
      .catch((error: IRestangularResponse) => {
        if (this.$location) {
          if (this.$location.path().startsWith('/sc-') || this.windowLocationService.pathname.endsWith('/selfcare/')) {
            if (this.$location.path() === '/sc-user-registration-activate') {
              this.$location.path('sc-user-registration-activate');
            } else if (this.$location.path() !== '/sc-home-page') {
              this.oAuthService.logout();
            }
          } else {
            this.oAuthService.logout();
          }
        }
      });
  }

  public update(user: IUser, id: number): ng.IPromise<void> {
    return super.update(user, id)
      .then(() => this.updateRoles(id, user.roles));
  }

  public create(user: IUser): ng.IPromise<void> {
    const roles = user.roles; // the user object is lost, therefore we set a variable.  this seems to only be an issue with create
    return super.create(user)
      .then((newUser: IUser) => this.updateRoles(newUser.id, roles));
  }

  public setCurrentUserLanguage(language: string): void {
    this.restangular.one(`${endpoint}/current`).get().then((resp: any) => {
      this.restangular.one(`${endpoint}/setCurrentUserLanguage/${resp.id}/${language}`).put();
    });
  }

  public updateRoles(userId: number, roles: IRole[]): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/${userId}/roles`).customPUT(roles);
  }

  public recoverPassword(email: string): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/passwordRecovery`).customPOST(email);
  }

  public updatePassword(changePassword: IPasswordChange|IUserInfoChange): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/updatePassword`).customPUT(changePassword);
  }

  public getListInternalUsers(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/list?selfCareUser=false`).get();
  }

  public getListLightInternalUsers(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/listLight?selfCareUser=false`).get();
  }

  public getListWebUsers(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/list?selfCareUser=true`).get();
  }

  public getListLightWebUsers(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/listLight?selfCareUser=true`).get();
  }
}
