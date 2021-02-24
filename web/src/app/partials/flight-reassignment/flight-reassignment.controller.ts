// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { FlightReassignmentService } from './service/flight-reassignment.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';

// interfaces
import { IFlightReassignment, IFlightReassignmentScope } from './flight-reassignment.interface';
import { IAerodrome, IAerodromeSpring } from '../aerodromes/aerodromes.interface';

export class FlightReassignmentScopeController extends CRUDFormControllerUserService {

  private nameMap: Object = {
    applies_to_type_arrival: 'ARR',
    applies_to_type_departure: 'DEP',
    applies_to_type_domestic: 'DOM',
    applies_to_type_overflight: 'OVF',
    applies_to_scope_domestic: 'DOM',
    applies_to_scope_regional: 'REG',
    applies_to_scope_international: 'INT',
    applies_to_nationality_national: 'NAT',
    applies_to_nationality_foreign: 'INT'
  };

  /* @ngInject */
  constructor(protected $scope: IFlightReassignmentScope, private flightReassignmentService: FlightReassignmentService, private aerodromesService: AerodromesService) {
    super($scope, flightReassignmentService);
    super.setup();

    aerodromesService.listAll().then((aerodromes: IAerodromeSpring) => $scope.aerodromesList = aerodromes.content);

    this.getFilterParameters();
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.getApplication = (item: IFlightReassignment, type: string) => this.getApplication(item, type);
    $scope.setMaxLength = (idType: string) => this.setMaxLength(idType);
    $scope.getAerodromeIdentifiers = (aerodromeIdentifiers: Array<string>) => this.getAerodromeIdentifiers(aerodromeIdentifiers);

    this.$scope.aerodromesModel = [];
    $scope.addAerodromeToList = () => $scope.editable.aerodrome_identifiers = $scope.aerodromesModel.map((aerodrome: IAerodrome) => aerodrome.aerodrome_name);

    $scope.updateAerodromeMultiselect = () => {
      $scope.aerodromesModel = $scope.aerodromesList.filter(
        (aerodrome: IAerodrome) => $scope.editable.aerodrome_identifiers.indexOf(aerodrome.aerodrome_name) > -1
      );
    };
  }

  private setMaxLength(idType: string): void {
    if (idType === 'ICAO code') {
      this.$scope.maxLength = 3;
    } else if (idType === 'Flight Id') {
      this.$scope.maxLength = 7;
    } else {
      this.$scope.maxLength = 20;
    };
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      filter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      account: this.$scope.accountFilter
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getApplication(item: IFlightReassignment, type: string): string {
    let output = '';

    for (let p in item) {
      if (item.hasOwnProperty(p)) {
        if (p.indexOf(type) !== -1 && item[p]) {
          output += this.nameMap[p] + ', ';
        };
      }
    };
    return output.replace(/(^\s*,)|(,\s*$)/g, '');
  }

  private getAerodromeIdentifiers(aerodromeIdentifiers: Array<string>): string {
    let arr = aerodromeIdentifiers.slice(0, 14);
    return arr.join(', ');
  }
}
