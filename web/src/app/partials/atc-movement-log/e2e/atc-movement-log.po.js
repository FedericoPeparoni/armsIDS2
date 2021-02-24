/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

var AtcMovementLog = function() {

    this.page = element(by.css('div[ng-class="main.atc-movement-log"]'));
    this.formEl = this.page.element(by.css('form'));

    let fields = [
      'date_of_contact',
      'registration',
      'operator_identifier',
      'route',
      'flight_id',
      'aircraft_type',
      'departure_aerodrome',
      'destination_aerodrome',
      'fir_entry_point',
      'fir_entry_time',
      'fir_mid_point',
      'fir_mid_time',
      'fir_exit_point',
      'fir_exit_time',
      'flight_level',
      'wake_turbulence',
      'flight_category',
      'flight_type',
      'day_of_flight',
      'departure_time'
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

module.exports = new AtcMovementLog();
