/**
 *
 *
 * this is a decorator, functionality is subject to change (https://www.typescriptlang.org/docs/handbook/decorators.html)
 *
 * Inject
 * @param dependencies  Passed Services to give to the assigne controllers constructor
 * @constructor
 *
 * Purpose:
 *
 * This allows a parent class to have services that do not require to be accessible to the child class
 *
 * How to use:
 *
 * If you have a constructor that is like this
 *
 * constructor(accountsService: AccountsService)
 *
 * You can use the decorator to make the 2nd (or last) parameter an Array of other objects
 *
 * @InjectDecorator(UsersService)
 *
 * The constructor now has another parameter
 *
 * constructor(accountsService: AccountsService, ... dependencies: Array<any>) {
 *  dependencies[0] // is UsersService
 *
 */


export const Inject = (... dependencies: Array<Object>) => (t: any) => {
  var original = t;
  let newConstructor = function(...args: Array<Object>): any {
    for (let i = 0; i < dependencies.length; i++) {
      args.push(dependencies[i]);
    }
    return original.apply(this, args);
  };
  newConstructor.prototype = original.prototype;
  return <typeof undefined><any>newConstructor; // if this was not reusable `undefined` would actually be the Controller that this is decorating
};
