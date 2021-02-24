// interfaces
import { IAdjustmentCharge, IChargeTypeScope } from '../../transactions/transactions.interface';
import { IFlightMovement } from '../../flight-movement-management/flight-movement-management.interface';

// service
import { CaabSageChargeAdjustmentsService } from '../../../plugins/caab/sage/partials/charge-adjustments/service/charge-adjustments.caab.sage.service';
import { SysConfigBoolean } from '../../../angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';
import { SystemConfigurationService } from '../../system-configuration/service/system-configuration.service';

// constants
import { OrganizationName } from '../../organization/organization.constants';
import { SysConfigConstants } from '../../system-configuration/system-configuration.constants';

export enum ChargesAdjustmentType {
  ALL_CHARGES = <any> 'All charges',
  ENROUTE_CHARGES = <any> 'Enroute charges',
  LANDING_CHARGES = <any> 'Landing charges',
  OTHER_CHARGES = <any> 'Other charges',
  PARKING_CHARGES = <any> 'Parking charges',
  PASSENGER_CHARGES = <any> 'Passenger charges',
  TASP_CHARGES = <any> 'TASP charges'
}

export class ChargeAdjustmentsService {

  /** @ngInject */
  constructor(protected sysConfigBoolean: SysConfigBoolean, protected systemConfigurationService: SystemConfigurationService,
    private caabSageChargeAdjustmentsService: CaabSageChargeAdjustmentsService) {}

   /**
    * Retreive iata aviation charges based on system configuration settings.
    */
  public iataChargeTypes(): Array<IChargeTypeScope> {
    return [
      { description: this.getChargesAdjustment(ChargesAdjustmentType.ENROUTE_CHARGES) },
      { description: this.getChargesAdjustment(ChargesAdjustmentType.OTHER_CHARGES) },
      { description: this.getChargesAdjustment(ChargesAdjustmentType.ALL_CHARGES) }
    ];
  }

  /**
   * Retreive non iata aviation charges based on system configuration settings.
   */
  public nonIataChargeTypes(): Array<IChargeTypeScope> {
    const charges = [
      { description: this.getChargesAdjustment(ChargesAdjustmentType.ENROUTE_CHARGES) },
      { description: this.getChargesAdjustment(ChargesAdjustmentType.LANDING_CHARGES) },
      { description: this.getChargesAdjustment(ChargesAdjustmentType.OTHER_CHARGES) },
      { description: this.getChargesAdjustment(ChargesAdjustmentType.ALL_CHARGES) }
    ];

    if (this.getChargesAdjustmentSupport(ChargesAdjustmentType.TASP_CHARGES)) {
      charges.splice(2, 0, { description: this.getChargesAdjustment(ChargesAdjustmentType.TASP_CHARGES) });
    }

    if (this.getChargesAdjustmentSupport(ChargesAdjustmentType.PASSENGER_CHARGES)) {
      charges.splice(2, 0, { description: this.getChargesAdjustment(ChargesAdjustmentType.PASSENGER_CHARGES) });
    }

    if (this.getChargesAdjustmentSupport(ChargesAdjustmentType.PARKING_CHARGES)) {
      charges.splice(2, 0, { description: this.getChargesAdjustment(ChargesAdjustmentType.PARKING_CHARGES) });
    }

    return charges;
  }

  /**
   * Resolve flight adjustment external properties based on organization system settings.
   * 
   * @param charge charge adjustment details
   * @param flight flight movement being adjusted
   */
  public resolveFlightAdjustmentExternal(charge: IAdjustmentCharge, flight: IFlightMovement, invoiceType: string): void {

    // only resolve if flight is not null
    if (!charge || !flight) {
      return;
    }

    // retrieve organization to resolve by different customizations
    const organization = this.systemConfigurationService.getValueByName(<any> SysConfigConstants.ORGANIZATION);

    // perform necessary organization specific logic, no default implementation
    if (OrganizationName.CAAB === organization) {
      this.caabSageChargeAdjustmentsService.resolveFlightAdjustementExternal(charge, flight, invoiceType);
    }
  }

  /**
   * Get charge adjustment type value.
   *
   * @param type charge adjustment type
   */
  private getChargesAdjustment(type: ChargesAdjustmentType): string {

    // override tasp charge value based on system configuration setting
    if (type === ChargesAdjustmentType.TASP_CHARGES) {
      return `${this.systemConfigurationService.getValueByName(<any> SysConfigConstants.TASP_FEES_LABEL)} charges`;
    } else if (type === ChargesAdjustmentType.LANDING_CHARGES) {
      return `${this.systemConfigurationService.getValueByName(<any> SysConfigConstants.APPROACH_FEES_LABEL)} charges`;
    } else {
      return <any> type;
    }
  }

  /**
   * Get charge adjustment type support based on system configuration settings.
   *
   * @param type charge adjustment type
   */
  private getChargesAdjustmentSupport(type: ChargesAdjustmentType): boolean {
    let support: boolean;
    switch (type) {
      case ChargesAdjustmentType.PARKING_CHARGES:
        support = this.sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any> SysConfigConstants.CALCULATE_PARKING_CHARGES));
        break;
      case ChargesAdjustmentType.PASSENGER_CHARGES:
        support = this.sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any> SysConfigConstants.PASSENGER_CHARGES_SUPPORT));
        break;
      case ChargesAdjustmentType.TASP_CHARGES:
        support = this.sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any> SysConfigConstants.CALCULATE_TASP_CHARGES));
        break;
      default:
        support = true;
    }
    return support;
  }
}
