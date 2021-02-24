'use strict';

describe('sc-passenger-service-charge-return view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-passenger-service-charge-return');
        page = require('./sc-passenger-service-charge-return.po');
        mock(['sc-passenger-service-charge-return/e2e/sc-passenger-service-charge-return.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['flight_id'].sendKeys('2');
            page.formFields['children'].sendKeys('2');
            page.formFields['day_of_flight'].sendKeys('2017-Oct-25');
            page.formFields['departure_time'].sendKeys('1400');
            page.formFields['joining_passengers'].sendKeys('2');
            page.formFields['transit_passengers'].sendKeys('2');
            page.formFields['chargeable_itl_passengers'].sendKeys('2');
            page.formFields['chargeable_domestic_passengers'].sendKeys('2');
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

            page.formFields['flight_id'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });
            page.formFields['children'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });
            page.formFields['day_of_flight'].getAttribute('value').then((value) => {
                expect(value).toBe('2017-Oct-25');
            });
            page.formFields['departure_time'].getAttribute('value').then((value) => {
                expect(value).toBe('1400');
            });
            page.formFields['joining_passengers'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });
            page.formFields['transit_passengers'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });
            page.formFields['chargeable_itl_passengers'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });
            page.formFields['chargeable_domestic_passengers'].getAttribute('value').then((value) => {
                expect(value).toBe('2');
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

            it('should create a passenger service charge return when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if a passenger service charge return is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if a passenger service charge return is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if a passenger service charge return is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the passenger service charge return is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['flight_id'].clear().then(() => {
                        page.formFields['flight_id'].sendKeys('1');
                    });

                    page.formFields['flight_id'].getAttribute('value').then((value) => {
                        expect(value).toBe('1');
                    });

                    page.formFields['flight_id'].clear().then(() => {
                        el[0].click();
                        page.formFields['flight_id'].sendKeys('2');
                    });

                    page.updateButtonEl.click();
                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['flight_id'].getAttribute('value').then((value) => {
                        expect(value).toBe('2');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if a passenger service charge return is not selected', () => {
                browser.waitForAngular();
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if a passenger service charge return is selected', () => {
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
                        let name = page.formFields.flight_id.getAttribute('value');
                        name.then((val) => { expect(val).toBe(''); });
                    });
                });
            });
        });

        // TABLE Filter
        describe('filter', () => {
            it('should filter all from table', () => {

                page.textFilter.sendKeys('zzzz');

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });
        });

    }); //End suite

}); //End view