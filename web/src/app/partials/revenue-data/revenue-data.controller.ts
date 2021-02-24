// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { RevenueDataService } from './service/revenue-data.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';
import { AccountsService } from '../accounts/service/accounts.service';
import { BillingCentreManagementService } from '../billing-centre-management/service/billing-centre-management.service';
import { DbqueryService } from '../dbquery/service/dbquery.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CatalogueServiceChargeService } from '../catalogue-service-charge/service/catalogue-service-charge.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interfaces
import { IRevenueDataScope, IRevenueData, IRevenueResponse, IRevenueDataTemplate } from './revenue-data.interface';
import { IAerodromeSpring } from '../aerodromes/aerodromes.interface';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { IBillingCentreSpring } from '../billing-centre-management/billing-centre-management.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ICatalogueServiceChargeType, ICatalogueServiceChargeTypeSpring } from '../catalogue-service-charge/catalogue-service-charge.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

declare let Sortable: any;

export class RevenueDataController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IRevenueDataScope, private revenueDataService: RevenueDataService,
    private aerodromesService: AerodromesService, private accountsService: AccountsService,
    private billingCentreManagementService: BillingCentreManagementService,
    private catalogueServiceChargeService: CatalogueServiceChargeService,
    private systemConfigurationService: SystemConfigurationService, private $timeout: ng.ITimeoutService,
    private dbqueryService: DbqueryService, private customDate: CustomDate) {
    super($scope, revenueDataService);
    super.setup();

    // retrieve organization to show different reports
    const organization = systemConfigurationService.getValueByName(<any>SysConfigConstants.ORGANIZATION);
    $scope[organization] = organization;

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.$watchGroup(['editable.temporal_group', 'editable.group_by'], () => {
      this.createSortList();
    });

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
    aerodromesService.listAll().then((aerodromes: IAerodromeSpring) => $scope.aerodromesList = aerodromes.content);
    accountsService.findAllCreditMinimalReturn().then((accounts: Array<IAccountMinimal>) => $scope.accountsList = accounts);
    billingCentreManagementService.listAll().then((billingCentres: IBillingCentreSpring) => $scope.billingCentresList = billingCentres.content);
    catalogueServiceChargeService.listAll().then((charges: ICatalogueServiceChargeTypeSpring) => {
      this.$scope.serviceChargeList = charges.content; this.getClasses(this.$scope.serviceChargeList); });

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

    // functions
    this.getTemplateNames();
    $scope.saveTemplate = (name: string) => this.saveTemplate(name);
    $scope.deleteTemplate = (name: string) => this.deleteTemplate(name);
    $scope.updateTemplate = (name: string) => this.updateTemplate(name);
    $scope.selectTemplate = (name: string) => this.selectTemplate(name);
    $scope.getCategories = (chargeClass: string) => this.getCategories(chargeClass);
    $scope.getTypes = (chargeClass: string, chargeCategory: string) => this.getTypes(chargeClass, chargeCategory);

    $scope.generate = (revenueData: IRevenueData, startDate: Date, endDate: Date, groupBy: string, chartType: string) => this.generate(revenueData, startDate, endDate, groupBy, chartType)
      .then((data: IRevenueResponse[]) => {
        if (data.length === 0) {
          this.$scope.noData = true;
        } else {
          this.$scope.data = data; // re-used if chart type changes
          this.$scope.datapoints = this.getDataPoints(this.$scope.data);
          this.$scope.datacolumns = this.getColumnLabels(this.$scope.data, this.$scope.datapoints, revenueData.group_by, chartType);
          this.$scope.csvData = this.createCSVdata(this.$scope.datapoints);
          $scope.editable = revenueData;
        }
      })
      .catch((error: any) => {
        this.$scope.error = <IExtendableError>{};
        this.$scope.error.error = {data: error.data};
    });

    $scope.labelFormat = (value: any) => {
      if (!this.$scope.display_values) {
        return null;
      }
      if (!value) {
        return value;
      }
      if ($scope.display_value !== 'count') {
        return '$' + value.toLocaleString();
      } else {
        return value.toLocaleString();
      }
    };
  }

  private getDataPoints(data: IRevenueResponse[]): Object[] {
    return this.dbqueryService.getDataPoints(data, this.$scope.display_value, this.$scope.sortList);
  }

  private getColumnLabels(data: any[], dataPoints: Object[], labelKey: string, chartType: string): Object[] {
    return this.dbqueryService.getColumnsLabels(data, dataPoints, labelKey, chartType); // sets up column labels
  }

  /**
   * Turns field arrays into comma separated
   * strings that are used with the dbquery IN
   * clause
   *
   * @param  {IRevenueData} revenueData
   * @param  {string} startDate
   * @param  {string} endDate
   * @param  {string} groupBy
   * @returns IPromise<IRevenueResponse[]>
   */
  private generate(revenueData: IRevenueData, startDate: Date, endDate: Date, groupBy: string, chartType: string): ng.IPromise<IRevenueResponse[]> {
    this.$scope.noData = false;
    this.$scope.ylabel = this.$scope.displayValues.find((x: IStaticType) => x.value === this.$scope.display_value).name;
    let obj: IRevenueData = angular.copy(revenueData);

    obj.start_date = startDate.toISOString().substr(0, 10); // must send in format yyyy-mm-dd
    obj.end_date = endDate.toISOString().substr(0, 10);

    if (groupBy) { // the groupBy can only be by one thing, it could be more but the order is always the same, therefore forced to one thing
      obj[groupBy] = true;
    }

    return this.revenueDataService.query(obj);
  }

  /**
   * Creates an array of unique classes from the
   * service charge catalogue
   *
   * @param  {ICatalogueServiceChargeType[]} serviceChargeList
   * @returns void
   */
  private getClasses(serviceChargeList: ICatalogueServiceChargeType[]): void {
    let classes = serviceChargeList.map(((item: ICatalogueServiceChargeType) => item.charge_class));
    this.$scope.chargeClasses = Array.from(new Set(classes));
  }

  /**
   * Creates an array of unique categories from the
   * service charge catalogue for specific charge class
   *
   * @param  {string} chargeClass
   * @returns void
   */
  private getCategories (chargeClass: string): void {
    let categories = this.$scope.serviceChargeList.filter((item: ICatalogueServiceChargeType) => item.charge_class === chargeClass)
      .map((item: ICatalogueServiceChargeType) => item.category);
    this.$scope.chargeCategories = Array.from(new Set(categories));
  }

  /**
   * Creates an array of unique types from the
   * service charge catalogue for specific category and charge class
   *
   * @param  {string} chargeClass
   * @param  {string} chargeCategory
   * @returns void
   */
  private getTypes(chargeClass: string, chargeCategory: string): void {
    let types = this.$scope.serviceChargeList.filter((item: ICatalogueServiceChargeType) => item.charge_class === chargeClass)
      .filter((item: ICatalogueServiceChargeType) => item.category === chargeCategory)
      .map((item: ICatalogueServiceChargeType) => item.type);
    this.$scope.chargeTypes = Array.from(new Set(types));
  }

  private createSortList(): void {
    this.$scope.sortList = [];
    let idNumber = 0;
    if (this.$scope.editable.temporal_group) {
      this.$scope.sortList.push({ id: idNumber, name: 'Temporal Group', value: 'date' });
      idNumber++;
    }
    if (this.$scope.editable.group_by) {
      for (let item of this.$scope.editable.group_by.split(',')) {
        this.$scope.sortList.push({ id: idNumber, name: this.toUpperCase(item.replace(/_/g, ' ')), value: item });
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

  private getEditableSort(arr: Array<IStaticType>): string {
    let array = [];
    arr.forEach((item: IStaticType) => {
      array.push(item.name);
    });
    return array.join(',');
  }

  // sort by indexes
  private getSorted(arr: Array<Object>, sortArr: Array<any>): Array<IStaticType> {
    let result = [];
    for (let i = 0; i < arr.length; i++) {
      result[i] = arr[sortArr[i]];
    }
    return result;
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

  private getEmptyModels(): void {
    // multi-selects
    this.$scope.billingCentresModel = [];
    this.$scope.accountsModel = [];
    this.$scope.aerodromesModel = [];
    this.$scope.groupByModel = [];

    this.$scope.sortList = [];
    this.$scope.display_value = null;
    this.$scope.chartType = null;

    // editable
    this.$scope.editable = this.revenueDataService.getModel();
  }

  // template
  private collectDataForTemplate(name: string): IRevenueDataTemplate {
    let template: any = angular.copy(this.$scope.editable);
    template.name = name;
    template.value = this.$scope.display_value;
    template.chart_type = this.$scope.chartType;

    return template;
  }

  private saveTemplate(name: string): void {
    let template = this.collectDataForTemplate(name);
    this.revenueDataService.createTemplate(template).then(() => { this.getTemplateNames(); this.getEmptyModels(); })
      .catch((error: any) => {
        this.$scope.error = <IExtendableError>{};
        this.$scope.error.error = {data: error.data};
      });
  }

  private updateTemplate(name: string): void {
    let template = this.collectDataForTemplate(name);
    this.revenueDataService.getTemplateByName(name).then((data: IRevenueDataTemplate) => {
      template.id = data.id;
      this.revenueDataService.updateTemplate(template, template.id).then(() => this.selectTemplate(name))
        .catch((error: any) => {
          this.$scope.error = <IExtendableError>{};
          this.$scope.error.error = { data: error.data };
        });
    });
  }

  private deleteTemplate(name: string): void {
    this.revenueDataService.getTemplateByName(name).then((template: IRevenueDataTemplate) => {
      this.revenueDataService.deleteTemplate(template.id).then(() => this.getTemplateNames())
        .catch((error: any) => {
          this.$scope.error = <IExtendableError>{};
          this.$scope.error.error = { data: error.data };
        });
    });
  }

  // return template names
  private getTemplateNames(): void {
    this.revenueDataService.getAllNames().then((names: Array<string>) => this.$scope.templatesList = names);
  }

  // populate the form
  private selectTemplate(name: string): void {
    this.getEmptyModels();
    if (name) {
      this.revenueDataService.getTemplateByName(name).then((template: IRevenueDataTemplate) => {
        this.setModelFromTemplate(this.$scope.billingCentresList, template.billing_centres, this.$scope.billingCentresModel, 'name', 'billing_centres');
        this.setModelFromTemplate(this.$scope.accountsList, template.accounts, this.$scope.accountsModel, 'name', 'accounts');
        this.setModelFromTemplate(this.$scope.aerodromesList, template.aerodromes, this.$scope.aerodromesModel, 'aerodrome_name', 'aerodromes');
        this.setModelFromTemplate(this.$scope.groupByValues, template.group_by, this.$scope.groupByModel, 'value', 'group_by');

        this.$scope.display_value = template.value;
        this.$scope.chartType = template.chart_type;

        this.$scope.editable.temporal_group = template.temporal_group;
        this.$scope.editable.fiscal_year = template.fiscal_year;
        this.$scope.editable.payment_mode = template.payment_mode;
        this.$scope.editable.analysis_type = template.analysis_type;
        this.$scope.editable.charge_class = template.charge_class;
        this.getCategories(this.$scope.editable.charge_class);
        this.$scope.editable.charge_category = template.charge_category;
        this.getTypes(this.$scope.editable.charge_class, this.$scope.editable.charge_category);
        this.$scope.editable.charge_type = template.charge_type;

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
          if (typeof(template) === 'object' && template.includes(neededProp)) {
            model.push(element);
          } else if (template.split(',').includes(neededProp)) {
            model.push(element);
          }
          this.$scope.editable[item] = template;
        });
      });
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
