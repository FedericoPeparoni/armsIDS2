// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRejectedItemScope, IRejectedItem } from './rejected-items.interface';
import { IConvertItemTypeProperties } from '../convert-item-type/convert-item-type.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// services
import { RejectedItemsService, RejectedItemType } from './service/rejected-items.service';
import { SaveLocalTemplateService } from '../../angular-ids-project/src/components/services/saveLocalTemplate/saveLocalTemplate.service';
import { ConvertItemTypeService, ConvertItemType } from '../convert-item-type/service/convert-item-type.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// no need for the user service as there are no permissions
export class RejectedItemsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IRejectedItemScope, private $state: angular.ui.IStateService, private rejectedItemsService: RejectedItemsService,
    private saveLocalTemplateService: SaveLocalTemplateService, private convertItemTypeService: ConvertItemTypeService, private customDate: CustomDate) {
    super($scope, rejectedItemsService);
    super.setup();

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    this.$scope.toggle = true;

    $scope.$watchGroup(['search', 'control', 'filter-by-record-type', 'filter-by-status', 'filter-by-originator', 'filter-by-file-name'], () => this.getFilterParameters());

    this.$state = $state;
    $scope.update = () => this.updateOverride();
    $scope.delete = () => super.delete(this.$scope.id).then(() => this.$scope.displayed_data = []);
    $scope.reset = () => this.$scope.displayed_data = [];
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.selectFirstIfNoSelection = () => {
      if (!$scope.displayed_data) {
        this.edit($scope.list[0]);
      }
    };
    this.saveLocalTemplate();
  }

  /**
   * For 'Flight Movement' and 'Radar Summary' types:
   * This function create an object from decoded
   * 'json_text' field reterned from server
   *
   * Loops through the data in this object to fix
   * value type in key/value pair (date, string, number and null)
   *
   * Compare keys of this object with an array that containes names of fields need to be shown
   * and create an new array (header/value) with data that need to be shown for user
   *
   * Convert 'Flight Movement'.'dayOfFlight' field in the new array from milliseconds to date
   *
   * For other types:
   * This function splits 'header' and 'raw_text'
   * fields reterned from server into arrays
   *
   * These arrays are used as key/value to populate
   * a new array (header/value) to display information for user
   *
   * @param  {IRejectedItem} item
   * @returns void
   */
  protected edit(item: IRejectedItem): void {
    this.$scope.reset();

    this.$scope.data = [];
    this.$scope.userInput = []; // callect changed data by user
    this.$scope.item = item;
    this.$scope.id = item.id;
    this.$scope.error = null; // clear error when selecting a new item
    let displayed_data = []; // used to display data from server

    if (item.record_type === RejectedItemType[RejectedItemType.FLIGHT_MOV]
      || item.record_type === RejectedItemType[RejectedItemType.RADAR_SUMM]) { // calls when record_type is 'Flight Movement' or 'Radar Summary'

      // get decoded json object
      const decodedJson: any = this.decodeToObject(item.json_text);

      // get fields to show based on record type
      const showData: Array<string> = this.visibleFields(item.record_type, decodedJson.format);

      // get convert item properties base on record type
      const convertProperties: Object = this.convertItemTypeProperties(item.record_type);

      // loop through each object property, fix
      // data types and add to displayed data
      // if it exists in showData list
      for (let value in decodedJson) {
        if (decodedJson.hasOwnProperty(value)) {

          // set data value from decoded object
          let dataValue = decodedJson[value];

          // run additional data parsing if exists
          if (dataValue) {

            // check if type of custom date from api
            // or in the format of toISOString
            // deprecated: for outdated json_text data
            if (dataValue.hasOwnProperty('chronology')) {
              dataValue = new Date(Date.UTC(
                dataValue.year,
                dataValue.monthValue - 1,
                dataValue.dayOfMonth,
                dataValue.hour,
                dataValue.minute,
                dataValue.second));
            } else if (moment(dataValue, moment.ISO_8601).isValid()) {
              dataValue = new Date(dataValue);
            }

            // convert item format if exists in convert properties
            if (convertProperties[value]) {
              dataValue = this.convertItemTypeService.itemTo(dataValue, convertProperties[value]);
            }

            // update decodedJson object with new value type
            decodedJson[value] = dataValue;
          }

          // push data value to displayed data
          if (showData.indexOf(value) > -1) {
            let type = Object.prototype.toString.call(dataValue);
            if (value === 'day_of_flight') {
              type = '[object Date]';
            }
            displayed_data.push({
              header: this.headerToText(value, item.record_type),
              data: dataValue,
              type: type,
              isMultiLineField: this.isMultilineField (item.record_type, value)
            });
          }
        }
      }

      // set scope resutls from decoded json
      this.$scope.result = decodedJson;

    } else { // calls when record_type is not accounted for above

      // split header and raw_text on comma characters
      let header = item.header.split(',');
      this.$scope.data = item.raw_text.split(',');

      // add data from 'header' and 'raw_text' to display
      for (let i = 0; i < header.length; i++) {

        // all Unknowns fields are hidden
        if (header[i] === '') {
          header[i] = '-Unknown-';
        }

        // push item to displayed data
        displayed_data.push({
          header: this.headerToText(header[i], item.record_type),
          data: this.$scope.data[i],
          type: Object.prototype.toString.call(this.$scope.data[i]),
          isMultiLineField: false
        });
      }
    }

    // update merge upload checkbox based on init logic
    this.$scope.merge_upload = this.$scope.result && this.$scope.result.format === 'INDRA_REC';

    // push dispalyed data to scope
    this.$scope.displayed_data = displayed_data;
  }
  /**
   * For 'Flight Movement' and 'Radar Summary' types:
   * Insert new value from user into decoded object;
   * Convert 'Flight Movement'.'dayOfFlight' field in this object from date to milliseconds;
   * Call update method with this object
   *
   * For other types:
   * This function create an object from data that entered by user
   * Call update method with this object
   *
   * @returns void
   */
  protected updateOverride(): void {

    // used as data trasfer object
    let obj: any = new Object();

    if (this.$scope.item.record_type === RejectedItemType[RejectedItemType.FLIGHT_MOV]
      || this.$scope.item.record_type === RejectedItemType[RejectedItemType.RADAR_SUMM]) { // calls when record_type is 'Flight Movement' or 'Radar Summary'

      // loop through each user defined input and updated displayed data
      for (let i = 0; i < this.$scope.userInput.length; i++) {

        // set displayed data from user input
        this.$scope.displayed_data[i].data = this.$scope.userInput[i];

        // convert 'object Date' types to ISO string
        // or header value is 'day of flight' and item type is 'Flight Movement'
        if (this.$scope.displayed_data[i].type === '[object Date]'
          || (this.$scope.displayed_data[i].header === 'day of flight' && this.$scope.item.record_type === RejectedItemType[RejectedItemType.FLIGHT_MOV])) {
          let date = new Date(<string>this.$scope.displayed_data[i].data);
          if (!isNaN(date.getTime())) {
            this.$scope.displayed_data[i].data = date.toISOString();
          }
        }
      }

      // for each result, updated from displayed data
      for (let res in this.$scope.result) {
        if (this.$scope.result.hasOwnProperty(res)) {
          for (let data of this.$scope.displayed_data) {
            if (res === this.headerToProperty(data.header, this.$scope.item.record_type)) {
              this.$scope.result[res] = data.data;

              if (res === 'date' && this.$scope.item.record_type === RejectedItemType[RejectedItemType.RADAR_SUMM]) {
                //we have to even set 'dayOfFlight' property with value of 'date' property
                if (this.$scope.result.hasOwnProperty(this.getDayOfFlightPropertyName())) {
                  this.$scope.result[this.getDayOfFlightPropertyName()] = data.data;
                }
              }
            }
          }
        }
      }

      // set 'json_text' as base-64 encoded string
      obj.json_text = this.encodeToString(this.$scope.result);

    } else { // calls when record_type is NOT accounted for above

      // set user input based on data if length is less
      if (this.$scope.userInput.length < this.$scope.data.length) {
        this.$scope.userInput = this.$scope.userInput.concat(this.$scope.data.slice(this.$scope.userInput.length));
      }

      // set 'raw_text' based on user input
      obj.raw_text = this.$scope.userInput.join();
    }

    // set merge upload flag based on scope
    obj.merge_upload = this.$scope.merge_upload;

    // update rejected item object based on item id
    this.rejectedItemsService
      .updateOverride(obj, this.$scope.id, this.$scope.merge_upload).then(() => {
        super.reset();
        this.refreshOverride();
      }, (error: IRestangularResponse) => {
        throw super.setErrorResponse(error);
      });
  }

  private getFilterParameters(): void {
    let startDate: string;
    let endDate: string;

    if (this.$scope.control && this.$scope.control.getUTCStartDate()) {
      startDate = this.$scope.control.getUTCStartDate().toISOString().substr(0, 10);
    }

    if (this.$scope.control && this.$scope.control.getUTCEndDate()) {
      endDate = this.$scope.control.getUTCEndDate().toISOString().substr(0, 10);
    }

    this.$scope.filterParameters = {
      'search': this.$scope.search,
      'filter-by-record-type': this.$scope.filter_by_record_type,
      'filter-by-status': this.$scope.filter_by_status,
      'filter-by-originator': this.$scope.filter_by_originator,
      'filter-by-file-name': this.$scope.filter_by_file_name,
      'start': startDate,
      'end': endDate,
      'page': this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * Calls list with applied filters
   *
   * @returns void
   */
  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  /**
   * Use to decode base-64 encoded string as an object.
   *
   * @param {string} baseString base-64 encoded string
   * @returns {object} decoded string as an object
   */
  private decodeToObject(baseString: string): object {

    // decodes string that has been encoded using base-64 encoding
    let value = atob(baseString);

    // return decoded string as an object
    return JSON.parse(value);
  }

  /**
   * Use to encode an object as a base-64 encoded string.
   *
   * @param {object} baseObject object to be encoded
   * @return {string} encoded object as a string
   */
  private encodeToString(baseObject: object): string {

    // stringify object that is to be encoded
    let value = JSON.stringify(baseObject);

    // return encoded object as a base-64 encoding
    return btoa(value);
  }

  /**
   * Returns a list of visiable fields based on
   * the supplied rejected item type value.
   *
   * @param {string} itemType rejected item type
   * @param {string} itemFormat rejected item format (ie: INDRA_REC for radar)
   * @returns {string[]} list of visiable fields
   */
  private visibleFields(itemType: string, itemFormat?: string): Array<string> {
    if (itemType === RejectedItemType[RejectedItemType.FLIGHT_MOV]) {
      return this.visibleFieldsFlightMov();
    } else if (itemType === RejectedItemType[RejectedItemType.RADAR_SUMM]) {
      return this.visibleFieldsRadarSumm(itemFormat);
    } else {
      return [];
    }
  }

  /**
   * Returns a string with property name dayOfFlight
   * flight movement/radar summary rejectable item.
   *
   * @returns {string} dayOfFlight property name
   */
  private getDayOfFlightPropertyName(): string {
    return 'dayOfFlight';
  }

  /**
   * Returns a list of visiable fields based on
   * flight movement rejectable item.
   *
   * @returns {string[]} list of visiable fields
   */
  private visibleFieldsFlightMov(): Array<string> {
    return [
      'flight_id', 'flight_rules', 'flight_type', 'aircraft_type', 'departure_ad', 'departure_time', 'route',
      'destination_ad', 'total_eet', 'day_of_flight', 'other_info', 'speed', 'flight_level'
    ];
  }

  /**
   * Returns a list of visiable fields based on radara summary
   * rejectable item format.
   *
   * @param {string} itemFormat radar summary format (ie: INDRA_REC for radar)
   * @returns {string[]} list of visiable fields
   */
  private visibleFieldsRadarSumm(itemFormat?: string): Array<string> {
    let fields: Array<string> = [
      'flightTravelCategory', 'flightIdentifier', 'date', 'registration', 'flightType', 'flightRule', 'aircraftType', 'wakeTurb',
      'destinationAeroDrome', 'destTime', 'departureAeroDrome', 'departureTime', 'cruisingSpeed', 'flightLevel', 'firEntryPoint', 'firEntryTime',
      'firEntryFlightLevel', 'firExitPoint', 'firExitTime', 'firExitFlightLevel'
    ];

    if (itemFormat === 'INDRA_REC') {
      fields.push('fixes');
    } else {
      fields.push('route');
    }

    return fields;
  }

  /**
   * @param itemType Returns true if the given field should be displayed as a text area
   */
  private isMultilineField (itemType: string, fieldId: string): boolean {
    if (itemType === RejectedItemType[RejectedItemType.FLIGHT_MOV]) {
      return this.isMultilineFieldFlightMov (fieldId);
    } else {
      return false;
    }
  }

  /**
   * @param itemType Returns true if the given Flight Movement field should be displayed as a text area
   */
  private isMultilineFieldFlightMov (fieldId: string): boolean {
    if (fieldId === 'other_info') {
      return true;
    }
    return false;
  }

  /**
   * Returns an object of converstion properties for each
   * item based on the supplied rejected item type value.
   *
   * This is used in conjunction with convertItemTo method.
   *
   * It is used here only to correct json_text field types.
   *
   * @param {string} itemType rejected item type
   * @returns {Object} conversion properties
   */
  private convertItemTypeProperties(itemType: string): Object {
    if (itemType === RejectedItemType[RejectedItemType.RADAR_SUMM]) {
      return {
        date: <IConvertItemTypeProperties>{ source: ConvertItemType.DATE, target: ConvertItemType.STRING, format: 'YYMMDD', utc: true }
      };
    } else {
      return {};
    }
  }

  /**
   * Returns use friendly readable text value of header property
   * based on the rejected item type.
   *
   * 'Flight Moevemnts' are broken on underscore characters.
   * All other ttypes are broken on capitalized letters.
   *
   * Exception made for AeroDrome under RADAR_SUMM.
   *
   * @param {string} value property header value
   * @param {string} itemType rejected item type
   * @return {string} readable header value
   */
  private headerToText(value: string, itemType: string): string {

    // 'Flight Movements' are broken on underscore characters

    if (itemType === RejectedItemType[RejectedItemType.FLIGHT_MOV]) {
      return value.replace(/_/g, ' ').trim();
    } else if (itemType === RejectedItemType[RejectedItemType.RADAR_SUMM] && /AeroDrome/i.test(value)) {
      return value.replace(/AeroDrome/, 'Aerodrome').replace(/([A-Z])/g, ' $1').trim();
    } else if (itemType === RejectedItemType[RejectedItemType.ATS_MOV] && /Fl(Entry|Mid|Exit)Time/i.test(value)) {
      return value.replace(/fl(Entry|Mid|Exit)Time/, 'fir$1FlightLevel').replace(/([A-Z])/g, ' $1').trim();
    } else {
      return value.replace(/([A-Z])/g, ' $1').trim();
    }
  }

  /**
   * Returns property value used within the dto based on the
   * rejected item type.
   *
   * 'Flight Moevemnts' are combined on underscore characters.
   * All other ttypes are combined on capitalized letters.
   *
   * Exception made for AeroDrome under RADAR_SUMM.
   *
   * @param {string} value readable header value
   * @param {string} itemType rejceted item type
   * @return {string} property header value
   */
  private headerToProperty(value: string, itemType: string): string {
    if (itemType === RejectedItemType[RejectedItemType.FLIGHT_MOV]) {
      return value.replace(/ /g, '_').trim();
    } else if (itemType === RejectedItemType[RejectedItemType.RADAR_SUMM] && /Aerodrome/i.test(value)) {
      return value.replace(/ /g, '').replace(/Aerodrome/, 'AeroDrome').trim();
    } else if (itemType === RejectedItemType[RejectedItemType.ATS_MOV] && /fir(Entry|Mid|Exit)FlightLevel/i.test(value)) {
      return value.replace(/ /g, '').replace(/fir(Entry|Mid|Exit)FlightLevel/, 'fl$1Time').trim();
    } else {
      return value.replace(/ /g, '').trim();
    }
  }

  private saveLocalTemplate(): void {
    let filterPairs = [['search', 'search'], ['filter_by_record_type', 'filter-by-record-type'], ['filter_by_status', 'filter-by-status'],
    ['filter_by_originator', 'filter-by-originator'], ['filter_by_file_name', 'filter-by-file-name']];

    this.saveLocalTemplateService.saveLocalTemplate(this.$scope, 'rejected-items', filterPairs);
  }
}
