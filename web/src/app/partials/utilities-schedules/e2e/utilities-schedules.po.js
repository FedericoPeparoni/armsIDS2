/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var UtilitiesSchedules = function () {

  this.page = element(by.css('div[ng-class="main.utilities-schedules"]'));
  this.formEl = this.page.element(by.css('form'));
  this.formFields = [];

  //Form
  this.formFields['schedule_type'] = this.formEl.element(by.model(`editable.schedule_type`));
  this.formFields['minimum_charge'] = this.formEl.element(by.model(`editable.minimum_charge`));
  this.formFields['range_top_end'] = this.formEl.element(by.model(`editableRangeBrackets.range_top_end`));
  this.formFields['unit_price'] = this.formEl.element(by.model(`editableRangeBrackets.unit_price`));
  this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
  this.createButtonEl = this.formEl.element(by.css('.btn-create'));
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
  this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));
  this.createBracketsButtonEl = this.formEl.element(by.css('.btn-create-brackets'));
  this.updateBracketsButtonEl = this.formEl.element(by.css('.btn-update-brackets'));
  this.deleteBracketsButtonEl = this.formEl.element(by.css('.btn-delete-brackets'));

  //Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new UtilitiesSchedules();
