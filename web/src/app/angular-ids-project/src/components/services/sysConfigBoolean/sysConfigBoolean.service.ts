export class SysConfigBoolean {

  /** @ngInject */

  // returns Boolean true or false for system configs in the form 'f' or 't' so that
  // the boolean evaluation can be used instead of testing against a string
  public parse(val: any): boolean {
    return !/^(?:f(?:alse)?|no?|0+)$/i.test(val) && !!val;
  };

}
