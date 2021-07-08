// interfaces
import { ISpringPageableParams } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { ISystemConfiguration } from '../system-configuration.interface';
import { ISystemConfigurationSpring } from '../system-configuration.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { LocalStorageService } from '../../../angular-ids-project/src/components/services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../system-configuration.constants';

export let endpoint: string = 'system-configurations';

export class SystemConfigurationService extends CRUDService {

  protected restangular: restangular.IService;
  systemConfigurationContent: any;

  private _mod: Array<ISystemConfiguration> = [{
    id: null,
    item_name: null,
    item_class: {
      id: null,
      name: null
    },
    data_type: {
      id: null,
      name: null
    },
    units: null,
    range: null,
    default_value: null,
    current_value: null,
    client_storage_forbidden: null,
    system_validation_type: null,
    display_units: null
  }];

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private $filter: ng.IFilterService, private $translate: angular.translate.ITranslateService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getLanguageConfiguration(): ng.IPromise<any> {
    return this.restangular.one(`${this.endpoint}/noauth/getLanguages`).get().then((resp: Array<ISystemConfiguration>) => {
      return this.restangular.stripRestangular(resp);
    }, (err: any) => {
      console.error(err);

      return null;
    });
  }

  public getUnitsOfMeasureConfiguration(): ng.IPromise<any> {
    return this.restangular.one(`${this.endpoint}/noauth/getUnitsOfMeasure`).get().then((resp: Array<ISystemConfiguration>) => {
      return this.restangular.stripRestangular(resp);
    }, (err: any) => {
      console.error(err);

      return null;
    });
  }

  public getAirNavigationChargesCurrency(): ng.IPromise<any> {
    return this.restangular.one(`${this.endpoint}/noauth/getAirNavigationChargesCurrency`).get().then((resp: ISystemConfiguration) => {
      return this.restangular.stripRestangular(resp);
    }, (err: any) => {
      console.error(err);

      return null;
    });
  }

  public getANSPCurrency(): ng.IPromise<any> {
    return this.restangular.one(`${this.endpoint}/noauth/getANSPCurrency`).get().then((resp: ISystemConfiguration) => {
      return this.restangular.stripRestangular(resp);
    }, (err: any) => {
      console.error(err);

      return null;
    });
  }

  public getSupportedLanguages(): any {
    const options = [];
    const languageOptions = this.systemConfigurationContent.find((option: any) => option.item_name === 'Language supported');
    if (languageOptions) {
      JSON.parse(languageOptions.current_value).map((language: any) => options.push({ id: language.code, label: language.label }));
    }
    return options;
  }

  public getLanguageOptions(): any {
    const options = [];
    const languageOptions = this.systemConfigurationContent.find((option: any) => option.item_name === 'Language supported');
    if (languageOptions) {
      JSON.parse(languageOptions.range).map((language: any) => options.push({ id: language.code, label: language.label }));
    }
    return options;
  }

  // on save it updates localStorage variables which should be reflected in the UI instantly
  public list(): ng.IPromise<ISystemConfigurationSpring> {
    // override default size to ensure all entries are retrieved
    const params: ISpringPageableParams = { size: -1 };

    return super.list(params).then((systemConfiguration: ISystemConfigurationSpring) => this.parseResults(systemConfiguration));
  }

  public listByPluginId(pluginId: number): ng.IPromise<ISystemConfigurationSpring> {
    return this.restangular.one(`${this.endpoint}/plugins/${pluginId}`).get()
      .then((response: ISpringPageableParams) => super.rectifyPageableParams(response))
      .then((systemConfiguration: ISystemConfigurationSpring) => this.parseResults(systemConfiguration));
  }

  public listForClientStorage(): ng.IPromise<ISystemConfigurationSpring> {
    return this.restangular.one(`${this.endpoint}/storage`).get()
      .then((response: ISpringPageableParams) => super.rectifyPageableParams(response))
      .then((systemConfiguration: ISystemConfigurationSpring) => this.parseResults(systemConfiguration));
  }

  public validateItem(item: ISystemConfiguration): ng.IPromise<boolean> {
    return this.restangular.all(`${this.endpoint}/validate`).post(item);
  }

  public validateInvoiceByFm(item: ISystemConfiguration[]): ng.IPromise<boolean> {
    return this.restangular.all(`${this.endpoint}/validate/fm`).post(item);
  }

  // returns configuration value by name
  public getValueByName(name: string): any {
    const val = LocalStorageService.get(`SystemConfiguration:${name}`);

    if (typeof (val) === 'undefined' || val === null) {
      console.warn(`could not find configuration for ${name}.`);
    }

    return val;
  }

  public getBooleanFromValueByName (name: string): boolean {
    return this.getValueByName(name) === <any>SysConfigConstants.SYSTEM_CONFIG_TRUE;
  }

  public convertMtowProperty(editable: any, prop: string): any {
    let mtow = angular.copy(editable);
    const mtowUnit = this.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);

    if (mtowUnit === 'kg') {
      const mtowInKg = this.$filter('number')(mtow[prop] * 907.185, 0);
      let mtowAsFloat: number;
      if (mtowInKg !== null) {
        mtowAsFloat = parseFloat(mtowInKg.replace(/,/g, ''));
      }
      mtow[prop] = mtowAsFloat;
    } else {
      const mtowInTons = this.$filter('number')(mtow[prop], 2);
      if (mtowInTons !== null) {
        mtow[prop] = parseFloat(mtowInTons.replace(/,/g, ''));
      }
    }

    return mtow;
  }

  public convertCargoProperty(editable: any, prop: string): any {
    let cargo = angular.copy(editable);
    const cargoUnit = this.getValueByName(<any>SysConfigConstants.CARGO_DISPLAY_UNITS);
    const properties = prop.split(',');

    properties.forEach((element: string) => {
      if (cargoUnit === 'kg') {
        const cargoInKg = cargo[element] ? parseFloat(this.$filter('number')(cargo[element] * 907.185, 0).replace(/,/g, '')) : null;
        cargo[element] = cargoInKg;
      } else {
        const cargoInTons = cargo[element] ? parseFloat(this.$filter('number')(cargo[element], 2).replace(/,/g, '')) : null;
          cargo[element] = cargoInTons;
      }
    });

    return cargo;
  }

  public convertDistanceProperty(editable: any, prop: string, distanceValue: string): any {
    const toConvert: boolean = editable[prop] || editable[prop] === 0 ? true : false;
    let converted;
    if (toConvert) {
      if (distanceValue === 'nm') {
        converted = Number(this.$filter('number')(editable[prop] * 0.539957, 2).replace(/,/g, ''));
      } else {
        converted = Number(this.$filter('number')(editable[prop], 2).replace(/,/g, ''));
      }
    }

    return { ...editable, [prop]: converted };
  }

  public shouldShowCharge(chargeType: string): boolean {
    switch (chargeType) {
      case 'parking':
        return this.getBooleanFromValueByName(<any>SysConfigConstants.CALCULATE_PARKING_CHARGES);
      case 'arrival':
        return this.getBooleanFromValueByName(<any>SysConfigConstants.CALCULATE_ARRIVAL_CHARGES);
      case 'departure':
        return this.getBooleanFromValueByName(<any>SysConfigConstants.CALCULATE_DEPARTURE_CHARGES);
      case 'TASP':
        return this.getBooleanFromValueByName(<any>SysConfigConstants.CALCULATE_TASP_CHARGES);
      case 'passenger':
        return this.getBooleanFromValueByName(<any>SysConfigConstants.PASSENGER_CHARGES_SUPPORT);
      case 'extendedHours':
        return this.getBooleanFromValueByName(<any>SysConfigConstants.EXTENDED_HOURS_SURCHARGE_SUPPORT);
      default:
        return true;
    }
  }

  /**
   * @param  {ISystemConfigurationSpring} data
   * @returns ISystemConfiguration - crossing distance precedence
   */
  public setCrossingDistance(): ISystemConfiguration {
    for (let item of this.systemConfigurationContent) {
      if (item.item_name === 'Crossing distance precedence') {
        return item;
      };
    };
  }

  public isLocaleEnglish(): boolean {
    return this.$translate.use().startsWith('en');
  }

  private parseResults(systemConfiguration: ISystemConfigurationSpring): ISystemConfigurationSpring {
    this.systemConfigurationContent = systemConfiguration.content;

    if (!systemConfiguration.content) {
      return systemConfiguration;
    }

    for (let i = 0; i < systemConfiguration.content.length; i++) {

      // this contains the user
      // selected distance unit
      // of measure, before it
      // is stored locally
      const distanceBeforeStorage = <any>systemConfiguration.content.find((item: ISystemConfiguration) =>
        item.item_name === SysConfigConstants.DISTANCE_UNIT_OF_MEASURE.toString()
      );
      const distanceValueBeforeStorage = distanceBeforeStorage ? distanceBeforeStorage.current_value : '';

      // because we use the input[type=number] and max/min attrs, we need to convert to number here
      if (systemConfiguration.content[i].data_type.name === 'int' || systemConfiguration.content[i].data_type.name === 'float') {
        systemConfiguration.content[i].current_value = Number(systemConfiguration.content[i].current_value);

        // handle distances props that need to be converted
        if (systemConfiguration.content[i].item_name.includes('distance')) {
          systemConfiguration.content[i] = this.convertDistanceProperty(systemConfiguration.content[i], 'current_value', distanceValueBeforeStorage);
        };
      }

      // because the label (item_name) is also the primary key,
      // if we want to change the label, we add a 'display_name'
      // to the object and display that instead
      const displayNames = this.getNames(distanceValueBeforeStorage);
      for (let name of displayNames) {
        if (name[0] === systemConfiguration.content[i].item_name) {
          systemConfiguration.content[i].display_units = <string>name[1];
        }
      }

      // bind all the system configurations
      let configValue: string | number;
      if (systemConfiguration.content[i].current_value !== undefined && systemConfiguration.content[i].current_value !== null) {
        configValue = systemConfiguration.content[i].current_value;
      } else {
        configValue = systemConfiguration.content[i].default_value;
      }

      if (systemConfiguration.content[i].item_name.indexOf('Language') === -1 && !systemConfiguration.content[i].client_storage_forbidden) {
        LocalStorageService.set(`SystemConfiguration:${systemConfiguration.content[i].item_name}`, configValue);
      };

    }

    return systemConfiguration;
  }

  private getNames(distanceValueBeforeStorage: string): Array<Array<SysConfigConstants | String>> {
    const ANSP = this.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    const dUM = distanceValueBeforeStorage;

    return [ // item_name, display_name
      [SysConfigConstants.ENTRY_EXIT_POINT_ROUNDING_DISTANCE, this.getLabel(<any>SysConfigConstants.ENTRY_EXIT_POINT_ROUNDING_DISTANCE, dUM)],
      [SysConfigConstants.EXEMPT_FLIGHTS_DISTANCE, this.getLabel(<any>SysConfigConstants.EXEMPT_FLIGHTS_DISTANCE, dUM)],
      [SysConfigConstants.MAX_DOMESTIC_CROSSING_DISTANCE, this.getLabel(<any>SysConfigConstants.MAX_DOMESTIC_CROSSING_DISTANCE, dUM)],
      [SysConfigConstants.MAX_REGIONAL_CROSSING_DISTANCE, this.getLabel(<any>SysConfigConstants.MAX_REGIONAL_CROSSING_DISTANCE, dUM)],
      [SysConfigConstants.MAX_INTERNATIONAL_CROSSING_DISTANCE, this.getLabel(<any>SysConfigConstants.MAX_INTERNATIONAL_CROSSING_DISTANCE, dUM)],
      [SysConfigConstants.MIN_DOMESTIC_CROSSING_DISTANCE, this.getLabel(<any>SysConfigConstants.MIN_DOMESTIC_CROSSING_DISTANCE, dUM)],
      [SysConfigConstants.MIN_REGIONAL_CROSSING_DISTANCE, this.getLabel(<any>SysConfigConstants.MIN_REGIONAL_CROSSING_DISTANCE, dUM)],
      [SysConfigConstants.MIN_INTERNATIONAL_CROSSING_DISTANCE, this.getLabel(<any>SysConfigConstants.MIN_INTERNATIONAL_CROSSING_DISTANCE, dUM)],
      [SysConfigConstants.DEFAULT_ACCOUNT_MONTHLY_PENALTY, this.getLabel(<any>SysConfigConstants.DEFAULT_ACCOUNT_MONTHLY_PENALTY, '%')],
      [SysConfigConstants.DEFAULT_ACCOUNT_PARKING_EXEMPTION, this.getLabel(<any>SysConfigConstants.DEFAULT_ACCOUNT_PARKING_EXEMPTION, '%')],
      [SysConfigConstants.DEFAULT_ACCOUNT_PAYMENT_TERMS, this.getLabel(<any>SysConfigConstants.DEFAULT_ACCOUNT_PAYMENT_TERMS, true)],
      [SysConfigConstants.MIN_RANGE_FOR_FLIGHT_MATCH, this.getLabel(<any>SysConfigConstants.MIN_RANGE_FOR_FLIGHT_MATCH, true)],
      [SysConfigConstants.PERCENT_EET_USED_FOR_FLIGHT_MATCH, this.getLabel(<any>SysConfigConstants.PERCENT_EET_USED_FOR_FLIGHT_MATCH, '%')],
      [SysConfigConstants.ATC_MOVEMENT_RETENTION, this.getLabel(<any>SysConfigConstants.ATC_MOVEMENT_RETENTION, true)],
      [SysConfigConstants.FLIGHT_MOVEMENT_RETENTION, this.getLabel(<any>SysConfigConstants.FLIGHT_MOVEMENT_RETENTION, true)],
      [SysConfigConstants.LINE_ITEM_RETENTION, this.getLabel(<any>SysConfigConstants.LINE_ITEM_RETENTION, true)],
      [SysConfigConstants.USER_EVENT_LOG_RETENTION, this.getLabel(<any>SysConfigConstants.USER_EVENT_LOG_RETENTION, true)],
      [SysConfigConstants.DEFAULT_ACCOUNT_CREDIT_LIMIT, this.getLabel(<any>SysConfigConstants.DEFAULT_ACCOUNT_CREDIT_LIMIT, ANSP)],
      [SysConfigConstants.DEFAULT_ACCOUNT_MIN_CREDIT_NOTE, this.getLabel(<any>SysConfigConstants.DEFAULT_ACCOUNT_MIN_CREDIT_NOTE, ANSP)],
      [SysConfigConstants.DEFAULT_ACCOUNT_MAX_CREDIT_NOTE, this.getLabel(<any>SysConfigConstants.DEFAULT_ACCOUNT_MAX_CREDIT_NOTE, ANSP)],
      [SysConfigConstants.MTOW_UNIT_OF_MEASURE, this.getLabel(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE, 'tons / kg')],
      [SysConfigConstants.AUTOMATED_UPLOAD_SCHEDULING, this.getLabel(<any>SysConfigConstants.AUTOMATED_UPLOAD_SCHEDULING, true)],
      [SysConfigConstants.CACHED_EVENT_RETRY_INTERVAL, this.getLabel(<any>SysConfigConstants.CACHED_EVENT_RETRY_INTERVAL, true)],
      [SysConfigConstants.DISTANCE_UNIT_OF_MEASURE, this.getLabel(<any>SysConfigConstants.DISTANCE_UNIT_OF_MEASURE, 'km / nm')],
      [SysConfigConstants.MOCK_FATAL_ERROR_OCCURRENCE_FREQUENCY, this.getLabel(<any>SysConfigConstants.MOCK_FATAL_ERROR_OCCURRENCE_FREQUENCY, '%')],
      [SysConfigConstants.DUPL_OR_MISS_FLIGHTS_MIN_WIND, this.getLabel(<any>SysConfigConstants.DUPL_OR_MISS_FLIGHTS_MIN_WIND, true)],
      [SysConfigConstants.DUPL_OR_MISS_FLIGHTS_EET_PERC, this.getLabel(<any>SysConfigConstants.DUPL_OR_MISS_FLIGHTS_EET_PERC, '%')],
      [SysConfigConstants.FIRST_DAY_OF_FISCAL_YEAR, this.getLabel(<any>SysConfigConstants.FIRST_DAY_OF_FISCAL_YEAR, true)],
      [SysConfigConstants.SMALL_AIRCRAFT_MAXIMUM_WEIGHT, this.getLabel(<any>SysConfigConstants.SMALL_AIRCRAFT_MAXIMUM_WEIGHT, true)],
      [SysConfigConstants.SMALL_AIRCRAFT_MINIMUN_WEIGHT, this.getLabel(<any>SysConfigConstants.SMALL_AIRCRAFT_MINIMUN_WEIGHT, true)],
      [SysConfigConstants.WL_INACTIVITY_PERIOD, this.getLabel(<any>SysConfigConstants.WL_INACTIVITY_PERIOD, true)],
      [SysConfigConstants.WL_EXPIRY_PERIOD, this.getLabel(<any>SysConfigConstants.WL_EXPIRY_PERIOD, true)]
    ];
  }

  private getLabel(name: string, addText: string | number | boolean): string {
    let label;
    this.systemConfigurationContent.forEach((element: any) => {
      if (element.item_name === name) {
        if (typeof(addText) === 'boolean') {
          label = `(${element.units})`;
        } else {
          label = `(${addText})`;
        }
        return;
      }
    });
    return label;

  }
}
