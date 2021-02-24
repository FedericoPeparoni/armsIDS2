import { IHasOwnProperty } from '../../../helpers/interfaces/hasOwnProperty.interface';

export interface ILocalStorage extends IHasOwnProperty {
  str: string;
  num: number;
}
