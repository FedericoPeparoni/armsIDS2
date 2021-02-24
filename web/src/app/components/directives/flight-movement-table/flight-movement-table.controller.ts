// constants
import { SysConfigConstants } from '../../../partials/system-configuration/system-configuration.constants';

// interface
import { IExportTableOptions, IFlightMovementTableAttributes } from './flight-movement-table.interface';

// services
import { SystemConfigurationService } from '../../../partials/system-configuration/service/system-configuration.service';

/** @ngInject */
export class FlightMovementTableController {
  yes_text: string;
  no_text: string;
  airNavCurrencyInHeader: string;
  domCurrencyInHeader: string;
  intlCurrencyInHeader: string;
  anspCurrency: string;
  mtowUnitOfMeasure: string;
  distanceUnitOfMeasure: string;
  approachLabel: string;
  exportTableOptions: IExportTableOptions;

  /**
   * Approach fees lable is ADAP flag based on system configuration settings.
   */
  isADAP: boolean;

  /**
   * Constructor used to inject dependencies and setup intial field and scope state.
   * 
   * @param systemConfigurationService system configuration service dependency
   * @param $attrs flight movement table attributes dependency
   * @param $scope flight movement table scope dependency
   */
  constructor(private systemConfigurationService: SystemConfigurationService, private $attrs: IFlightMovementTableAttributes, private $scope: angular.IScope) {

    // setup initial state
    this.setup();
  }

  /**
   * Setup field and scope values.
   */
  private setup(): void {
    this.setCurrencyInTableHeader();
    this.setExportTableOptions();
    this.setLabelsInTableHeader();
    this.setColumnVisibility();
  }

  /**
   * Looks at system configurations to determine what currency
   * the Dom/Intl Pax charges use, and sets the proper
   * currency code in the table header
   *
   * sets the ANSP currency in approach, aerodrome
   * late dep/arr, parking charges header
   *
   * sets the unit of measure in the MTOW header
   */
  private setCurrencyInTableHeader(): void {
    const ANSP = 'ANSP';
    const USD = 'USD';
    let anspCurrency, airNavCurrency, domPaxCurrency, intlPaxCurrency;

    anspCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    airNavCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.AIR_NAVIGATION_CHARGES_CURRENCY);
    domPaxCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DOM_PAX_CURRENCY);
    intlPaxCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.INTL_PAX_CURRENCY);

    // used for Dom/Intl passenger charges
    this.airNavCurrencyInHeader = airNavCurrency === ANSP ? anspCurrency : USD;
    this.domCurrencyInHeader = domPaxCurrency === ANSP ? anspCurrency : USD;
    this.intlCurrencyInHeader = intlPaxCurrency === ANSP ? anspCurrency : USD;

    // used for Approach, Aerodrome
    // late Dep/Arr, Parking charges
    this.anspCurrency = anspCurrency;

    // mtow - ton / kg
    this.mtowUnitOfMeasure = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE) as string;
    // distance - km / nm
    this.distanceUnitOfMeasure = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DISTANCE_UNIT_OF_MEASURE) as string;
  }

  /**
   * Set exprot table options to attribute binding or defaults if none supplied.
   * Box header value default is '2' and endpoint is '/filters' for legecy support.
   */
  private setExportTableOptions(): void {

    // evaluate options from directive attributes if any, one time binding
    this.exportTableOptions = <IExportTableOptions> {
      boxHeader: this.evaluate(this.$attrs.exportTableBoxHeader, 1),
      endpoint: '/filters'
    };

    // observe endpoint attribute and evalulate, continues binding
    this.$attrs.$observe('exportTableEndpoint', (value: string) =>
      this.exportTableOptions.endpoint = this.evaluate(value, '/filters'));
  }

  /**
   * Set approach label based on system configuration setting.
   */
  private setLabelsInTableHeader(): void {
    this.approachLabel = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.APPROACH_FEES_LABEL);
  }

  /**
   * Evaluate value against scsope and return result or default if undefined.
   * 
   * @param value value to evalulate
   * @param dflt default if undefined
   */
  private evaluate(value: any, dflt: any): any {
    return this.$scope.$eval(value) || dflt;
  }

  /**
   * Set necessary fields for determining column visibility.
   */
  private setColumnVisibility(): void {
    // is approach fees label defined as 'ADAP' from system configuration settings.
    this.isADAP = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.APPROACH_FEES_LABEL) === 'ADAP';
  }
}
