/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let RejectedItems = function () {

  this.page = element(by.css('div[ng-class="main.rejected-items"]'));
  this.formEl = this.page.element(by.css('form'));

  //Form
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
  this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));

  //Table
  this.textFilter = element(by.model('textFilter'));

};

module.exports = new RejectedItems();

