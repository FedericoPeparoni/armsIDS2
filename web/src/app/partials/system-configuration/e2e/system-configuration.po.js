/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var SystemConfiguration = function () {

  this.page = element(by.css('div[ng-class="main.system-configuration"]'));
  this.formEl = this.page.element(by.css('form'));
  this.currentValue = this.formEl.element(by.model(`item.current_value`));

  //Form
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
};

module.exports = new SystemConfiguration();