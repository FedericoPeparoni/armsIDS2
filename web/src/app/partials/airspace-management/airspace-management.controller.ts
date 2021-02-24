// constants
import { MapFlyToEntityType } from '../map/map.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IAirspace, IAirspaceManagementScope } from './airspace-management.interface';

// services
import { AirspaceManagementService } from '../airspace-management/service/airspace-management.service';

export class AirspaceManagementController extends CRUDFormControllerUserService {

  private listFromNavDB: IAirspace[];

  /* @ngInject */
  constructor(protected $scope: IAirspaceManagementScope, private $rootScope: ng.IRootScopeService, private airspaceManagementService: AirspaceManagementService) {
    super($scope, airspaceManagementService);
    super.setup();
    this.getFilterParameters();
    $scope.loadAirspaces = () => this.loadAirspaces();
    $scope.edit = (item: IAirspace) => {
      this.$scope.openForm = true;
      this.$scope.selectedItemId = item.id;
      super.edit(item);
    };

    $scope.delete = (id: number) => {
      super.delete(id).then(() => this.$rootScope.$broadcast('map.refreshLayer', 'Airspaces'));
      this.$scope.selectedItemId = null;
    };

    $scope.create = (id: number) => super.create(id).then(() => {
      this.$rootScope.$broadcast('map.refreshLayer', 'Airspaces');
      this.resetOverride();
    });
    $scope.showAirspaceOnMap = (airspace: IAirspace) => this.$rootScope.$broadcast('map.flyToEntity', MapFlyToEntityType.SELECT_AIRSPACE, [ airspace ]);
    $scope.showAirspaceOnMapNavDb = (airspace: IAirspace) => this.$rootScope.$broadcast('map.flyToEntity', MapFlyToEntityType.SELECT_AIRSPACE_NAVDB, [ airspace ]);
    $scope.filterAirspaceType = (airspaceType: string) => this.$scope.listFromNavDB = this.filterAirspaceType(airspaceType);
    $scope.checkIfAirspaceAdded = (airspaceName: string) => this.$scope.airspaceAlreadyExists = this.checkIfAirspaceAdded(airspaceName);

    $scope.$on('$destroy', (): ng.IAngularEvent => $rootScope.$broadcast('map.toggle', 'Layers Off', []));

    this.loadAirspaces();
    $scope.refresh = () => {
      this.$scope.selectedItemId = null;
      this.refreshOverride();
    };

    $scope.reset = () => this.resetOverride();
  }

  // returns filtered airspaces on whether they are FIR, FIR_P, TMA or all airspaces
  private filterAirspaceType(airspaceType: string): IAirspace[] {

    if (airspaceType === null) { // no filter, return all
      return this.listFromNavDB;
    }

    let listOfAirspaces: IAirspace[] = [];

    for (let i in this.listFromNavDB) {
      if (this.listFromNavDB[i].airspace_type === airspaceType) {
        listOfAirspaces.push(this.listFromNavDB[i]);
      }
    }

    return listOfAirspaces;
  }

  // returns whether an airspace has already been added
  private checkIfAirspaceAdded(airspaceName: string): boolean {

    for (let i in this.$scope.list) {
      if (airspaceName === this.$scope.list[i].airspace_name) {
        return true;
      }
    }

    return false;
  }

  private loadAirspaces(): void {
    this.service.getAirspacesFromNavDB().then(
      (response: any) => {
        this.listFromNavDB = response.content;
        this.$scope.listFromNavDB = response.content;
      },
      () => this.$scope.navDBError = true); // error when trying to get airspaces from navDB
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
    this.$scope.openForm = false;
    this.$scope.airspaceType = null;
    this.$scope.navDBeditable = null;
  }
}
