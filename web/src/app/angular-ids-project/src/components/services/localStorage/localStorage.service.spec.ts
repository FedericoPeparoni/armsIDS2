import { LocalStorageService } from './localStorage.service';
import { ILocalStorage } from './localStorage.interface';

describe('service localStorage', () => {

  beforeEach(angular.mock.module('armsWeb'));

  it('should be registered', inject(() => {
    expect(LocalStorageService).not.toEqual(null);
  }));

  it('should be able to set a key with a value of a string', inject(() => {
    LocalStorageService.set('test', 'works');
    expect(LocalStorageService.get('test')).toEqual('works');
  }));

  it('should be able to set a key with a value of a integer, which will not be converted into a string', inject(() => {
    LocalStorageService.set('test', 3);
    expect(LocalStorageService.get('test')).toEqual(3);
  }));

  it('should be able to set a key with a value of a float, which will not be converted into a string', inject(() => {
    LocalStorageService.set('test', 3.14);
    expect(LocalStorageService.get('test')).toEqual(3.14);
  }));

  it('should be able to set a key with an array of different types', inject(() => {

    let arr = [5, 10.3, 'test', 50];

    LocalStorageService.set('testArr', arr);
    let l = LocalStorageService.get('testArr');

    for (let i = 0; i < l.length; i++) {
      expect(l[i]).toEqual(arr[i]); // matches the keys
    }
  }));

  it('should be able to set a key with an object', inject(() => {

    let obj: ILocalStorage = {
      str: 'works',
      num: 5
    };

    LocalStorageService.set('testArr', obj);
    let l = LocalStorageService.get('testArr');

    for (var i in obj) {
      if (obj.hasOwnProperty(i)) {
        expect(obj[i]).toEqual(l[i]);
      }
    }
  }));

  it('should be able to delete a key and that key will then equal null', inject(() => {
    LocalStorageService.set('test', 'works');
    expect(LocalStorageService.get('test')).toEqual('works');

    LocalStorageService.delete('test');
    expect(LocalStorageService.get('test')).toBe(null);
  }));

  it('getting a key that does not exist will return null', inject(() => {
    LocalStorageService.delete('test');
    expect(LocalStorageService.get('test')).toBe(null);
  }));

  it('should be able to be reset/destroyed', inject(() => {
    LocalStorageService.set('test', 'works');
    LocalStorageService.destroy([]);
    expect(LocalStorageService.get('test')).toBe(null);
  }));
});
