'use strict';

var path = require('path');

describe('invoice template management view', () => {
    let page;
    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/invoice-template-management');
        page = require('./invoice-template-management.po');
        mock(['invoice-template-management/e2e/invoice-template-management.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['invoice_template_name'].sendKeys('test');
            page.formFields['invoice_category'].sendKeys('iata-avi');

            absolutePath = path.resolve(__dirname, './Book1.csv');
            element(by.css('input[type=file]')).sendKeys(absolutePath);
            element(by.id('uploadButton')).click();
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

            page.formFields['invoice_template_name'].getAttribute('value').then((value) => {
                expect(value).toBe('6');
            });
            page.formFields['invoice_category'].getAttribute('value').then((value) => {
                expect(value).toBe('1');
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

            it('should create a invoice template when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if a invoice template is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if a invoice template is selected', () => {
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
                        let name = page.formFields.upper_limit.getAttribute('value');
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
        });

    }); //End suite

}); //End view
