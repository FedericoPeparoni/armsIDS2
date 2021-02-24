import { IDateObject } from '../../helpers/interfaces/dateObject.interface';

export interface IFooter extends ng.IScope {
  serverTime: IDateObject;
}
