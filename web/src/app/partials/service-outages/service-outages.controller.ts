// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ServiceOutagesService } from './service/service-outages.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { LocalStorageService } from './../../angular-ids-project/src/components/services/localStorage/localStorage.service';

// interfaces
import { IServiceOutages, IServiceOutagesScope } from './service-outages.interface';
import { IAerodrome, IAerodromeServiceType } from '../aerodromes/aerodromes.interface';

// contants
import { SysConfigConstants } from './../system-configuration/system-configuration.constants';

export class ServiceOutagesController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IServiceOutagesScope, private serviceOutagesService: ServiceOutagesService,
    private customDate: CustomDate, private aerodromesService: AerodromesService) {
      super($scope, serviceOutagesService);
      super.setup();

      $scope.customDate = this.customDate.returnDateFormatStr(false);

      $scope.$watchGroup(['serviceType', 'aerodromeStatus'], () => this.getFilterParameters());

      this.aerodromesService.getAllAerodromeServices().then((aerodromes: IAerodrome[]) => $scope.aerodromeWithServices = aerodromes);
      this.aerodromesService.getAerodromeServiceTypes().then((serviceTypes: IAerodromeServiceType[]) => $scope.aerodromeServiceTypes = serviceTypes);

      $scope.refresh = () => this.refreshOverride();
      $scope.reset = () => this.resetOverride();
      $scope.create = (item: IServiceOutages, startDate: string, startTime: string, endDate: string, endTime: string) =>
        this.createOverride(item, startDate, startTime, endDate, endTime);
      $scope.update = (item: IServiceOutages, id: number, startDate: string, startTime: string, endDate: string, endTime: string) =>
        this.updateOverride(item, id, startDate, startTime, endDate, endTime);
      $scope.delete = (id: number) => this.deleteOverride(id);
      $scope.editOutages = (item: IServiceOutages) => this.editOutages(item);
      $scope.showOutages = (item: any) => this.showOutages(item);
      $scope.getAerodromeServiceTypes = (aerodrome: string) => this.getAerodromeServiceTypes(aerodrome);
      $scope.setDefaultData = (aerodromeServiceType: string) => this.setDefaultData(aerodromeServiceType);
      $scope.clearTime = (field: string, date: string) => this.clearTime(field, date);
  }

  private showOutages(item: any): void {
    this.resetOverride();
    this.$scope.editable = this.serviceOutagesService.getModel();
    this.$scope.outages = item.aerodrome_service_outages;
  }

  private editOutages(item: IServiceOutages): void {
    super.edit(item);

    const startDay = moment(item.start_date_time).utc();
    const endDay = moment(item.end_date_time).utc();

    this.$scope.start_date = item.start_date_time;
    this.$scope.end_date = item.end_date_time;
    this.$scope.start_time = startDay.format('HH').toString().concat(startDay.format('mm').toString());
    this.$scope.end_time = endDay.format('HH').toString().concat(endDay.format('mm').toString());

    this.$scope.editable.aerodrome = this.$scope.aerodromeWithServices.find((aerodrome: IAerodrome) =>
      aerodrome.aerodrome_name === item.aerodrome).aerodrome_name;

    this.$scope.editable.aerodrome_service_type = this.$scope.aerodromeServiceTypes.find((serviveType: IAerodromeServiceType) =>
      item.aerodrome_service_type === serviveType.service_name).service_name;
  }

  private getFilterParameters(): void {
    this.$scope.startDateTimeFilter = null;
    this.$scope.endDateTimeFilter = null;

    if (this.$scope.startDateFilter) {
      this.$scope.startDateTimeFilter = moment(this.$scope.startDateFilter).utc().startOf('day').toISOString();
    }

    if (this.$scope.endDateFilter) {
      this.$scope.endDateTimeFilter = moment(this.$scope.endDateFilter).utc().endOf('day').toISOString();
    }

    if (this.$scope.startTimeFilter) {
      this.$scope.startDateTimeFilter = this.setDateTime(this.$scope.startDateFilter, this.$scope.startTimeFilter);
    }

    if (this.$scope.endTimeFilter) {
      this.$scope.endDateTimeFilter = this.setDateTime(this.$scope.endDateFilter, this.$scope.endTimeFilter);
    }

    this.$scope.filterParameters = {
      'size': this.$scope.pagination && this.$scope.pagination.size || LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.ROW_FOR_PAGE}`) || 20,
      'search': this.$scope.search,
      'page': this.$scope.pagination ? this.$scope.pagination.number : 0,
      'serviceType': this.$scope.serviceType,
      'aerodromeStatus': this.$scope.aerodromeStatus,
      'aerodromeName': this.$scope.aerodromeName,
      'startDateTime': this.$scope.startDateTimeFilter,
      'endDateTime': this.$scope.endDateTimeFilter
    };
  }

  private refreshOverride(): void {
    this.$scope.outages = null;
    this.getFilterParameters();

    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private resetOverride(): void {
    super.reset();
    this.$scope.start_date = null;
    this.$scope.end_date = null;
    this.$scope.start_time = null;
    this.$scope.end_time = null;
  }

  private createOverride(item: IServiceOutages, startDate: string, startTime: string, endDate: string, endTime: string): void {
    this.$scope.editable.start_date_time = this.setDateTime(startDate, startTime);
    this.$scope.editable.end_date_time = this.setDateTime(endDate, endTime);
    super.create(item).then(() => { this.resetOverride(); this.refreshOverride(); });
  }

  private updateOverride(item: IServiceOutages, id: number, startDate: string, startTime: string, endDate: string, endTime: string): void {
    this.$scope.editable.start_date_time = this.setDateTime(startDate, startTime);
    this.$scope.editable.end_date_time = this.setDateTime(endDate, endTime);
    super.update(item, id).then(() => { this.resetOverride(); this.refreshOverride(); });
  }

  private deleteOverride(id: number): void {
    super.delete(id).then(() => { this.resetOverride(); this.refreshOverride(); });
  }

  private setDateTime(date: string, time: string): string {
    const hoursMinutes = { hours: parseInt(time.substring(0, 2), 10),
                          minutes: parseInt(time.substring(2), 10) };

    const { hours, minutes} = hoursMinutes;
    return moment(date).utc().startOf('day').hours(hours).minutes(minutes).toISOString();
  }

  private getAerodromeServiceTypes(selectedAerodrome: string): void {
    this.$scope.aerodromeServiceTypes = this.$scope.aerodromeWithServices.find((aerodrome: IAerodrome) =>
      aerodrome.aerodrome_name === selectedAerodrome).aerodrome_services;

    this.$scope.editable.aerodrome_service_type = this.$scope.aerodromeServiceTypes[0].service_name;
    this.setDefaultData(this.$scope.editable.aerodrome_service_type);
  }

  private setDefaultData(aerodromeServiceType: string): void {
    const selectedServiceType = this.$scope.aerodromeServiceTypes.find((serviceType: IAerodromeServiceType) =>
      serviceType.service_name === aerodromeServiceType);

    const { service_outage_approach_discount_type,
            service_outage_approach_amount,
            service_outage_aerodrome_discount_type,
            service_outage_aerodrome_amount,
            default_flight_notes} = selectedServiceType;

    this.$scope.editable.approach_discount_type = service_outage_approach_discount_type;
    this.$scope.editable.approach_discount_amount = service_outage_approach_amount;
    this.$scope.editable.aerodrome_discount_type = service_outage_aerodrome_discount_type;
    this.$scope.editable.aerodrome_discount_amount = service_outage_aerodrome_amount;
    this.$scope.editable.flight_notes = default_flight_notes;
  }

  private clearTime(field: string, date: string): void {
    if (!date) {
      if (field === 'start') {
        this.$scope.startTimeFilter = null;
      }
      if (field === 'end') {
        this.$scope.endTimeFilter = null;
      }
    }
  }
}
