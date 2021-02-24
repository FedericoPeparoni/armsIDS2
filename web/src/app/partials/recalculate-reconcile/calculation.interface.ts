import { IError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface ICalculation {
    checkStatusOnPageLoad: Function;
    executeCalculation: Function;
    cancelCalculation: Function;
}