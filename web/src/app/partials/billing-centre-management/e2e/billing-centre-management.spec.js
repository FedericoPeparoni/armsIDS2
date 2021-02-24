'use strict';

describe('billing-centre-management view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/billing-centre-management');
        page = require('./billing-centre-management.po');
        mock(['billing-centre-management/e2e/billing-centre-management.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['name'].sendKeys('Test');
            page.formFields['prefix_invoice_number'].sendKeys('T1A');
            page.formFields['invoice_sequence_number'].sendKeys('456');
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
                expect(value).toBe('Test');
            });
            page.formFields['prefix_invoice_number'].getAttribute('value').then((value) => {
                expect(value).toBe('T1A');
            });

            page.formFields['invoice_sequence_number'].getAttribute('value').then((value) => {
                expect(value).toBe('456');
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

            it('should create a billing centre when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if a billing centre is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if a billing centre is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if a billing centre is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    fillOutForm();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the billing centre is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['prefix_invoice_number'].clear().then(() => {
                        page.formFields['prefix_invoice_number'].sendKeys('T1A');
                    });

                    page.formFields['prefix_invoice_number'].getAttribute('value').then((value) => {
                        expect(value).toBe('T1A');
                    });

                    page.formFields['prefix_invoice_number'].clear().then(() => {
                        el[0].click();
                        page.formFields['prefix_invoice_number'].sendKeys('TT');

                    });

                    page.updateButtonEl.click();

                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['prefix_invoice_number'].getAttribute('value').then((value) => {
                        expect(value).toBe('TT');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if a billing centre is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if a billing centre is selected', () => {
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

                    page.deleteButtonEl.click();

                    popup();

                    let name = page.formFields.name.getAttribute('value');
                    name.then((val) => { expect(val).toBe(''); });
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
        });
    }); //End suite
}); //End view