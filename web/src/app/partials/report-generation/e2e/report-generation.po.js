/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let ReportGeneration = function () {

    this.page = element(by.css('div[ng-class="main.report-generation"]'));
    this.formEl = this.page.element(by.css('form'));

    let fields = [
        'report',
        'credit_type',
        'overdue_interval',
        'invoices',
        'summary_interval',
        'used_defined',
        'used_undefined'
        // 'financial_year',
        // 'account_new_page',
    ];

    this.formFields = [];

    for (let field = 0; field < fields.length; field++) {
        this.formFields[fields[field]] = this.formEl.element(by.model(`editable.${fields[field]}`));
    };

    //Buttons
    this.cleanButtonEl = this.formEl.element(by.css('.btn-clean'));
    this.previewButtonEl = this.formEl.element(by.css('.btn-preview'));
};

module.exports = new ReportGeneration();
