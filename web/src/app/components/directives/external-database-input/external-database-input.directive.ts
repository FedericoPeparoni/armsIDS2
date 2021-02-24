/**
 * Input for querying external database.
 * 
 * Accepts an input value and a target value
 *
 * Accepts params as follows:
 *
 * 
 * <external-database-input
 *   input-value="inputValue"
 *   return-value="returnValue"
 * />
 *
 */

const EXTERNAL_DATABASE_TARGETS = {
  AATIS: {
    name: 'AATIS',
    error: {
      notFound: 'Adhoc permit not found',
      error: 'There was an error connecting to the AATIS database'
    }
  },
  EAIP: {
    name: 'EAIP',
    error: {
      notFound: 'Requisition not found',
      error: 'There was an error connecting to the eAIP database'
    }
  }
 };

// services
import { ExternalDatabaseInputService } from  './external-database-input.service';

// interfaces
import { IExtendableError, IError } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IFlightMovement } from '../../../partials/flight-movement-management/flight-movement-management.interface';

interface IExternalDatabaseInputScope extends ng.IScope {
  item: any;
  error: any;
  required: any;
}

export interface IAdhocFee extends ng.IScope {
  invoice_permit_number: string;
  external_database_for_charge: string;
  adhoc_total_fee_payment_amount: number;
  flight_movement: IFlightMovement;
}

/** @ngInject */
export function externalDatabaseInput(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/external-database-input/external-database-input.template.html',
    scope: {
      item: '=',
      required: '=?',
      callback: '=?',
      supported: '=?',
      externalDatabase: '@?',
      currencyCode: '=?',
      posAviation: '=?'
    },
    controller: ExternalDatabaseInputController
  };

}

/** @ngInject */
export class ExternalDatabaseInputController {

  constructor(private $scope: IExternalDatabaseInputScope, private externalDatabaseInputService: ExternalDatabaseInputService) {

    $scope.databaseTarget = this.$scope.item.service_charge_catalogue ? this.$scope.item.service_charge_catalogue.external_database_for_charge : this.$scope.externalDatabase;
    $scope.queryExternalDatabase = () => this.queryExternalDatabase();

    $scope.supported = angular.isDefined(this.$scope.supported) ? this.$scope.supported : true;

    $scope.posAviation = $scope.posAviation ? $scope.posAviation : false;

    $scope.$watch('required', (isRequired: boolean) => {
      if (!isRequired) {
        this.$scope.permitNumber = null;
      }
    });

    $scope.$watch('currencyCode', () => this.queryExternalDatabase());
  }

  private queryExternalDatabase(): void {
    this.$scope.error = null;

    switch (this.$scope.databaseTarget) {
      case EXTERNAL_DATABASE_TARGETS.AATIS.name:
      this.getInvoicPermitFromAATIS();
      break;

      case EXTERNAL_DATABASE_TARGETS.EAIP.name:
        this.getRequisitionFromEAIP();
        break;
      default:
        break;
    }
  }

  private getInvoicPermitFromAATIS(): void {

    const adhocFee = {
      invoice_permit_number: this.$scope.permitNumber,
      external_database_for_charge: EXTERNAL_DATABASE_TARGETS.AATIS.name,
      adhoc_total_fee_payment_amount: null,
      flight_movement: this.$scope.item
    };

    if (!this.$scope.permitNumber) {
      if (this.$scope.callback) {
        return this.$scope.callback(adhocFee);
      }
      return;
    }

    let { item, permitNumber, databaseTarget } = this.$scope;
    item.invoice_permit.invoice_permit_number = permitNumber;
    item.invoice_permit.external_database_for_charge = databaseTarget;

    this.externalDatabaseInputService.getInvoicePermitFromAATIS(this.$scope.permitNumber, this.$scope.currencyCode).then((invoicePermit: any) => {

      if (invoicePermit) {
        item.invoice_permit.adhoc_total_fee_payment_amount = invoicePermit.adhoc_fee;
        adhocFee.adhoc_total_fee_payment_amount = this.$scope.currencyCode ? invoicePermit.adhoc_fee_converted : invoicePermit.adhoc_fee;

        if (this.$scope.callback) {
          this.$scope.callback(adhocFee);
        } else {
          this.$scope.item.amount = adhocFee.adhoc_total_fee_payment_amount;
        }

      } else {
        this.showError(EXTERNAL_DATABASE_TARGETS.AATIS.error.notFound);
        this.updateNotFoundAdhoc(item, adhocFee);
      }

    }).catch(() => {
      this.showError(EXTERNAL_DATABASE_TARGETS.AATIS.error.error);
      this.updateNotFoundAdhoc(item, adhocFee);
    });
  }

  private getRequisitionFromEAIP(): void {

    if (!this.$scope.permitNumber) {
      return;
    }

    this.externalDatabaseInputService.getRequisitionFromEAIP(this.$scope.permitNumber).then((requisition: any) => {

      if (requisition) {

        const { item, permitNumber, databaseTarget } = this.$scope;

        const {
          req_currency,
          req_total_amount,
          req_ar_id,
          req_country_id,
          req_id,
          req_maninfo_id
        } = requisition;

        const newRequisition = {
          req_number: permitNumber,
          external_database_for_charge: databaseTarget,
          req_currency,
          req_total_amount,
          req_ar_id,
          req_country_id,
          req_id,
          req_maninfo_id
        };

        item.requisition = newRequisition;
        item.amount = req_total_amount;

      } else {
        this.showError(EXTERNAL_DATABASE_TARGETS.EAIP.error.notFound);
      }
    }).catch(() => this.showError(EXTERNAL_DATABASE_TARGETS.EAIP.error.error));
  }

  private showError(errorDescription: string): void {
    this.$scope.item.amount = null;
    this.$scope.error = <IExtendableError>{
      error: {
        data: <IError>{
          error: 'NOT FOUND',
          error_description: errorDescription,
          field_errors: null
        }
      }
    };
  }

  // we need to include the user entered permit number on the invoice (only for POS Aviation), 
  // with fees of 0.00, even if it's not found/not connected to external db
  private updateNotFoundAdhoc(item: any, adhocFee: any): void {
    if (this.$scope.permitNumber && this.$scope.posAviation && this.$scope.callback) {
      item.invoice_permit.adhoc_total_fee_payment_amount = 0;
      adhocFee.adhoc_total_fee_payment_amount = 0;
      this.$scope.callback(adhocFee);
    }
  }

}
