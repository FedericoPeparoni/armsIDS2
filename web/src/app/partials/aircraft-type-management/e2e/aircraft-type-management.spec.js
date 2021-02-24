'use strict';

describe('aircraft type view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aircraft-type-management');
        page = require('./aircraft-type-management.po');
        mock(['aircraft-type-management/e2e/aircraft-type-management.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['aircraft_type'].sendKeys('Test');
            page.formFields['aircraft_name'].sendKeys('Test-ABC');
            page.formFields['manufacturer'].sendKeys('Airbus');

            element(by.id("wake-turbulence")).click();
            browser.actions().sendKeys("T").perform();

            page.formFields['maximum_takeoff_weight'].sendKeys('350.85');
        }

        function popup() {
            let EC = protractor.ExpectedConditions;
            let confirmButton = element(by.buttonText('Confirm'));
            browser.wait(EC.visibilityOf(confirmButton), 4000);
            confirmButton.click();
        }

        // RESET Button and Form Fields
        it('should clear fields when the reset button is clicked', () => {
            fillOutForm();

            page.formFields['aircraft_type'].getAttribute('value').then((value) => {
                expect(value).toBe('Test');
            });
            page.formFields['aircraft_name'].getAttribute('value').then((value) => {
                expect(value).toBe('Test-ABC');
            });
            page.formFields['manufacturer'].getAttribute('value').then((value) => {
                expect(value).toBe('Airbus');
            });
            page.formFields['wake_turbulence_category'].getText().then((value) => {
                expect(value).toBe('T');
            });
            page.formFields['maximum_takeoff_weight'].getAttribute('value').then((value) => {
                expect(value).toBe('350.85');
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

            it('should create an aircraft type when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an aircraft type is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an aircraft type is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an aircraft type is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the aircraft type is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['aircraft_name'].clear().then(() => {
                        page.formFields['aircraft_name'].sendKeys('Test-ABC');
                    });

                    page.formFields['aircraft_name'].getAttribute('value').then((value) => {
                        expect(value).toBe('Test-ABC');
                    });

                    page.formFields['aircraft_name'].clear().then(() => {
                        el[0].click();
                        page.formFields['aircraft_name'].sendKeys('Test-123');

                    });
                    page.updateButtonEl.click();
                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['aircraft_name'].getAttribute('value').then((value) => {
                        expect(value).toBe('Test-123');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an aircraft type is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if an aircraft type is selected', () => {
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
                    page.formFields['aircraft_name'].getAttribute('value').then((value) => {
                        expect(value).toBe('');
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
