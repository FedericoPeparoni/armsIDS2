'use strict';

describe('aircraft-exempt-management view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aircraft-exempt-management');
        page = require('./aircraft-exempt-management.po');
        mock(['aircraft-exempt-management/e2e/aircraft-exempt-management.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            element(by.cssContainingText('option', 'S123')).click();
            page.formFields['enroute_fees_exempt'].sendKeys(100);
            page.formFields['late_arrival_fees_exempt'].sendKeys(100);
            page.formFields['late_departure_fees_exempt'].sendKeys(100);
            page.formFields['parking_fees_exempt'].sendKeys(100);
            page.formFields['approach_fees_exempt'].sendKeys(100);
            page.formFields['aerodrome_fees_exempt'].sendKeys(100);
            page.formFields['adult_fees_exempt'].sendKeys(null);
            page.formFields['child_fees_exempt'].sendKeys(100);
            page.formFields['flight_notes'].sendKeys('Test Flight Notes');
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

            page.formFields['aircraft_type'].getText().then((value) => {
                expect(value).toBe('S123');
            });
            page.formFields['enroute_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['late_arrival_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['late_departure_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['parking_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['approach_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['aerodrome_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['adult_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['child_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe(100);
            });
            page.formFields['flight_notes'].getAttribute('value').then((value) => {
                expect(value).toBe('Test Flight Notes');
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

            it('should create an exempt aircraft when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an exempt aircraft is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an exempt aircraft is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an exempt aircraft is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    fillOutForm();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the exempt aircraft is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['enroute_fees_exempt'].sendKeys(null);

                    page.formFields['enroute_fees_exempt'].getAttribute('value').then((value) => {
                        expect(value).toBe(null);
                    });

                    el[0].click();
                    page.formFields['enroute_fees_exempt'].sendKeys(100);

                    page.updateButtonEl.click();

                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['enroute_fees_exempt'].getAttribute('value').then((value) => {
                        expect(value).toBe(100);
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an exempt aircraft is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            });

            it('should be visible if an exempt aircraft is selected', () => {
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

                        let flightNotes = page.formFields.flight_notes.getAttribute('value');
                        flightNotes.then((val) => {
                            expect(val).toBe('');
                        });
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