/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let Transactions = function () {

  this.page = element(by.css('div[ng-class="main.transactions"]'));
  this.formEl = this.page.element(by.css('form'));

  let fields = [
      'account',
      'description',
      'transaction_type',
      'currency',
      'amount',
      'payment_mechanism',
      'payment_reference_number'
  ];

  this.formFields = [];

  for (let field = 0; field < fields.length; field++) {
    this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
  };

  //Form
  this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
  this.createButtonEl = this.formEl.element(by.css('.btn-create'));

  //dropdown
  this.account = element.all(by.options('account as account.name for account in list.content track by account.id'));
  this.transactionType = element.all(by.options('item as item.name for item in list.content track by item.id'));

  //Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new Transactions();

