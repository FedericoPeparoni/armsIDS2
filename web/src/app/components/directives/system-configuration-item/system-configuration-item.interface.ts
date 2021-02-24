// interfaces
import { IMultiSelectObj } from '../../../angular-ids-project/src/helpers/interfaces/multiselect.interface';
import { ISystemConfiguration } from '../../../partials/system-configuration/system-configuration.interface';
import { IError } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface ISystemConfigurationItemScope extends angular.IScope {
    CDPModel: Array<IMultiSelectObj>;
    CDPRange: Array<IMultiSelectObj>;
    CDP: Array<string>;
    crossing: ISystemConfiguration;
    item: ISystemConfiguration;
    verifyInProgress: boolean;
    verifyResult: boolean;
    validationError: IError;
    addCDPToList(): void;
    itemValidation(item: ISystemConfiguration): void;
}
