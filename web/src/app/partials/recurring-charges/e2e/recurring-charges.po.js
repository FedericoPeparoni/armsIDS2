/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let RecurringCharges = function () {
    this.page = element(by.css('div[ng-class="main.recurring-charges"]'));
    this.formEl = this.page.element(by.css('form'));

    let fields = [
        'service_charge_catalogue',
        'account',
        'start.date',
        'end.date'
    ];
    this.formFields = [];

    for (let field = 0; field < fields.length; field++) {
        this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
    }

    this.account = element.all(by.options('item.id as item.name for item in list.content'));
    this.serviceChargeCatalogue = element.all(by.options('item.id as item.description for item in catalogueServiceChargeListFiltered'));

    //Form
    this.resetButtonEl = this.formEl.element(by.css('.btn-reset'));
    this.createButtonEl = this.formEl.element(by.css('.btn-create'));
    this.updateButtonEl = this.formEl.element(by.css('.btn-update'));
    this.deleteButtonEl = this.formEl.element(by.css('.btn-delete'));

    //Table
    this.textFilter = element(by.model('textFilter'));

};

module.exports = new RecurringCharges();
