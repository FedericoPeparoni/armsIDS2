
import { ICRUDFormScope } from '../../angular-ids-project/src/helpers/interfaces/crud-form.interface';

export interface ITuRateManagement {

id: number,
fromManufactureYear: string,
toManufactureYear: string,
ars: number
fromValidityYear: string,
toValidityYear: string

}


export interface ITuRateManagementScope extends ICRUDFormScope<ITuRateManagement> {

}
