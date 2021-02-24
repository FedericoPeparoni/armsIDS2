'use strict';

describe('accounts view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/accounts');
        page = require('./accounts.po');
        mock(['accounts/e2e/accounts.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['name'].sendKeys('Air Canada35');
            page.formFields['alias'].sendKeys('AC');
            page.formFields['aviation_billing_contact_person_name'].sendKeys('Reg Dyer');
            page.formFields['aviation_billing_phone_number'].sendKeys('1234567890');
            page.formFields['aviation_billing_mailing_address'].sendKeys('555 Main Str, Ottawa, ON, E76 0W8, Canada');
            page.formFields['aviation_billing_email_address'].sendKeys('john@test.com');
            page.formFields['aviation_billing_sms_number'].sendKeys('1234567890');
            page.formFields['non_aviation_billing_contact_person_name'].sendKeys('Mona Cat');
            page.formFields['non_aviation_billing_phone_number'].sendKeys('1234554321');
            page.formFields['non_aviation_billing_mailing_address'].sendKeys('456 Carling Ave, Ottawa, ON, K2A 7T5, Canada');
            page.formFields['non_aviation_billing_email_address'].sendKeys('mona@test.com');
            page.formFields['non_aviation_billing_sms_number'].sendKeys('1234554321');
            page.formFields['self_care_portal_user_name'].sendKeys('test');
            page.formFields['self_care_portal_password'].sendKeys('test');
            page.formFields['iata_code'].sendKeys('K1');
            page.formFields['icao_code'].sendKeys('I45');
            page.formFields['opr_identifier'].sendKeys('test');
            page.formFields['payment_terms'].sendKeys('5');
            page.formFields['discount_structure'].sendKeys('6');
            page.formFields['tax_profile'].sendKeys('test');
            page.formFields['percentage_of_passenger_fee_payable'].sendKeys('20');
            page.formFields['invoice_delivery_format'].sendKeys('test');
            // page.formFields['invoice_delivery_method'].sendKeys('train');

            element(by.id("invoice-delivery-method")).click();
            browser.actions().sendKeys("Paper").perform();

            element(by.id("invoice-currency")).click();
            browser.actions().sendKeys("TT").perform();

            page.formFields['monthly_overdue_penalty_rate'].sendKeys('2');
            page.formFields['notes'].sendKeys('test');
            page.formFields['black_listed_override'].sendKeys('true');
            page.formFields['credit_limit'].sendKeys('5');
            page.formFields['aircraft_parking_exemption'].sendKeys('3');

            element(by.id("account-type")).click();
            browser.actions().sendKeys("Test").perform();

            page.formFields['iata_member'].sendKeys('true');
            page.formFields['separate_pax_invoice'].sendKeys('true');
            page.formFields['external_accounting_system_identifier'].sendKeys('test');
            page.formFields['active'].sendKeys('true');
        }

        function popup() {
            let EC = protractor.ExpectedConditions;
            let confirmButton = element(by.buttonText('Confirm'));
            browser.wait(EC.visibilityOf(confirmButton), 5000);
            confirmButton.click();
        }

        // RESET Button and Form Fields
        it('should clear fields when the reset button is clicked', () => {
            fillOutForm();

            page.formFields['name'].getAttribute('value').then((value) => {
                expect(value).toBe('Air Canada35');
            });
            page.formFields['alias'].getAttribute('value').then((value) => {
                expect(value).toBe('AC');
            });
            page.formFields['aviation_billing_contact_person_name'].getAttribute('value').then((value) => {
                expect(value).toBe('Reg Dyer');
            });
            page.formFields['aviation_billing_phone_number'].getAttribute('value').then((value) => {
                expect(value).toBe('1234567890');
            });
            page.formFields['aviation_billing_mailing_address'].getAttribute('value').then((value) => {
                expect(value).toBe('555 Main Str, Ottawa, ON, E76 0W8, Canada');
            });
            page.formFields['aviation_billing_email_address'].getAttribute('value').then((value) => {
                expect(value).toBe('john@test.com');
            });
            page.formFields['aviation_billing_sms_number'].getAttribute('value').then((value) => {
                expect(value).toBe('1234567890');
            });
            page.formFields['non_aviation_billing_contact_person_name'].getAttribute('value').then((value) => {
                expect(value).toBe('Mona Cat');
            });
            page.formFields['non_aviation_billing_phone_number'].getAttribute('value').then((value) => {
                expect(value).toBe('1234554321');
            });
            page.formFields['non_aviation_billing_mailing_address'].getAttribute('value').then((value) => {
                expect(value).toBe('456 Carling Ave, Ottawa, ON, K2A 7T5, Canada');
            });
            page.formFields['non_aviation_billing_email_address'].getAttribute('value').then((value) => {
                expect(value).toBe('mona@test.com');
            });
            page.formFields['non_aviation_billing_sms_number'].getAttribute('value').then((value) => {
                expect(value).toBe('1234554321');
            });
            page.formFields['self_care_portal_user_name'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });
            page.formFields['self_care_portal_password'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });
            page.formFields['iata_code'].getAttribute('value').then((value) => {
                expect(value).toBe('K1');
            });
            page.formFields['icao_code'].getAttribute('value').then((value) => {
                expect(value).toBe('I45');
            });
            page.formFields['opr_identifier'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });
            page.formFields['payment_terms'].getAttribute('value').then((value) => {
                expect(value).toBe('5');
            });
            page.formFields['discount_structure'].getAttribute('value').then((value) => {
                expect(value).toBe('6');
            });
            page.formFields['list_of_events_accounts_notified'].getAttribute('value').then((value) => {
                expect(value).toBe('? undefined:undefined ?');
            });
            page.formFields['tax_profile'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });
            page.formFields['percentage_of_passenger_fee_payable'].getAttribute('value').then((value) => {
                expect(value).toBe('20');
            });
            page.formFields['invoice_delivery_format'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });
            page.formFields['invoice_delivery_method'].getAttribute('value').then((value) => {
                expect(value).toBe('paper');
            });
            page.formFields['invoice_currency'].getText().then((value) => {
                expect(value).toBe('Test (TT)');
            });
            page.formFields['monthly_overdue_penalty_rate'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });
            page.formFields['notes'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });

            page.formFields['black_listed_override'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['credit_limit'].getAttribute('value').then((value) => {
                expect(value).toBe('5');
            });
            page.formFields['aircraft_parking_exemption'].getAttribute('value').then((value) => {
                expect(value).toBe('3');
            });
            page.formFields['account-type'].getText().then((value) => {
                expect(value).toBe('Test');
            });
            page.formFields['iata_member'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['separate_pax_invoice'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['external_accounting_system_identifier'].getAttribute('value').then((value) => {
                expect(value).toBe('test');
            });
            page.formFields['active'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });

            page.resetButtonEl.click();

            for (let field = 0; field < page.formFields.length; fields++) {
                page.formFields[page.formFields[field]].getAttribute('value').then((value) => {
                    expect(value).toBe('');
                });
            }
        });

        // CREATE Button
        describe('create', () => {
            it('shouldn\'t be possible to click when the form is empty', () => {
                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeTruthy();
                });
            });

            it('should be enabled when all fields are filled out', () => {
                fillOutForm();
                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should create an account when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an account is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an account is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an account is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when an account is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['name'].clear().then(() => {
                        page.formFields['name'].sendKeys('WestJet');
                    });

                    page.formFields['name'].getAttribute('value').then((value) => {
                        expect(value).toBe('WestJet');
                    });

                    page.formFields['name'].clear().then(() => {
                        page.formFields['name'].sendKeys('Air Canada3');
                    });

                    page.updateButtonEl.click();

                    popup();
                    browser.waitForAngular();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    page.formFields['name'].getAttribute('value').then((value) => {
                        expect(value).toBe('Air Canada3');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an account is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if an account is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.deleteButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be removed upon clicking delete', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.deleteButtonEl.click().then((value) => {
                        popup();
                        let name = page.formFields.name.getAttribute('value');
                        name.then((val) => { expect(val).toBe(''); });
                    });
                });
            });
        });

        // TABLE Filter
        describe('filter', () => {
            it('should filter all from table if a letter is an input', () => {
                page.textFilter.sendKeys('zzzz');

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });

            it('should filter tables if account type "Active" is selected', () => {

                element(by.cssContainingText('option', 'Active')).click();
                browser.waitForAngular();

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeDefined();
                });
            });

            it('should filter tables if account type "Not Active" is selected', () => {

                element(by.cssContainingText('option', 'Not Active')).click();
                browser.waitForAngular();

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });
        });

    }); //End suite

}); //End view
