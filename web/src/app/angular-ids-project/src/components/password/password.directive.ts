// interfaces
import { IPasswordScope, IPasswordValidation } from './password.interface';

// services
import { SystemConfigurationService } from '../../../../partials/system-configuration/service/system-configuration.service';

// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';

/** @ngInject */
export function password(): angular.IDirective {

  return {
    restrict: 'E',
    controller: PasswordController,
    controllerAs: 'PasswordController',
    templateUrl: 'app/angular-ids-project/src/components/password/password.html',
    replace: true,
    scope: {
      password: '=',
      required: '=',
      resetPass: '=',
      error: '=',
      name1: '@?',
      name2: '@?'
    },
    require: '^form',
    link: (scope: IPasswordScope, elem: any, attrs: any, ngModelCtrl: any): void => scope.modelCtrl = ngModelCtrl
  };

}

/** @ngInject */
export class PasswordController {

  public constructor(private $scope: IPasswordScope, private systemConfigurationService: SystemConfigurationService) {

    // set labels for password inputs. Default id 'Password *' and 'Repeat Password *'
    $scope.name1 = !$scope.name1 ? 'Password *' : $scope.name1;
    $scope.name2 = !$scope.name2 ? 'Repeat Password *' : $scope.name2;

    // set password min length value in scope
    $scope.minLength = parseInt(<string>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.PASSWORD_MINIMUM_LENGTH), 10);

    // methods used to check passwords on change and blur
    $scope.checkPasswords = (password1: string, password2: string) => this.checkPasswords(password1, password2);

    // clears the form fields when a new entry is wanted to be changed or updating an entry
    $scope.$watchGroup(['required', 'resetPass'], () => this.resetPass());
  }

  /**
   * Reset password fields if main password empty, else validate.
   *
   * Zero length passwords are handled using a toggle button in
   * the user view and thus password field validation should be
   * reset when an empty string.
   *
   * @param password1 main password value
   * @param password2 validation password value
   */
  private checkPasswords(password1: string, password2: string): void {

    // if main password value is empty, assume "clear"
    // else validate passwords
    if (!password1 && !this.$scope.required) {

      // sets password to undefined
      delete (this.$scope.password);

      // cleanup password fields and validation flags/messages
      this.resetPass();

    } else {
      this.validatePasswords(password1, password2);
    }
  }

  /**
   * Check if password values are valid using system configuration settings.
   *
   * @param password1 main password value
   * @param password2 validation password value
   */
  private getPasswordValidation(password1: string, password2: string): IPasswordValidation {
    const lowercase = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.PASSWORD_LOWERCASE);
    const minLength = parseInt(<string>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.PASSWORD_MINIMUM_LENGTH), 10);
    const numeric = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.PASSWORD_NUMERIC);
    const special = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.PASSWORD_SPECIAL);
    const uppercase = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.PASSWORD_UPPERCASE);

    if ((!password1 || !password2) && !this.$scope.required) {
      return null;
    }

    const validation: IPasswordValidation = {
      required: true,
      minlength: true,
      confirm: true,
      lowercase: true,
      uppercase: true,
      numeric: true,
      special: true
    };

    if (!password1) {
      validation.required = false;
    }

    if (password1 && password1.length < minLength) {
      validation.minlength = false;
    }

    if (password1 !== password2) {
      validation.confirm = false;
    }

    if (lowercase && password1 && !password1.match(/[a-z]/)) {
      validation.lowercase = false;
    }

    if (uppercase && password1 && !password1.match(/[A-Z]/)) {
      validation.uppercase = false;
    }

    if (numeric && password1 && !password1.match(/[0-9]/)) {
      validation.numeric = false;
    }

    if (special && password1 && !password1.match(/[\s!"#$%&'\(\)\*\+,\-\.\/:;\<\=\>\?@\[\\\]\^\_\`\{\|\}\~]/)) {
      validation.special = false;
    }

    return validation;
  }

  /**
   * Return true if password validation is valid.
   *
   * @param passwordValidation password validation to check
   */
  private isPasswordValid(passwordValidation: IPasswordValidation): boolean {
    // loop through each validation key and return false if invalid
    for (const key of Object.keys(passwordValidation)) {
      if (!passwordValidation[key]) {
        return false;
      }
    }

    // else return true inidcating password validation is valid
    return true;
  }

  /**
   * Resets password values and form validation flags.
   */
  private resetPass(): void {
    // reset password
    this.$scope.password1 = null;
    this.$scope.password2 = null;

    // clean validation flags and messages
    this.$scope.modelCtrl.$setUntouched();
    this.setValidation();
  }

  /**
   * Use to validate password values with the following logic.
   *
   * If the password is valid, clears validation flags/messages and updates
   * directive two-way password value.
   *
   * Else if the password is invalid, display first validation flag/message and
   * set two-way password value to null.
   *
   * @param password1 main password value
   * @param password2 validation password value
   */
  private validatePasswords(password1: string, password2: string): void {
    const passwordValidation: IPasswordValidation = this.getPasswordValidation(password1, password2);
    this.setValidation(passwordValidation);

    // if valid, set linked password or set to null*/
    const p = passwordValidation && this.isPasswordValid(passwordValidation) ? password1 : null;

    if (p !== null) {
      this.$scope.password = p;
    } else if (this.$scope.password !== null || this.$scope.password === '') {
      this.$scope.password = null;
    }
  }

  /**
   * Clear existing validation and set invalid key if passed.
   *
   * @param invalidKey invalid pwcheck key
   */
  private setValidation(passwordValidation?: IPasswordValidation): void {
    if (!passwordValidation) {
      passwordValidation = {
        required: true,
        minlength: true,
        confirm: true,
        lowercase: true,
        uppercase: true,
        numeric: true,
        special: true
      };
    }

    for (const key of Object.keys(passwordValidation)) {
      if (key === 'confirm') {
        this.$scope.modelCtrl.passwordConfirm.$setValidity('pwcheck-' + key, passwordValidation[key]);
      } else {
         this.$scope.modelCtrl.password.$setValidity('pwcheck-' + key, passwordValidation[key]);
      };
    }
  }

}
