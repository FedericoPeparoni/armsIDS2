// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { AircraftFlightsExemptionsService } from './service/aircraft-flights-exemptions.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interfaces
import { IAircraftFlightsExemptionsScope, IAircraftFlightsExemptions } from './aircraft-flights-exemptions.interface';

export class AircraftFlightsExemptionsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IAircraftFlightsExemptionsScope,
              private aircraftFlightsExemptionsService: AircraftFlightsExemptionsService,
              private systemConfigurationService: SystemConfigurationService, private customDate: CustomDate) {
    super($scope, aircraftFlightsExemptionsService);
    super.setup();

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);
    this.getFilterParameters();

    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.createOverride = (editable: IAircraftFlightsExemptions, startDate: string, endDate: string) => this.createOverride(editable, startDate, endDate);
    $scope.updateOverride = (editable: IAircraftFlightsExemptions, id: number, startDate: string, endDate: string) => this.updateOverride(editable, id, startDate, endDate);
    $scope.edit = (item: IAircraftFlightsExemptions) => this.edit(item);
  }

  protected edit(item: IAircraftFlightsExemptions): void {
    super.edit(item);

    if (item.exemption_start_date) {
      this.$scope.control.setUTCStartDate(new Date(item.exemption_start_date));
    }
    if (item.exemption_end_date) {
      this.$scope.control.setUTCEndDate(new Date(item.exemption_end_date));
    }
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private createOverride(editable: IAircraftFlightsExemptions, startDate: string, endDate: string): void {
    editable.exemption_start_date = startDate;
    editable.exemption_end_date = endDate;
    super.create(editable).then(() => {
      if (typeof this.$scope.control !== 'undefined') {
        this.$scope.control.reset();
      }
    });
  }

  private updateOverride(editable: IAircraftFlightsExemptions, id: number, startDate: string, endDate: string): void {
    editable.exemption_start_date = startDate;
    editable.exemption_end_date = endDate;
    super.update(editable, id).then(() => {
      if (typeof this.$scope.control !== 'undefined') {
        this.$scope.control.reset();
      }
    });
  }
}
