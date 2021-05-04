// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { BillingCentreManagementService } from '../billing-centre-management/service/billing-centre-management.service';
import { AccountsService } from '../accounts/service/accounts.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';
import { AircraftTypeManagementService } from '../aircraft-type-management/service/aircraft-type-management.service';
import { MtowService } from '../mtow/service/mtow.service';
import { FlightMovementManagementService } from '../flight-movement-management/service/flight-movement-management.service';
import { AirTrafficDataService } from './service/air-traffic-data.service';
import { DbqueryService } from '../dbquery/service/dbquery.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interfaces
import { IBillingCentreSpring } from '../billing-centre-management/billing-centre-management.interface';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { IAerodromeSpring } from '../aerodromes/aerodromes.interface';
import { IAircraftTypeSpring } from '../aircraft-type-management/aircraft-type-management.interface';
import { IMtowTypeSpring, IMtowType } from '../mtow/mtow.interface';
import { IAirTrafficDataScope, IAirTrafficData, IAirTrafficResponse, IAirTrafficDataTemplate } from './air-traffic-data.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

declare let Sortable: any;

export class AirTrafficDataController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IAirTrafficDataScope, private airTrafficDataService: AirTrafficDataService,
    private aerodromesService: AerodromesService, private aircraftTypeManagementService: AircraftTypeManagementService,
    private billingCentreManagementService: BillingCentreManagementService, private accountsService: AccountsService,
    private mtowService: MtowService, private flightMovementManagementService: FlightMovementManagementService,
    private systemConfigurationService: SystemConfigurationService, private $filter: ng.IFilterService,
    private dbqueryService: DbqueryService, private $timeout: ng.ITimeoutService, private customDate: CustomDate) {
    super($scope, airTrafficDataService);
    super.setup();

    this.$scope.customDate = this.customDate.returnDateFormatStr(true);

    // retrieve organization to show different reports
    const organization = systemConfigurationService.getValueByName(<any>SysConfigConstants.ORGANIZATION);
    $scope[organization] = organization;

    // configuration for MTOW Units (tons/kg)
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);

    $scope.$watchGroup(['editable.temporal_group', 'editable.group_by'], () => {
      this.createSortList();
    });

    $scope.cashAccounts = <IAccountMinimal>{};
    $scope.cashAccounts.name = 'CASH ACCOUNTS';

    $timeout(() => {
      // add sortable to <ul>
      $scope.newSortIndexes = [];
      const mySort: HTMLElement = <HTMLElement>document.getElementById('my-sort');
      const sortable = () => {
        return new Sortable(mySort, {
          onUpdate: (evt: ng.IAngularEvent): void => {
            // get new sort order based on indexes
            let newSortIndexes = [];
            let liElements = Array.from(mySort.getElementsByTagName('li'));
            for (let element of liElements) {
              newSortIndexes.push(element.getAttribute('data-index'));
            }
            $scope.newSortIndexes = newSortIndexes;
            $scope.sortList = this.getSorted($scope.sortList, newSortIndexes);
            $scope.editable.sort = this.getEditableSort($scope.sortList);
          }
        });
      };

      sortable();
    });

    // services
    billingCentreManagementService.listAll().then((billingCentres: IBillingCentreSpring) => $scope.billingCentresList = billingCentres.content);
    accountsService.findAllCreditMinimalReturn().then((account: Array<IAccountMinimal>) => { $scope.accountsList = account; $scope.accountsList.push($scope.cashAccounts); });
    aerodromesService.listAll().then((aerodromes: IAerodromeSpring) => $scope.aerodromesList = aerodromes.content);
    aircraftTypeManagementService.listAll().then((aircraftType: IAircraftTypeSpring) => $scope.aircraftTypeList = aircraftType.content);
    mtowService.listAll().then((mtow: IMtowTypeSpring) => {
      $scope.mtowListAll = mtow.content;

      if (!$scope.KCAA) {
        $scope.mtow_factor_class = 'DOMESTIC';
        this.showMTOWList($scope.mtow_factor_class);
      }
    });

    $scope.showMTOWList = (mtowFactorClass: string) => this.showMTOWList(mtowFactorClass);

    flightMovementManagementService.getDistinctRoutes().then((routes: Array<string>) => {
      $scope.routeList = [];
      this.setListForMultiselect($scope.routeList, routes, true);
    });

    flightMovementManagementService.getDistinctFlightLevels().then((flightlevels: Array<string>) => {
      $scope.flightLevelList = [];
      this.setListForMultiselect($scope.flightLevelList, flightlevels, false);
    });

    $scope.displayValues = this.service.displayValues();
    $scope.groupByValues = this.service.groupByValues().sort((a: IStaticType, b: IStaticType) => {
      let nameA = a.name.toUpperCase(); // ignore upper and lowercase
      let nameB = b.name.toUpperCase(); // ignore upper and lowercase
      if (nameA < nameB) {
        return -1;
      }
      if (nameA > nameB) {
        return 1;
      }
      return 0; // names must be equal
    });

    if (!$scope.KCAA) {
      $scope.groupByValues = $scope.groupByValues.filter((values: IStaticType) => values.name !== 'Flight Level');
    }

    // default chart variables
    $scope.datapoints = [];
    $scope.datacolumns = [];
    $scope.ylabel = [];
    $scope.datax = { 'id': 'x' };

    $scope.getEmptyModels = () => this.getEmptyModels();
    this.getEmptyModels();

    $scope.addGroupToList = () => {
      this.$scope.editable.group_by = this.addToList(this.$scope.groupByModel, 'value');
    };

    $scope.addBillingCentreToList = () => {
      this.$scope.editable.billing_centres = this.addToList(this.$scope.billingCentresModel, 'name');
    };

    $scope.addAccountToList = () => {
      this.$scope.editable.accounts = this.addToList(this.$scope.accountsModel, 'name');
    };

    $scope.addAerodromeToList = () => {
      this.$scope.editable.aerodromes = this.addToList(this.$scope.aerodromesModel, 'aerodrome_name');
    };

    $scope.addAircraftToList = () => {
      this.$scope.editable.aircraft_types = this.addToList(this.$scope.aircraftTypeModel, 'aircraft_type');
    };

    $scope.addMTOWToList = () => {
      let mtowID = this.addToList(this.$scope.mtowModel, 'id');
      let selectedMtow = [];
      this.$scope.editable.mtow_categories = null;
      $scope.mtowListAll.forEach((element: IMtowType) => {
        if (mtowID !== null && mtowID.split(',').indexOf(element.id.toString()) > -1) {
          selectedMtow.push(element.upper_limit);
        }
      });
      if (selectedMtow.length > 0) {
        this.$scope.editable.mtow_categories = selectedMtow.join(',');
      }
    };

    $scope.addRouteToList = () => {
      this.$scope.editable.routes = this.addToList(this.$scope.routeModel, 'name');
    };

    $scope.addFlightLevelToList = () => {
      this.$scope.editable.flight_levels = this.addToList(this.$scope.flightLevelModel, 'name');
    };

    // functions
    this.getTemplateNames();
    $scope.saveTemplate = (name: string) => this.saveTemplate(name);
    $scope.deleteTemplate = (name: string) => this.deleteTemplate(name);
    $scope.updateTemplate = (name: string) => this.updateTemplate(name);
    $scope.selectTemplate = (name: string) => this.selectTemplate(name);

    $scope.generate = (airTrafficData: IAirTrafficData, startDate: Date, endDate: Date, groupBy: string, chartType: string) =>
      this.generate(airTrafficData, startDate, endDate, groupBy, chartType)
        .then((data: IAirTrafficResponse[]) => {
          if (data.length === 0) {
            this.$scope.noData = true;
          } else {


            this.$scope.data = data; // reused if chart type changes
            this.$scope.datapoints = this.getDataPoints(this.$scope.data);
            this.$scope.datacolumns = this.getColumnLabels(this.$scope.data, this.$scope.datapoints, airTrafficData.group_by, chartType);

            let contentDivWidth = document.getElementById("content").offsetWidth;
            //controllo il numero di elementi per impostare l'altezza del grafico
            if (this.$scope.datacolumns.length <= 40) {
              this.$scope.chartHeight = contentDivWidth * 0.3;
            } else {
              this.$scope.chartHeight = contentDivWidth * 0.5;
            }

            this.$scope.csvData = this.createCSVdata(this.$scope.datapoints);
            $scope.editable = airTrafficData;
          }
        })
        .catch((error: any) => {
          this.$scope.error = <IExtendableError>{};
          this.$scope.error.error = { data: error.data };
        });

    $scope.labelFormat = (value: any) => {
      if (!this.$scope.display_values) {
        return null;
      }
      if (!value) {
        return value;
      }
      if ($scope.display_value === 'sum_total_charges' || $scope.display_value === 'revenue_category') {
        return '$' + value.toLocaleString();
      } else {
        return value.toLocaleString();
      }
    };
  
    $scope.valueFormat = (value : any) =>{
      return value.toFixed(2);
    }
  }

  private setListForMultiselect(list: Array<object>, array: Array<string>, pairElement: boolean): void {
    let i = 0;
    array.forEach((element: string) => {
      if (pairElement) {
        let item: string = `${element[0]}-${element[1]}`;
        list.push({ id: i, name: item });
      } else {
        list.push({ id: i, name: element });
      }
      i++;
    });
  }

  private showMTOWList(mtowFactorClass: string): void {
    this.$scope.editable.mtow_categories = null;
    if (mtowFactorClass !== '') {
      this.$scope.mtowList = [];
      this.$scope.mtowModel = [];
      let list = angular.copy(this.$scope.mtowListAll);
      list.forEach((element: IMtowType) => {
        if (element.factor_class === mtowFactorClass) {
          if (this.$scope.mtowUnitOfMeasure === 'kg') {
            element.upper_limit = this.$filter('number')(element.upper_limit * 907.185, 0);  // us ton
          } else {
            element.upper_limit = this.$filter('number')(element.upper_limit, 2);
          }
          this.$scope.mtowList.push(element);
        }
      });
    } else {
      this.$scope.mtow_factor_class = null;
    }
  }

  private getEmptyModels(): void {
    // multi-selects
    this.$scope.billingCentresModel = [];
    this.$scope.accountsModel = [];
    this.$scope.aerodromesModel = [];
    this.$scope.aircraftTypeModel = [];
    this.$scope.mtowModel = [];
    this.$scope.routeModel = [];
    this.$scope.flightLevelModel = [];
    this.$scope.groupByModel = [];

    this.$scope.sortList = [];
    this.$scope.display_value = null;
    this.$scope.revenue_category = null;
    this.$scope.chartType = null;
    this.$scope.mtow_factor_class = null;

    // checkboxes
    this.$scope.flight_type = null;
    this.$scope.flight_scope = null;
    this.$scope.flight_category = null;
    this.$scope.flight_rules = null;

    // editable
    this.$scope.editable = this.airTrafficDataService.getModel();
  }

  private addToList(model: Array<any>, prop: string): string {
    let items = null;
    let neededProp = null;
    if (model.length > 0) {
      items = model.map((item: any) => {
        Object.getOwnPropertyNames(item).forEach((val: string, idx: number, array: Array<any>) => {
          if (val === prop) {
            neededProp = item[val];
          }
        });
        return neededProp;
      }).join(',');
    }
    return items;
  }

  private createSortList(): void {
    this.$scope.sortList = [];
    let idNumber = 0;
    let value;
    if (this.$scope.editable.temporal_group) {
      this.$scope.sortList.push({ id: idNumber, name: 'Temporal Group', value: 'date' });
      idNumber++;
    } if (this.$scope.editable.group_by) {
      for (let item of this.$scope.editable.group_by.split(',')) {
        value = item;
        if (item.includes('mtow')) {
          item = 'MTOW Category';
        }
        if (item.includes('aerodromes')) {
          item = 'Aerodromes';
        }
        this.$scope.sortList.push({ id: idNumber, name: this.toUpperCase(item.replace(/_/g, ' ')).trim(), value: value });
        idNumber++;
      }
    }
    this.$scope.editable.sort = this.getEditableSort(this.$scope.sortList);
  }

  private toUpperCase(str: string): string {
    let splitStr = str.split(' ');
    for (let i = 0; i < splitStr.length; i++) {
      splitStr[i] = splitStr[i].charAt(0).toUpperCase() + splitStr[i].substring(1);
    }
    return splitStr.join(' ');
  }

  private getDataPoints(data: IAirTrafficResponse[]): Object[] {
    let yaxis = this.$scope.display_value === 'revenue_category' ? this.$scope.revenue_category : this.$scope.display_value; // value to compare data against
    return this.dbqueryService.getDataPoints(data, yaxis, this.$scope.sortList);
  }

  private getColumnLabels(data: any[], dataPoints: Object[], labelKey: string, chartType: string): Object[] {
    return this.dbqueryService.getColumnsLabels(data, dataPoints, labelKey, chartType); // sets up column labels
  }

  /**
   * Turns field arrays into comma separated
   * strings that are used with the dbquery IN
   * clause
   *
   * @param  {IAirTrafficData} airTrafficData
   * @param  {string} startDate
   * @param  {string} endDate
   * @param  {string} groupBy
   * @returns IPromise<IAirTrafficResponse[]>
   */
  private generate(airTrafficData: IAirTrafficData, startDate: Date, endDate: Date, groupBy: string, chartType: string): ng.IPromise<IAirTrafficResponse[]> {
    this.$scope.noData = false;
    this.$scope.ylabel = this.$scope.displayValues.find((x: IStaticType) => x.value === this.$scope.display_value).name;
    let obj: IAirTrafficData = angular.copy(airTrafficData);

    if (startDate && endDate) {
      obj.start_date = startDate.toISOString().substr(0, 10); // must send in format yyyy-mm-dd
      obj.end_date = endDate.toISOString().substr(0, 10);
    }

    this.filterArray(obj);

    if (groupBy !== null) { // the groupBy can only be by one thing, it could be more but the order is always the same, therefore forced to one thing
      obj[groupBy] = true;
    }

    return this.airTrafficDataService.query(obj);
  }

  // filter out all bad values
  private filterArray(obj: IAirTrafficData): void {

    // filters out values that are null or undefined
    function hasValue(val: string): boolean {
      return val && val !== null;
    }

    for (let field of ['flight_rules', 'flight_types', 'flight_scopes', 'flight_categories']) {
      let arr = this.$scope.editable[field].filter(hasValue);
      obj[field] = arr.length > 0 ? arr.toString() : null; // creates a comma delimited string || null
    }
  }

  // sort by indexes
  private getSorted(arr: Array<Object>, sortArr: Array<any>): Array<IStaticType> {
    let result = [];
    for (let i = 0; i < arr.length; i++) {
      result[i] = arr[sortArr[i]];
    }
    return result;
  }

  private getEditableSort(arr: Array<IStaticType>): string {
    let array = [];
    arr.forEach((item: IStaticType) => {
      array.push(item.name);
    });
    return array.join(',');
  }

  // template
  private collectDataForTemplate(name: string): IAirTrafficDataTemplate {
    let template: any = angular.copy(this.$scope.editable);
    this.filterArray(template);
    template.name = name;
    template.value = this.$scope.display_value;
    template.revenue_category = this.$scope.revenue_category;
    template.chart_type = this.$scope.chartType;
    template.mtow_factor_class = this.$scope.mtow_factor_class;

    return template;
  }

  private saveTemplate(name: string): void {
    let template = this.collectDataForTemplate(name);
    this.airTrafficDataService.createTemplate(template).then(() => { this.getTemplateNames(); this.getEmptyModels(); })
      .catch((error: any) => {
        this.$scope.error = <IExtendableError>{};
        this.$scope.error.error = { data: error.data };
      });
  }

  private updateTemplate(name: string): void {
    let template = this.collectDataForTemplate(name);
    this.airTrafficDataService.getTemplateByName(name).then((data: IAirTrafficDataTemplate) => {
      template.id = data.id;
      this.airTrafficDataService.updateTemplate(template, template.id).then(() => this.selectTemplate(name))
        .catch((error: any) => {
          this.$scope.error = <IExtendableError>{};
          this.$scope.error.error = { data: error.data };
        });
    });
  }

  private deleteTemplate(name: string): void {
    this.airTrafficDataService.getTemplateByName(name).then((template: IAirTrafficDataTemplate) => {
      this.airTrafficDataService.deleteTemplate(template.id).then(() => this.getTemplateNames())
        .catch((error: any) => {
          this.$scope.error = <IExtendableError>{};
          this.$scope.error.error = { data: error.data };
        });
    });
  }

  // return template names
  private getTemplateNames(): void {
    this.airTrafficDataService.getAllNames().then((names: Array<string>) => this.$scope.templatesList = names);
  }

  // populate the form
  private selectTemplate(name: string): void {
    this.getEmptyModels();
    if (name) {
      this.airTrafficDataService.getTemplateByName(name).then((template: IAirTrafficDataTemplate) => {
        this.setModelFromTemplate(this.$scope.billingCentresList, template.billing_centres, this.$scope.billingCentresModel, 'name', 'billing_centres');
        this.setModelFromTemplate(this.$scope.accountsList, template.accounts, this.$scope.accountsModel, 'name', 'accounts');
        this.setModelFromTemplate(this.$scope.aerodromesList, template.aerodromes, this.$scope.aerodromesModel, 'aerodrome_name', 'aerodromes');
        this.setModelFromTemplate(this.$scope.aircraftTypeList, template.aircraft_types, this.$scope.aircraftTypeModel, 'aircraft_type', 'aircraft_types');
        this.setModelFromTemplate(this.$scope.routeList, template.routes, this.$scope.routeModel, 'name', 'routes');
        this.setModelFromTemplate(this.$scope.flightLevelList, template.flight_levels, this.$scope.flightLevelModel, 'name', 'flight_levels');
        this.setModelFromTemplate(this.$scope.groupByValues, template.group_by, this.$scope.groupByModel, 'value', 'group_by');

        this.$scope.mtow_factor_class = template.mtow_factor_class;
        this.$scope.showMTOWList(this.$scope.mtow_factor_class);
        let mtowID = [];
        this.$scope.mtowListAll.forEach((element: IMtowType) => {
          if (element.factor_class === this.$scope.mtow_factor_class) {
            if (template.mtow_categories !== null && template.mtow_categories.includes(element.upper_limit.toString())) {
              mtowID.push(element.id);
            }
          }
        });
        this.setModelFromTemplate(this.$scope.mtowList, mtowID, this.$scope.mtowModel, 'id', null);
        this.$scope.editable.mtow_categories = template.mtow_categories;

        this.$scope.display_value = template.value;
        this.$scope.revenue_category = template.revenue_category;
        this.$scope.chartType = template.chart_type;
        this.$scope.editable.temporal_group = template.temporal_group;
        this.$scope.editable.fiscal_year = template.fiscal_year;

        this.$timeout(() => {
          this.$scope.sortList = [];
          this.$scope.editable.sort = null;
          if (template.sort) {
            let sort = template.sort.split(',');
            let idNumber = 0;
            sort.forEach((element: string) => {
              idNumber++;
              let value;
              if (element === 'Temporal Group') {
                value = 'date';
              } else {
                const existingGroupByValue = this.$scope.groupByValues.find((item: IStaticType) => item.value === element.replace(/ /g, '_').toLowerCase());
                value = existingGroupByValue ? existingGroupByValue.value : null;
              }
              this.$scope.sortList.push({ id: idNumber, name: element, value: value });
            });
            this.$scope.editable.sort = this.getEditableSort(this.$scope.sortList);
          }
        });

        this.setCheckBoxFromTemplate('flight_types', template.flight_types, 'flight_type', 'DEPARTURE', 'ARRIVAL', 'OVERFLIGHT');
        this.setCheckBoxFromTemplate('flight_scopes', template.flight_scopes, 'flight_scope', 'DOMESTIC', 'REGIONAL', 'INTERNATIONAL');
        this.setCheckBoxFromTemplate('flight_categories', template.flight_categories, 'flight_category', 'SCH', 'NONSCH', null);
        this.setCheckBoxFromTemplate('flight_rules', template.flight_rules, 'flight_rules', 'I', 'V', null);
      });
    }
  }

  private setModelFromTemplate(list: Array<Object>, template: any, model: Array<Object>, prop: string, item: string): void {
    if (template) {
      list.forEach((element: any) => {
        let neededProp = null;
        Object.getOwnPropertyNames(element).forEach((val: string, idx: number, array: Array<any>) => {
          if (!(val === prop)) {
            return;
          }
          neededProp = element[val];
          if (typeof (template) === 'object') {
            if (template.includes(neededProp)) {
              model.push(element);
            }
          } else {
            if (template.split(',').includes(neededProp)) {
              model.push(element);
            }
            this.$scope.editable[item] = template;
          }
        });
      });
    }
  }

  private setCheckBoxFromTemplate(editable: string, template: any, checkBox: string, index0: string, index1: string, index2: string): void {
    if (template) {
      this.$scope[checkBox] = true;
      if (template.includes(index0) && template.indexOf(index0, 0) === 0) {
        this.$scope.editable[editable][0] = index0;
      } else {
        this.$scope.editable[editable][0] = null;
      }
      if (template.includes(index1)) {
        this.$scope.editable[editable][1] = index1;
      } else {
        this.$scope.editable[editable][1] = null;
      }
      if (index2) {
        if (template.includes(index2)) {
          this.$scope.editable[editable][2] = index2;
        } else {
          this.$scope.editable[editable][2] = null;
        }
      }
    }
  }

  private createCSVdata(data: Array<Object>): Array<Object> {
    const headers = data.map((entry: any) =>
      Object.keys(entry)).reduce((accumulator: Object, currentValue: any) => {
        if (!Object.keys(accumulator).length) {
          let key = 'x';
          accumulator[key] = 'Date';
        }
        currentValue.map((value: string) => {
          if (!accumulator[value] && value !== 'x') {
            accumulator[value] = value;
          }
        });
        return accumulator;
      }, {});

    const csvData = [headers];
    data.forEach((item: string) => {
      let row = {};
      Object.keys(headers).forEach((key: string) => row[key] = item[key] || 0);
      csvData.push(row);
    });

    return csvData;
  }
}
