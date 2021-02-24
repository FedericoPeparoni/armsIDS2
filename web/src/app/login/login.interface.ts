import { ILanguageObject } from '../partials/languages/languages.interface';

export interface ILoginScope extends ng.IScope {
  warning: boolean;
  error: any;
  timeoutDelay: number;
  submit(user: string, password: string): void;
  changeLanguage(lang: ILanguageObject): void;
  setupLangs(): void;
  language: ILanguageObject;
  languages: Array<ILanguageObject>;
}
