// interfaces
import { IAdjustmentCharge } from '../../../../../../partials/transactions/transactions.interface';
import { IFlightMovement } from '../../../../../../partials/flight-movement-management/flight-movement-management.interface';

// services
import { ChargesAdjustmentType } from '../../../../../../partials/charge-adjustments/service/charge-adjustments.service';
import { SystemConfigurationService } from '../../../../../../partials/system-configuration/service/system-configuration.service';
import { CaabSageSysConfigConstants } from '../../system-configuration/system-configuration.caab.sage.contants';
import { FlightType } from '../../../../../../partials/flight-types/service/flight-types.service';

export class CaabSageChargeAdjustmentsService {

  private static ENROUTE_EXTERNAL_CHARGE_CATEGORY: string = 'ENROUTE';
  private static IATA_EXTERNAL_CHARGE_CATEGORY: string = 'IATA';

  /** @ngInject */
  constructor(protected systemConfigurationService: SystemConfigurationService) {}

  /**
   * CAAB organization specific logic for resolving flight adjustment external properties.
   *
   * @param charge charge adjustment details
   * @param flight flight movement being adjusted
   */
  public resolveFlightAdjustementExternal(charge: IAdjustmentCharge, flight: IFlightMovement, invoiceType: string): void {

    // set external accounting system identifier by charge and flight
    charge.external_accounting_system_identifier = this.getExternalAccountingSystemIdentifier(charge, flight);

    // set exgternal charge category name by ivnoice type
    charge.external_charge_category_name = this.getExternalChargeCategoryName(invoiceType);
  }

  /**
   * Get external account system identifier (charge code) by charge adjustment descriptiong
   * and flight movement type from system configuration settings.
   *
   * @param charge charge adjustment details
   * @param flight flight movement being adjusted
   */
  private getExternalAccountingSystemIdentifier(charge: IAdjustmentCharge, flight: IFlightMovement): string {

    let isScheduled: boolean = flight.flight_type && flight.flight_type.toUpperCase() === 'S';
    let isDomestic: boolean = flight.movement_type === <any> FlightType.DOMESTIC;

    let chargeCode: string;
    switch (charge.charge_description) {
      case <any> ChargesAdjustmentType.ENROUTE_CHARGES:
        chargeCode = this.resolveEnrouteCharges(isScheduled);
        break;
      case <any> ChargesAdjustmentType.LANDING_CHARGES:
        chargeCode = this.resolveLandingCharges(isScheduled, isDomestic);
        break;
      case <any> ChargesAdjustmentType.PARKING_CHARGES:
        chargeCode = this.resolveParkingCharges();
        break;
      case <any> ChargesAdjustmentType.PASSENGER_CHARGES:
        // assumed domestic passenger charges being adjusted
        chargeCode = this.resolvePassengerCharges(true);
        break;
    default:
        chargeCode = this.resolveEnrouteCharges(isScheduled);
    }

    return chargeCode;
  }

  /**
   * Return external charge category name based on the invoice type.
   *
   * @param invoiceType invoice adjusted type
   */
  private getExternalChargeCategoryName(invoiceType: string): string {
    return invoiceType === 'aviation-iata'
      ? CaabSageChargeAdjustmentsService.IATA_EXTERNAL_CHARGE_CATEGORY
      : CaabSageChargeAdjustmentsService.ENROUTE_EXTERNAL_CHARGE_CATEGORY;
  }

  /**
   * Resolve domestic landing charge code by system configuration setting.
   *
   * @param isScheduled true if flight type scheduled ('S')
   */
  private resolveDomesticLandingCharges(isScheduled: boolean): string {

    let config: CaabSageSysConfigConstants = isScheduled
      ? CaabSageSysConfigConstants.DOMESTIC_LANDING_CHARGES_SCHEDULED_CODE
      : CaabSageSysConfigConstants.DOMESTIC_LANDING_CHARGES_NON_SCHEDULED_CODE;

      return this.systemConfigurationService.getValueByName(<any> config);
  }

  /**
   * Resolve enroute charge code by system configuration setting.
   *
   * @param isScheduled true if flight type scheduled ('S')
   */
  private resolveEnrouteCharges(isScheduled: boolean): string {

    let config: CaabSageSysConfigConstants = isScheduled
      ? CaabSageSysConfigConstants.ENROUTE_NAVIGATION_CHARGES_SCHEDULED_CODE
      : CaabSageSysConfigConstants.ENROUTE_NAVIGATION_CHARGES_NON_SCHEDULED_CODE;

    return this.systemConfigurationService.getValueByName(<any> config);
  }

  /**
   * Resolve international landing charge code by system configuration setting.
   *
   * @param isScheduled true if flight type scheduled ('S')
   */
  private resolveInternationalLandingCharges(isScheduled: boolean): string {

    let config: CaabSageSysConfigConstants = isScheduled
      ? CaabSageSysConfigConstants.INTERNATIONAL_LANDING_CHARGES_SCHEDULED_CODE
      : CaabSageSysConfigConstants.INTERNATIONAL_LANDING_CHARGES_NON_SCHEDULED_CODE;

      return this.systemConfigurationService.getValueByName(<any> config);
  }

  /**
   * Resolve landing charge code by system configuration setting.
   *
   * @param isScheduled true if flight type scheduled ('S')
   * @param isDomestic true if domestic landing charge
   */
  private resolveLandingCharges(isScheduled: boolean, isDomestic: boolean): string {
    return isDomestic
      ? this.resolveDomesticLandingCharges(isScheduled)
      : this.resolveInternationalLandingCharges(isScheduled);
  }

  /**
   * Resolve parking charge code by system configuration setting.
   */
  private resolveParkingCharges(): string {
    return this.systemConfigurationService.getValueByName(<any> CaabSageSysConfigConstants.PARKING_CHARGES_CODE);
  }

  /**
   * Resolve passenger charge code by system configuration setting.
   *
   * @param isDomestic true if domestic passenger charge
   */
  private resolvePassengerCharges(isDomestic: boolean): string {

    let config: CaabSageSysConfigConstants = isDomestic
      ? CaabSageSysConfigConstants.DOMESTIC_PASSENGER_SERVICE_CHARGES_CODE
      : CaabSageSysConfigConstants.INTERNATIONAL_PASSENGER_SERVICE_CHARGES_CODE;

    return this.systemConfigurationService.getValueByName(<any> config);
  }
}
