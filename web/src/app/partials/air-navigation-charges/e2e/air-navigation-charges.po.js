/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var AirNavigationCharge = function () {

  this.page = element(by.css('div[ng-class="main.air-navigation-charges"]'));
  this.formEl = this.page.element(by.css('form'));

  let fields = [
      'aerodrome_category_name',
      'charges_type',
      'aerodrome_category_id',
      'new_charges_type',
      'new_aerodromecategory_name',
      'new_aerodromecategory_id'
  ];
  this.formFields = [];

  for (let field = 0; field < fields.length; field++) {
    this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
  };

  //Form
  this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
  this.createButtonEl = this.formEl.element(by.css('.btn-create'));
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
  this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));

  //Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new AirNavigationCharge();
