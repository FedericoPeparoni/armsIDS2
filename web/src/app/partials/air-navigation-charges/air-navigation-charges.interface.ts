import { IAerodromeCategory } from '../aerodrome-category-management/aerodrome-category-management.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IAirNavigationChargeType {
  id?: number;
  aerodrome_category_name: string;
  charges_type: string;
  aerodrome_category_id: number;
  document_filename: string;
}

export interface IAirNavigationChargeScope {
  chargeTypes: Array<IStaticType>;
  aerodromeCategoryList: Array<IAerodromeCategory>;
  shouldShowCharge: Function;
  refreshOverride: Function;
  categories: any;
  pattern: string;
  filterParameters: object;
  textFilter: string;
  pagination: ISpringPageableParams;
  getSortQueryString: () => string;
}
