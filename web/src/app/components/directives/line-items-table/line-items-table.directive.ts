// interface
import { IInvoiceLineItem } from '../../../partials/line-item/line-item.interface';
import { ISchedule, IScheduleSpring, IRange } from '../../../partials/utilities-schedules/utilities-schedules.interface';
import { ITown } from '../../../partials/utilities-towns/utilities-towns.interface';
import { IAerodrome, IAerodromeSpring } from '../../../partials/aerodromes/aerodromes.interface';
import { IRestangularResponse } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IAccountExternalChargeCategory } from '../../../partials/account-external-charge-category/account-external-charge-category.interface';

// services
import { UtilitiesSchedulesService } from '../../../partials/utilities-schedules/service/utilities-schedules.service';
import { UsersService } from '../../../partials/users/service/users.service';
import { ReportsService } from '../../../partials/reports/service/reports.service';
import { AerodromesService } from '../../../partials/aerodromes/service/aerodromes.service';
import { UtilitiesTownsService } from '../../../partials/utilities-towns/service/utilities-towns.service';
import { AccountExternalChargeCategoryService } from '../../../partials/account-external-charge-category/service/account-external-charge-category.service';

/** @ngInject */
export function lineItemsTable(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/line-items-table/line-items-table.directive.html',
    scope: {
      lineItems: '=',
      isValid: '=',
      params: '=',
      isNonAvi: '=?' // non-aviation billing specific
    },
    controller: LineItemsTableController
  };
}

interface IParams {
  month: number;
  year: number;
  accountId: number;
  currencyCode: string;
}

interface ILineItemsTableScope extends ng.IScope {
  lineItems: Array<IInvoiceLineItem>;
  isWater: boolean;
  isElectric: boolean;
  isFixed: boolean;
  isValid: boolean;
  schedules: Array<ISchedule>;
  getUtility: Function;
  removeChargeItem: Function;
  lineItemError: string[];
  checkAll: Function;
  itemCheck: Function;
  validateInvoiceLineItem: Function;
  townsList: ITown[];
  aerodromeList: IAerodrome[];
  aerodromeListAll: IAerodrome[];
  params: IParams;
  selectAll: boolean;
  isAccountExternalSystemIdentifier: boolean;
  lineItemAccountExternalChargeCategories: Array<Array<IAccountExternalChargeCategory>>;
  externalSystemIdentifiers: (item: IInvoiceLineItem) => void;
}

/** @ngInject */
export class LineItemsTableController {

  private accountExternalChargeCategories: Array<IAccountExternalChargeCategory>;

  constructor(private $scope: ILineItemsTableScope, private utilitiesTownsService: UtilitiesTownsService, private aerodromesService: AerodromesService,
              private reportsService: ReportsService, private utilitiesSchedulesService: UtilitiesSchedulesService, private usersService: UsersService,
              private accountExternalChargeCategoryService: AccountExternalChargeCategoryService) {

    $scope.lineItemAccountExternalChargeCategories = [];

    $scope.$watch('lineItems', (newlineItems: Array<IInvoiceLineItem>) => {
      $scope.lineItemError = []; // reset errors
      this.dynamicTable(newlineItems);
      this.$scope.isValid = this.areAllLineItemsValid();
    }, true);

    $scope.$watch('params.currencyCode', (currencyCode: string) => {
      if (this.$scope.lineItems) {
        this.$scope.lineItems.forEach((item: IInvoiceLineItem, index: number) => this.validate(index, item, currencyCode));
      }
    });

    $scope.validateInvoiceLineItem = (index: number, lineItem: IInvoiceLineItem, selectedInvoiceCurrency: string) => this.validate(index, lineItem, selectedInvoiceCurrency);
    $scope.removeChargeItem = (index: number) => this.$scope.lineItems.splice(index, 1);
    $scope.getUtility = (index: number, lineItem: IInvoiceLineItem, amount: number) => this.getUtility(index, lineItem, amount);
    $scope.lineItemError = [];
    utilitiesTownsService.listAll().then((towns: ITown[]) => $scope.townsList = towns);
    aerodromesService.getAerodromesByBillingCentre().then((aerodromes: IAerodrome[]) => $scope.aerodromeList = aerodromes);
    aerodromesService.listAll().then((aerodromes: IAerodromeSpring) => $scope.aerodromeListAll = aerodromes.content);
    utilitiesSchedulesService.listAll().then((data: IScheduleSpring) => $scope.schedules = data.content);
    $scope.checkAll = () => this.checkAll();
    $scope.itemCheck = () => this.$scope.selectAll = false;

    // external database related
    $scope.isLocalInput = (lineItem: IInvoiceLineItem) => !this.isExternalDatabaseInput(lineItem);
    $scope.hasLocalInputs = () => {
      if ($scope.lineItems) {
        // check if any line items are local inputs
        return $scope.lineItems.find((item: IInvoiceLineItem) => $scope.isLocalInput(item));
      } else {
        // no line items so no local input
        return false;
      }
    };

    $scope.isExternalDatabaseInput = (lineItem: IInvoiceLineItem) => this.isExternalDatabaseInput(lineItem);
    $scope.hasExternalInputs = () => {
      if ($scope.lineItems) {
        // check if any line items are external database inputs
        return $scope.lineItems.find((item: IInvoiceLineItem) => $scope.isExternalDatabaseInput(item));
      } else {
        // no line items so no external databse inputs
        return false;
      }
    };

    $scope.getAccountExternalChargeCategories = (item: IInvoiceLineItem) => this.getAccountExternalChargeCategories(item);

    this.setAccountExternalChargeCategories($scope.params.accountId);
    $scope.$watch('params.accountId', (newAccountId: number) => this.setAccountExternalChargeCategories(newAccountId));

  }

  /**
   * Validates line item by month
   * If valid, it updates the line item amount
   *
   * @param  {number} index
   * @param  {IInvoiceLineItem} lineItem
   * @returns void
   */
  private validate(index: number, lineItem: IInvoiceLineItem, selectedInvoiceCurrency: string): void {

    lineItem.amount = null; // need to clear amount so that it recalculates if the user changes a value. This also disables things from being `valid` when fields are missing
    let acountExternalList: Array<IAccountExternalChargeCategory> = this.$scope.lineItemAccountExternalChargeCategories[index];
    if (!this.$scope.params.accountId || !lineItem.aerodrome || !lineItem.aerodrome.id
      || (!lineItem.user_unit_amount && lineItem.service_charge_catalogue.charge_basis !== 'fixed')
      || (!lineItem.account_external_system_identifier && acountExternalList && acountExternalList.length > 1)) {
      return;
    }

    let month: number = null,
      year: number = null;

    if (this.$scope.params.month !== null) {
      month = this.$scope.params.month;
    }

    if (this.$scope.params.year !== null) {
      year = this.$scope.params.year;
    }

    this.reportsService.validateInvoiceLineItem(this.$scope.params.accountId, lineItem, month, year, selectedInvoiceCurrency)
      .then((itemReturned: IInvoiceLineItem) => {
        this.$scope.lineItemError[index] = null;
        lineItem.amount = itemReturned.amount;
        this.$scope.$emit('lineItem amount changed');
        this.$scope.isValid = this.areAllLineItemsValid();
      })
      .catch((error: IRestangularResponse) => {

        if (error.data.error_description && error.data.error_description !== 'null') { // back-end sometimes sends this string
          this.$scope.lineItemError[index] = error.data.error_description;
        } else {
          this.$scope.lineItemError[index] = error.data.error;
        }

        this.$scope.isValid = this.areAllLineItemsValid();
      });
  }

  private areAllLineItemsValid(): boolean {

    if (!this.$scope.lineItems) {
      return;
    }

    for (let item of this.$scope.lineItems) {
      if (item.amount === null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Shows or hides the town / electricity charge type columns
   *
   * @param  {Array<IInvoiceLineItem>} lineItems
   * @returns void
   */
  private dynamicTable(lineItems: Array<IInvoiceLineItem>): void {
    if (!lineItems) {
      return;
    }

    this.$scope.isWater = false;
    this.$scope.isElectric = false;
    this.$scope.isFixed = false;
    this.$scope.isExternalDatabase = false;
    this.$scope.isAccountExternalSystemIdentifier = false;

    for (let item of lineItems) {
      if (item.service_charge_catalogue.charge_basis === 'unit' || item.service_charge_catalogue.charge_basis === 'fixed' || item.service_charge_catalogue.charge_basis === 'percentage') {
        item.price_per_unit = item.service_charge_catalogue.amount;
      }

      if (item.service_charge_catalogue.charge_basis === 'water') {
        this.$scope.isWater = true;
      }
      if (item.service_charge_catalogue.charge_basis === 'commercial-electric' || item.service_charge_catalogue.charge_basis === 'residential-electric') {
        this.$scope.isElectric = true;
      }
      if (item.service_charge_catalogue.charge_basis === 'fixed' && lineItems.length === 1) {
        this.$scope.isFixed = true;
      }
      if (item.service_charge_catalogue.external_charge_category && this.accountExternalChargeCategories.filter((category: IAccountExternalChargeCategory) =>
        category.external_charge_category.id === item.service_charge_catalogue.external_charge_category.id).length > 1) {
        this.$scope.isAccountExternalSystemIdentifier = true;
      }
    }
  }

  private getUtility(index: number, lineItem: IInvoiceLineItem, amount: number): void {
    if (lineItem === null || lineItem === undefined || amount === null || amount === undefined) {
      return;
    }

    if (amount === 0) {
      this.$scope.lineItems[index].price_per_unit = 1; // hard-coded to 1 because that's what the back-end is returning
      return;
    }

    let scheduleId;
    let rangeBracket = [];

    if (lineItem.service_charge_catalogue.charge_basis === 'water') {
      scheduleId = lineItem.user_town.water_utility_schedule.schedule_id;
    } else if (lineItem.service_charge_catalogue.charge_basis === 'commercial-electric') {
      scheduleId = lineItem.user_town.commercial_electricity_utility_schedule.schedule_id;
    } else if (lineItem.service_charge_catalogue.charge_basis === 'residential-electric') {
      scheduleId = lineItem.user_town.residential_electricity_utility_schedule.schedule_id;
    }

    for (let schedule of this.$scope.schedules) {
      if (schedule.schedule_id === scheduleId) {
        rangeBracket = schedule.utilities_range_bracket;
      }
    }

    let ranges = rangeBracket.map((item: IRange) => item.range_top_end);

    let closest = Math.max.apply(null, ranges); // get the highest number in arr in case it match nothing.

    for (let i = 0; i < ranges.length; i++) {
      if (ranges[i] >= amount && ranges[i] < closest) { // check if it's higher than amount, but lower than closest value
        closest = ranges[i];
      }
    }

    if (amount > closest) { // if amount higher than highest top range, set to null
      this.$scope.lineItems[index].price_per_unit = null;
      return;
    }

    for (let bracket of rangeBracket) {
      if (bracket.range_top_end === closest) {
        this.$scope.lineItems[index].price_per_unit = bracket.unit_price;
      }
    }
  }

  private checkAll(): void {
    for (let item of this.$scope.lineItems) {
      item.any_aerodrome = this.$scope.selectAll ? true : false;
    }
  }

  private isExternalDatabaseInput(lineItem: IInvoiceLineItem): boolean {
    return lineItem.service_charge_catalogue.charge_basis === 'external-database';
  }

  private setAccountExternalChargeCategories(accountId: number): void {

    if (!accountId) {
      this.accountExternalChargeCategories = [];
    } else {
      this.accountExternalChargeCategoryService.getByAccountId(accountId)
        .then((response: Array<IAccountExternalChargeCategory>) => this.accountExternalChargeCategories = response)
        .catch((error: IRestangularResponse) => this.accountExternalChargeCategories = []);
    }
  }

  private getAccountExternalChargeCategories(lineItem: IInvoiceLineItem): Array<IAccountExternalChargeCategory> {

    if (lineItem.service_charge_catalogue && lineItem.service_charge_catalogue.external_charge_category) {
      return this.accountExternalChargeCategories.filter((item: IAccountExternalChargeCategory) =>
        item.external_charge_category.id === lineItem.service_charge_catalogue.external_charge_category.id);
    } else {
      return [];
    }
  }
}
