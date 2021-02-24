import { IAccount } from '../accounts/accounts.interface';
import { IExternalChargeCategory } from '../external-charge-category/external-charge-category.interface';

export interface IAccountExternalChargeCategory {
    id: number;
    account: IAccount;
    external_charge_category: IExternalChargeCategory;
    external_system_identifier: string;
}
