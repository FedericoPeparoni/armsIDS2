/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let RadarSummary = function() {

    this.page = element(by.css('div[ng-class="main.radar-summary"]'));
    this.formEl = this.page.element(by.css('form'));

    let fields = [
        'date',
        'flight_identifier',
        'day_of_flight',
        'departure_time',
        'registration',
        'aircraft_type',
        'departure_aero_drome',
        'destination_aero_drome',
        'route',
        'fir_entry_point',
        'fir_entry_time',
        'fir_exit_point',
        'fir_exit_time',
        'flight_rule',
        'flight_travel_category'
    ];

    this.formFields = [];

    for (let field = 0; field < fields.length; field++) {
        this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
    };

    this.destAerodrome = element.all(by.options('a.aerodrome_name as a.aerodrome_name for a in list.content'));
    this.aircraftType = element.all(by.options('item.aircraft_type as item.aircraft_type for item in list.content'));
    this.depAerodrome = element.all(by.options('item.aerodrome_name as item.aerodrome_name for item in list.content'));

    //Form
    this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
    this.createButtonEl = this.formEl.element(by.css('.btn-create'));
    this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
    this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));

    //Table
    this.textFilter = element(by.model('textFilter'));
};

module.exports = new RadarSummary();
