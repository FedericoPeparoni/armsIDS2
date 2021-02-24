'use strict';

describe('aircraft-registration view', () => {
    let page;
    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aircraft-registration');
        page = require('./aircraft-registration.po');
        mock(['aircraft-registration/e2e/aircraft-registration.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['registration_number'].sendKeys('AB123');

            element(by.id("account")).click();
            browser.actions().sendKeys('Mock Account').perform();

            element(by.id("aircraft-type")).click();
            browser.actions().sendKeys('A30B').perform();

            page.formFields['mtow_override'].sendKeys('50');

            element(by.id("country-of-registration")).click();
            browser.actions().sendKeys('Afghanistan').perform();

            page.page.element(by.model('start.date')).sendKeys('2017-Jan-17');
            page.page.element(by.model('end.date')).sendKeys('2017-Feb-10');
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

            page.formFields['registration_number'].getAttribute('value').then((value) => {
                expect(value).toBe('AB123');
            });
            page.formFields['account'].getText().then((value) => {
                expect(value).toBe('Mock Account');
            });
            page.formFields['aircraft_type'].getText().then((value) => {
                expect(value).toBe('A30B');
            });
            page.formFields['mtow_override'].getAttribute('value').then((value) => {
                expect(value).toBe('50');
            });
            page.formFields['country_of_registration'].getText().then((value) => {
                expect(value).toBe('Afghanistan');
            });

            page.page.element(by.model('start.date')).getAttribute('value').then((value) => {
                expect(value).toBe('2017-Jan-17');
            });

            page.page.element(by.model('end.date')).getAttribute('value').then((value) => {
                expect(value).toBe('2017-Feb-10');
            });

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

            it('should disable if the reset is clicked', () => {
                fillOutForm();

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeFalsy();
                });

                page.resetButtonEl.click();

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeTruthy();
                });
            });

            it('should create an aircraft registration when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an aircraft registration is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an aircraft registration is selected', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an aircraft registration is selected', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should disappear if a reset is clicked', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });

                    page.resetButtonEl.click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeFalsy();
                    });

                });
            });

            it('should update when the aircraft registration is updated', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.formFields['registration_number'].clear().then(() => {
                        page.formFields['registration_number'].sendKeys('CD456');
                    });

                    page.updateButtonEl.click();

                    popup();

                    browser.waitForAngular();
                });

                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.formFields['registration_number'].getAttribute('value').then((value) => {
                        expect(value).toBe('CD456');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an aircraft registration is not selected', () => {
                browser.waitForAngular();
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            });

            it('should be visible if an aircraft registration is selected', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.deleteButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should disappear if a reset is clicked', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.deleteButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });

                    page.resetButtonEl.click();

                    page.deleteButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeFalsy();
                    });

                });
            });

            it('should remove data upon clicking delete', () => {
                element.all(by.repeater('aircraft in list')).then((el) => {
                    el[0].click();

                    page.deleteButtonEl.click().then(() => {
                        popup();
                        let name = page.formFields['registration_number'].getAttribute('value');
                        name.then((val) => {
                            expect(val).toBe('');
                        });
                    });
                });
            });
        });

        // TABLE Filter
        describe('filter', () => {

            it('should filter all from table', () => {

                page.textFilter.sendKeys('zzzz');

                element.all(by.repeater('aircraft in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });

        });

    }); //End suite

}); //End view
