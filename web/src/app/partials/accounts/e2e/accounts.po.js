/**
 * This file uses the Page Object pattern to define the main page for tests
 * https://docs.google.com/presentation/d/1B6manhG0zEXkC-H-tPo2vwU06JhL8w9-XCF9oehXzAQ
 */

'use strict';

let Account = function() {

    this.page = element(by.css('div[ng-class="main.accounts"]'));
    this.formEl = this.page.element(by.css('form'));

    let fields = [
        'name',
        'alias',
        'aviation_billing_contact_person_name',
        'aviation_billing_phone_number',
        'aviation_billing_mailing_address',
        'aviation_billing_email_address',
        'aviation_billing_sms_number',
        'non_aviation_billing_contact_person_name',
        'non_aviation_billing_phone_number',
        'non_aviation_billing_mailing_address',
        'non_aviation_billing_email_address',
        'non_aviation_billing_sms_number',
        'self_care_portal_user_name',
        'self_care_portal_password',
        'iata_code',
        'icao_code',
        'opr_identifier',
        'payment_terms',
        'discount_structure',
        'tax_profile',
        'percentage_of_passenger_fee_payable',
        'invoice_delivery_format',
        'invoice_delivery_method',
        'invoice_currency',
        'monthly_overdue_penalty_rate',
        'notes',
        'black_listed_indicator',
        'black_listed_override',
        'credit_limit',
        'aircraft_parking_exemption',
        'type',
        'list_of_events_accounts_notified',
        'iata_member',
        'separate_pax_invoice',
        'external_accounting_system_identifier',
        'active'
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

module.exports = new Account();
