// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { ITownsScope } from './utilities-towns.interface';
import { ITownsSpring } from './utilities-towns.interface';

// services
import { UtilitiesTownsService } from './service/utilities-towns.service';
import { UtilitiesSchedulesService } from '../utilities-schedules/service/utilities-schedules.service';

export class UtilitiesTownsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ITownsScope, private utilitiesTownsService: UtilitiesTownsService, private utilitiesSchedulesService: UtilitiesSchedulesService, $translate: angular.translate.ITranslateService) {
    super($scope, utilitiesTownsService);
    super.setup();

    this.getFilterParameters();

    $scope.waterSchedules = [];
    $scope.commElectricitySchedules = [];
    $scope.resElectricitySchedules = [];
    $scope.optionLabel = (schedule: any) => {
      if (schedule) {
        return `${$translate.instant('Schedule')}: ${schedule.schedule_id} / ${$translate.instant('Charge:')} ${schedule.minimum_charge}`;
      }
    };

    utilitiesSchedulesService.list()
      .then((data: ITownsSpring) => { $scope.schedules = data; this.separateWaterAndElectricSchedules(data); });

    $scope.refresh = () => this.refreshOverride();
  }

  private separateWaterAndElectricSchedules(schedules: ITownsSpring): void {

    for (let schedule of schedules.content) {
      if (schedule.schedule_type === 'WATER') {
        this.$scope.waterSchedules.push(schedule);
      } else if (schedule.schedule_type === 'ELECTRIC_COMM') {
        this.$scope.commElectricitySchedules.push(schedule);
      } else if (schedule.schedule_type === 'ELECTRIC_RES') {
        this.$scope.resElectricitySchedules.push(schedule);
      } else {
        console.error('Found `schedule_type` that does not exist', schedule);
      }
    }

  };

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      textFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

}
