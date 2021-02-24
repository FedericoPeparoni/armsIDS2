/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var AviationBillingEngine = function () {

  this.page = element(by.css('div[ng-class="main.aviation-billing-engine"]'));

  //Dropdown models
  this.month = element(by.model('selectedMonth'));
  this.year = element(by.model('dateObject'));
  this.invoice = element(by.model('accountType'));
};

module.exports = new AviationBillingEngine();
