// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { RepositioningAerodromeClustersService } from './service/repositioning-aerodrome-clusters.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';
import { UnspecifiedDepDestLocationsService } from '../unspecified-dep-dest-locations/service/unspecified-dep-dest-locations.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// interfaces
import { IRepositioningAerodromeCluster, IRepositioningAerodromeClustersScope } from './repositioning-aerodrome-clusters.interface';
import { IAerodrome, IAerodromeSpring } from '../aerodromes/aerodromes.interface';
import { IUnspecifiedDepartureLocationTypeSpring, IUnspecifiedDepartureLocationType } from '../unspecified-dep-dest-locations/unspecified-dep-dest-locations.interface';

export class RepositioningAerodromeClustersController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IRepositioningAerodromeClustersScope, private unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService,
    private repositioningAerodromeClustersService: RepositioningAerodromeClustersService, private aerodromesService: AerodromesService,
    private systemConfigurationService: SystemConfigurationService) {
    super($scope, repositioningAerodromeClustersService);
    super.setup();

    this.getFilterParameters();

    aerodromesService.listAll().then((aerodromes: IAerodromeSpring) => $scope.aerodromesList = aerodromes.content);
    unspecifiedDepDestLocationsService.listAll().then((aerodromes: IUnspecifiedDepartureLocationTypeSpring) => $scope.unknownAerodromesList = aerodromes.content);
    $scope.createOverride = (clust: IRepositioningAerodromeCluster) => this.createOverride(clust);
    $scope.updateOverride = (clust: IRepositioningAerodromeCluster, id: number) => this.updateOverride(clust, id);
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.reset = () => this.reset();
    this.$scope.unknownAerodromesModel = [];
    this.$scope.aerodromesModel = [];
    $scope.populateDropdowns = () => this.populateDropdowns();
    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
  }

  protected createOverride(clust: IRepositioningAerodromeCluster): void {
    super.create(this.getAllAerodromeIdentifiers(clust));
  }

  protected updateOverride(clust: IRepositioningAerodromeCluster, id: number): void {
    super.update(this.getAllAerodromeIdentifiers(clust), id);
  }

  protected reset(): void {
    this.$scope.unknownAerodromesModel = [];
    this.$scope.aerodromesModel = [];
    this.$scope.enteredAerodromes = null;
    super.reset();
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

  private populateDropdowns(): void { // we do this because the back-end only returns an array of strings
    let knowns: Array<IAerodrome> = [];
    let unknowns: Array<IUnspecifiedDepartureLocationType> = [];
    let typed: Array<string> = [];

    for (let aerodrome of this.$scope.aerodromesList) {
      for (let editedAerodrome of this.$scope.editable.aerodrome_identifiers) {
        if (aerodrome.aerodrome_name === editedAerodrome) {
          typed.push(editedAerodrome);
          knowns.push(aerodrome);
        }
      }
    }

    for (let aerodrome of this.$scope.unknownAerodromesList) {
      for (let editedAerodrome of this.$scope.editable.aerodrome_identifiers) {
        if (aerodrome.text_identifier === editedAerodrome) {
          typed.push(editedAerodrome);
          unknowns.push(aerodrome);
        }
      }
    }

    this.$scope.enteredAerodromes = this.$scope.editable.aerodrome_identifiers.filter((x: string) => typed.indexOf(x) === -1); // aerodromes not in dropdowns will be added to the 'enter aerodrome' input field
    this.$scope.unknownAerodromesModel = unknowns;
    this.$scope.aerodromesModel = knowns;
  }

  private getAllAerodromeIdentifiers(clust: IRepositioningAerodromeCluster): IRepositioningAerodromeCluster {
    let aerodromesSet: Set<string> = new Set(''); // using a set to keep aerodromes unique

    if (typeof this.$scope.enteredAerodromes === 'string') {
      aerodromesSet = new Set(this.$scope.enteredAerodromes.split(/[ ,]+/));
    } else {
      aerodromesSet = new Set(this.$scope.enteredAerodromes);
    }

    this.$scope.aerodromesModel.forEach((v: IAerodrome) => aerodromesSet.add(v.aerodrome_name)); // add identifiers from aerodromes to set
    this.$scope.unknownAerodromesModel.forEach((v: IUnspecifiedDepartureLocationType) => aerodromesSet.add(v.text_identifier)); // add identifiers from unknown aerodromes to set

    let aerodromesArray = Array.from(aerodromesSet);

    clust.aerodrome_identifiers = aerodromesArray.filter(Boolean); // remove any manually typed then deleted aerodromes, otherwise it will send ""

    return clust;
  }

}
