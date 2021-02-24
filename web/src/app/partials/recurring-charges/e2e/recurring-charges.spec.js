'use strict';

describe('recurring-charges view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/recurring-charges');
        page = require('./recurring-charges.po');
        mock(['recurring-charges/e2e/recurring-charges.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            element(by.id("account")).click();
            browser.actions().sendKeys("Mock Account").perform();

            element(by.id("service-charge-catalogue")).click();
            browser.actions().sendKeys("Test").perform();

            page.page.element(by.model('start.date')).sendKeys('2017-Feb-15');
            page.page.element(by.model('end.date')).sendKeys('2017-Feb-25');
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

            page.page.element(by.id('account')).getText().then((value) => expect(value).toBe('Mock Account'));
            page.page.element(by.id('service-charge-catalogue')).getAttribute('value').then((value) => expect(value).toBe('number:15'));
            page.page.element(by.model('start.date')).getAttribute('value').then((value) => expect(value).toBe('2017-Feb-15'));
            page.page.element(by.model('end.date')).getAttribute('value').then((value) => expect(value).toBe('2017-Feb-25'));


            page.resetButtonEl.click();

            browser.waitForAngular();

            for (let field = 0; field < page.formFields.length; fields++) {
                page.formFields[page.formFields[field]].getAttribute('value').then((value) => {
                    expect(value).toBe('');
                });
            }
        });

        // CREATE Button
        describe('create', () => {
            it('shouldn\'t be possible to click when the form is empty', () => {
                page.createButtonEl.getAttribute('disabled').then((value) => expect(value).toBeTruthy());
            });

            it('should be enabled when all fields are filled out', () => {
                fillOutForm();
                page.createButtonEl.getAttribute('disabled').then((value) => expect(value).toBeFalsy());
            });

            it('should create a recurring charge when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if a recurring charge is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => expect(value).toBeFalsy());
            });

            it('should be visible if a recurring charge is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => expect(value).toBeTruthy());
                });
            });

            it('should be enabled if a recurring charge is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => expect(value).toBeFalsy());
                });
            });

            it('should update when the recurring charge is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.page.element(by.model('start.date')).clear().then(() => {
                        page.page.element(by.model('start.date')).sendKeys('2017-Apr-01');
                    });
                    page.page.element(by.model('start.date')).getAttribute('value').then((value) => expect(value).toBe('2017-Apr-01'));

                    el[0].click();

                    page.page.element(by.model('start.date')).clear().then(() => {
                        page.page.element(by.model('start.date')).sendKeys('2017-Feb-15');
                    });
                    page.updateButtonEl.click();

                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.page.element(by.model('start.date')).getAttribute('value').then((value) => expect(value).toBe('2017-Feb-15'));
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if a recurring charge is not selected', () => {
                browser.waitForAngular();
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            });

            it('should be visible if a recurring charge is selected', () => {
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

                    page.deleteButtonEl.click().then(() => {
                        popup();
                        let date = page.page.element(by.model('start.date')).getAttribute('value');
                        date.then((val) => expect(val).toBe(''));
                    });
                });
            });
        });

        // TABLE Filter
        describe('filter', () => {

            it('should filter all from table', () => {

                page.textFilter.sendKeys('zzzz');

                element.all(by.repeater('item in list')).then((el) => expect(el[0]).toBeUndefined());
            });

        });

    }); //End suite

}); //End view
