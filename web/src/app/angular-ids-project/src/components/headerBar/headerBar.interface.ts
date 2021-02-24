import { IUser } from '../../../../partials/users/users.interface';
import { ILanguageObject } from '../../../../partials/languages/languages.interface';

export interface IHeaderBarScope extends angular.IScope {
  toggleSideBar: Function;
  user: IUser;
  onButtonGroupClick($event: any);
  changeLanguage(lang: any);
  changeMode(mode: string);
  languages: Array<ILanguageObject>;
}
