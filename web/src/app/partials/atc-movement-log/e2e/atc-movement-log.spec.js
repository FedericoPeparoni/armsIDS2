'use strict';

describe('atc-movement-log view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/atc-movement-log');
        page = require('./atc-movement-log.po');
        mock(['atc-movement-log/e2e/atc-movement-log.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
          page.formFields['date_of_contact'].sendKeys('2017-Mar-07');
          page.formFields['registration'].sendKeys('R456');
          page.formFields['operator_identifier'].sendKeys('167');
          page.formFields['route'].sendKeys('ABCD/DEF');
          page.formFields['flight_id'].sendKeys('A123');
          page.formFields['aircraft_type'].sendKeys('A320');
          element(by.id("departure_aerodrome")).click();
          browser.actions().sendKeys("Ottawa").perform();
          element(by.id("destination_aerodrome")).click();
          browser.actions().sendKeys("Ottawa").perform();
          page.formFields['fir_entry_point'].sendKeys('Z7');
          page.formFields['fir_entry_time'].sendKeys('1010');
          page.formFields['fir_mid_point'].sendKeys('Z7');
          page.formFields['fir_mid_time'].sendKeys('1010');
          page.formFields['fir_exit_point'].sendKeys('Z7');
          page.formFields['fir_exit_time'].sendKeys('1010');
          page.formFields['flight_level'].sendKeys('1000');
          element(by.id("wake_turbulence")).click();
          browser.actions().sendKeys("L").perform();
          element(by.id("flight_category")).click();
          browser.actions().sendKeys("sched").perform();
          element(by.id("flight_type")).click();
          browser.actions().sendKeys("normal").perform();
          page.formFields['day_of_flight'].sendKeys('2017-Mar-07');
          page.formFields['departure_time'].sendKeys('1010');
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

            page.formFields['date_of_contact'].getAttribute('value').then((value) => expect(value).toBe('2017-Mar-07'));
            page.formFields['registration'].getAttribute('value').then((value) => expect(value).toBe('R456'));
            page.formFields['operator_identifier'].getAttribute('value').then((value) => expect(value).toBe('167'));
            page.formFields['route'].getAttribute('value').then((value) => expect(value).toBe('ABCD/DEF'));
            page.formFields['flight_id'].getAttribute('value').then((value) => expect(value).toBe('A123'));
            page.formFields['aircraft_type'].getAttribute('value').then((value) => expect(value).toBe('A320'));
            page.formFields['departure_aerodrome'].getAttribute('value').then((value) => expect(value).toBe('Ottawa'));
            page.formFields['destination_aerodrome'].getAttribute('value').then((value) => expect(value).toBe('Ottawa'));
            page.formFields['fir_entry_point'].getAttribute('value').then((value) => expect(value).toBe('Z7'));
            page.formFields['fir_entry_time'].getAttribute('value').then((value) => expect(value).toBe('1010'));
            page.formFields['fir_mid_point'].getAttribute('value').then((value) => expect(value).toBe('Z7'));
            page.formFields['fir_mid_time'].getAttribute('value').then((value) => expect(value).toBe('1010'));
            page.formFields['fir_exit_point'].getAttribute('value').then((value) => expect(value).toBe('Z7'));
            page.formFields['fir_exit_time'].getAttribute('value').then((value) => expect(value).toBe('1010'));
            page.formFields['flight_level'].getAttribute('value').then((value) => expect(value).toBe('1000'));
            page.formFields['wake_turbulence'].getAttribute('value').then((value) => expect(value).toBe('L'));
            page.formFields['flight_category'].getAttribute('value').then((value) => expect(value).toBe('sched'));
            page.formFields['flight_type'].getAttribute('value').then((value) => expect(value).toBe('normal'));
            page.formFields['day_of_flight'].getAttribute('value').then((value) => expect(value).toBe('2017-Mar-07'));
            page.formFields['departure_time'].getAttribute('value').then((value) => expect(value).toBe('1010'));

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

                element(by.id("destination_aerodrome")).click();
                browser.actions().sendKeys("Ottawa").perform();

                element(by.id("departure_aerodrome")).click();
                browser.actions().sendKeys("Ottawa").perform();

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should create an atc movement log when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an atc movement log is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an atc movement log is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an atc movement log is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the atc movement log is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['registration'].clear().then(() => {
                        page.formFields['registration'].sendKeys('ABS');
                    });

                    page.formFields['registration'].getAttribute('value').then((value) => {
                        expect(value).toBe('ABS');
                    });

                    page.formFields['registration'].clear().then(() => {
                        el[0].click();
                        page.formFields['registration'].sendKeys('R456');
                    });

                    page.updateButtonEl.click();
                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['registration'].getAttribute('value').then((value) => {
                        expect(value).toBe('R456');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an atc movement log is not selected', () => {
                browser.waitForAngular();
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if an atc movement log is selected', () => {
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
                        let registration = page.formFields.registration.getAttribute('value');
                        registration.then((val) => { expect(val).toBe(''); });
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
