// interface
import { ICatalogueServiceChargeType } from '../catalogue-service-charge.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'service-charge-catalogues';

export class CatalogueServiceChargeService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ICatalogueServiceChargeType = {
    id: null,
    charge_class: null,
    category: null,
    type: null,
    subtype: null,
    description: null,
    charge_basis: null,
    minimum_amount: null,
    maximum_amount: null,
    amount: null,
    invoice_category: null,
    external_accounting_system_identifier: null,
    external_charge_category: null,
    currency: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getBasisList(): Array<IStaticType> {
    return [
      {
        name: 'Fixed',
        value: 'fixed'
      },
      {
        name: 'Price per Unit',
        value: 'unit'
      },
      {
        name: 'Percentage',
        value: 'percentage'
      },
      {
        name: 'User Entered Price',
        value: 'user'
      },
      {
        name: 'Water',
        value: 'water'
      },
      {
        name: 'Residential Electric',
        value: 'residential-electric'
      },
      {
        name: 'Commercial Electric',
        value: 'commercial-electric'
      },
      {
        name: 'Discount',
        value: 'discount'
      },
      {
        name: 'External Database',
        value: 'external-database'
      }
    ];
  }

  public listCategories(): Array<IStaticType> {
    return [
      {
        name: 'IATA-AVI',
        value: 'iata-avi'
      },
      {
        name: 'Non-IATA-AVI',
        value: 'non-iata-avi'
      },
      {
        name: 'Cash AVI',
        value: 'cash-avi'
      },
      {
        name: 'Non-AVI',
        value: 'non-avi'
      },
      {
        name: 'Utility',
        value: 'utility'
      },
      {
        name: 'Lease',
        value: 'lease'
      },
      {
        name: 'Concession',
        value: 'concession'
      },
      {
        name: 'Point of Sale',
        value: 'pos'
      },
      {
        name: 'Receipt',
        value: 'receipt'
      }
    ];
  }

}
