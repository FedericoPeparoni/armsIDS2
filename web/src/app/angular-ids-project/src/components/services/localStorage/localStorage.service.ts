import { SysConfigConstants } from '../../../../../partials/system-configuration/system-configuration.constants';

export class LocalStorageService {

  /** @ngInject */
  static isLocalStorageDefined(): boolean {
    return typeof window.localStorage !== 'undefined';
  }

  static get(key: string): any {
    let value = window.localStorage.getItem(key);
    if (this.IsJsonString(value)) {
      return angular.fromJson(JSON.parse(value)); // handles quotes added by server when data is transferred via API
    } else {
      return value;
    };
  }

  static getBooleanFromValueByName (name: string): boolean {
    return this.get(name) === <any>SysConfigConstants.SYSTEM_CONFIG_TRUE;
  }

  static set(key: string, value: any): void {
    if (typeof value === 'object') {
      window.localStorage.setItem(key, JSON.stringify(value));
    } else {
      window.localStorage.setItem(key, value);
    };
  }

  static delete(key: string): void {
    window.localStorage.removeItem(key);
  }

  static destroy(items: Array<string>): void {
    let saved = [];

    for (let i = 0, len = items.length; i < len; i++) {
      saved.push(window.localStorage.getItem(items[i]));
    };

    window.localStorage.clear();

    for (let i = 0, len = saved.length; i < len; i++) {
      window.localStorage.setItem(items[i], saved[i]);
    };
  }

  static IsJsonString(str: any): Boolean {
    try {
      JSON.parse(str);
    } catch (e) {
      return false;
    }
    return true;
  }

}
