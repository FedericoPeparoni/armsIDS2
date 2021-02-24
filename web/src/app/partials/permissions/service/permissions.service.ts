import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { sideBar } from '../../../angular-ids-project/src/components/sideBar/sideBar';

// interfaces
import { IPermissions, IPermissionsMap } from '../permissions.interface';
import { ISideBarCategory, ISidebarLink } from '../../../angular-ids-project/src/components/sideBar/sideBar.interface';

export let endpoint = '/permissions';

export const NO_PERMISSIONS_REQUIRED = 'No permissions required';
export const SELF_CARE_PORTAL_PERMISSIONS = 'No permissions required for self-care portal';
export const SELF_CARE_PORTAL_PERMISSIONS_NOT_AUTHORIZED = 'No permissions required for self-care portal not authorized user';

export class PermissionsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: Array<IPermissions> = [{
    id: null,
    name: null,
    permissions: []
  }];

  private _pMap: IPermissionsMap = <IPermissionsMap>{};

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public generatePermissionsMap(): void {

    // use sidebar to return a two dimensional
    // array with permission objects
    let links = sideBar.map((category: ISideBarCategory) => {
      return category.links.map((link: ISidebarLink) => {
        return { [link.url]: link.permissions };
      });
    });

    // put permission objects into
    // a 'permission map'
    for (let link of links) {
      for (let obj of link) {
        this._pMap = Object.assign(this._pMap, obj);
      }
    }
  }

  public get getPermissionsMap(): IPermissionsMap {
    return this._pMap;
  }

}
