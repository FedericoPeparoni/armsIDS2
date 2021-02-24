/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let towerLog = function () {

  this.page = element(by.css('div[ng-class="main.tower-movement-logs"]'));
  this.formEl = this.page.element(by.css('form'));

  let fields = [
    'date_of_contact',
    'flight_id',
    'registration',
    'aircraft_type',
    'operator_name',
    'departure_aerodrome',
    'departure_contact_time',
    'destination_aerodrome',
    'destination_contact_time',
    'route',
    'flight_level',
    'flight_crew',
    'passengers',
    'flight_category',
    'day_of_flight',
    'departure_time'
  ];
  this.formFields = [];

  for (let field = 0; field < fields.length; field++) {
    this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
  };

  this.destAerodrome = element.all(by.options('a.id as a.aerodrome_name for a in list.content'));
  this.aircraftType = element.all(by.options('item.aircraft_type as item.aircraft_type for item in list.content'));
  this.depAerodrome = element.all(by.options('item.id as item.aerodrome_name for item in list.content'));

  //Form
  this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
  this.createButtonEl = this.formEl.element(by.css('.btn-create'));
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
  this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));

  //Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new towerLog();
