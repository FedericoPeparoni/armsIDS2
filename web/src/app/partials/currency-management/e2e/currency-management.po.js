/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let CurrencyManagementPage = function () {

  this.page = element(by.css('div[ng-class="main.currency-management"]'));
  this.formEl = this.page.element(by.css('[name=form]'));
  this.formE2 = this.page.element(by.css('[name=form2]'));

  let fields = [
    'currency_code',
    'currency_name',
    'country_code',
    'decimal_places',
    'symbol',
    'active'
  ];
  this.formFields = [];

  for (let field = 0; field < fields.length; field++) {
    this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
  };

  this.formFields['exchange_rate'] = this.formE2.element(by.model('exchangeRate.exchange_rate'));

  //Dynamic Service Selector
  this.countries = element.all(by.options('country as country.country_code for country in list.content track by country.id'));

  //Form
  this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
  this.createButtonEl = this.formEl.element(by.css('.btn-create'));
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
  this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));

  //Form2
  this.createRateButtonEl = this.formE2.element(by.css('.btn-rate-create'));
  this.updateRateButtonEl = this.formE2.element(by.css('.btn-rate-update'));
  this.deleteRateButtonEl = this.formE2.element(by.css('.btn-rate-delete'));
  this.startDateE1 = element(by.model('start.date'));
  this.endDateE1 = element(by.model('end.date'));

  //Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new CurrencyManagementPage();
