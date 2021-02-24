/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var exemption = function () {

  this.page = element(by.css('div[ng-class="main.flight-route-exemptions"]'));
  this.formEl = this.page.element(by.css('form'));

  let fields = [
    departure_aerodrome_id,
    destination_aerodrome_id,
    exemption_in_either_direction,
    enroute_fees_are_exempt,
    approach_fees_are_exempt,
    aerodrome_fees_are_exempt,
    late_arrival_fees_are_exempt,
    late_departure_fees_are_exempt,
    parking_fees_are_exempt,
    adult_passenger_fees_are_exempt,
    child_passenger_fees_are_exempt,
    flight_notes
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

module.exports = new exemption();
