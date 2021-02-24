/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC_H_tPo2vwU06JhL8w9_XCF9oehXzAQ
 */

'use strict';

var FlightMovementManagement = function () {

  this.page = element(by.css('div[ng-class="main.flight-movement-management"]'));
  this.formEl = this.page.element(by.css('form'));

  let flightInputs = [
    'flight_id',
    'item18_reg_num',
    'date_of_flight',
    'dep_time',
    'dep_ad',
    'dest_ad',
    'flight_type',
    'flight_rules',
    'item18_status',
    'item18_rmk',
    'item18_dep',
    'item18_dest',
    'item18_aircraft_type',
    'fpl_route',
    'arrival_ad',
    'arrival_time',
    'user_crossing_distance',
    'parking_time',
    'passengers_chargeable_intern',
    'passengers_chargeable_domestic',
    'tasp_charge',
    'passengers_child',
    'elapsed_time',
    'cruising_speed_or_mach_number'
  ]

  this.flightFields = [];

  for (let field = 0; field < flightInputs.length; field++) {
    this.flightFields[flightInputs[field]] = this.formEl.element(by.model(`editable.${flightInputs[field]}`));
  }

  // dynamic select
  this.account = element.all(by.options('item.id as item.name for item in list.content'));
  this.aircraftType = element.all(by.options('item.aircraft_type as item.aircraft_type for item in list.content'));

  // Checkbox
  this.checkedFlightMovement = this.page.element(by.css('input[id="checkbox_2"]'));
  this.checkedIncompleteFlightMovement = this.page.element(by.css('input[id="checkbox_3"]'));

  // Filter
  this.incompleteReasonFilter = this.page.element(by.css('select[id="incomplete-flight-movement-reason"]'));

  // Form
  this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
  this.createButtonEl = this.formEl.element(by.css('.btn-create'));
  this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
  this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));
  this.reconcileButtonEl = this.page.element(by.css('.btn-reconcile'));
  this.recalculateButtonEl = this.page.element(by.css('.btn-recalculate'));
  this.invoiceButtonEl = this.page.element(by.css('.btn-invoice'));
  this.fplButtonEl = this.page.element(by.css('.btn-fpl'));

  // Table
  this.textFilter = element(by.model('textFilter'));
};

module.exports = new FlightMovementManagement();
