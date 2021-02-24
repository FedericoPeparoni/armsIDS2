/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var AirspaceManagement = function () {

  this.page = element(by.css('div[ng-class="main.airspace-management"]'));

  //Buttons
  this.resetButtonEl = element(by.buttonText('Reset Form'));
  this.addButtonEl = element(by.buttonText('Add Airspace'));
  this.removeButtonEl = element(by.buttonText('Remove Airspace'));

  //Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new AirspaceManagement();