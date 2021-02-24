'use strict';

describe('enroute-air-navigation-charges-management view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/enroute-air-navigation-charges-management');
        page = require('./enroute-air-navigation-charges-management.po');
        mock(['enroute-air-navigation-charges-management/e2e/enroute-air-navigation-charges-management.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['upper_limit'].sendKeys('6');
            page.formFields['domestic_formula'].sendKeys('Test');
            page.formFields['regional_departure_formula'].sendKeys('RegDep');
            page.formFields['regional_arrival_formula'].sendKeys('RegArr');
            page.formFields['regional_overflight_formula'].sendKeys('RegOv');
            page.formFields['international_departure_formula'].sendKeys('IntDep');
            page.formFields['international_arrival_formula'].sendKeys('IntArr');
            page.formFields['international_overflight_formula'].sendKeys('IntOv');
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

            page.formFields['upper_limit'].getAttribute('value').then((value) => {
                expect(value).toBe('6');
            });
            page.formFields['domestic_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('Test');
            });
            page.formFields['regional_departure_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('RegDep');
            });
            page.formFields['regional_arrival_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('RegArr');
            });
            page.formFields['regional_overflight_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('RegOv');
            });
            page.formFields['international_departure_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('IntDep');
            });
            page.formFields['international_arrival_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('IntArr');
            });
            page.formFields['international_overflight_formula'].getAttribute('value').then((value) => {
                expect(value).toBe('IntOv');
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

            it('should create a formula for enroute air navigation charges when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if a enroute air navigation charge is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if a enroute air navigation charge is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if a enroute air navigation charge is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    fillOutForm();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the enroute air navigation charge is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['upper_limit'].clear().then(() => {
                        page.formFields['upper_limit'].sendKeys('6');
                    });

                    page.formFields['upper_limit'].getAttribute('value').then((value) => {
                        expect(value).toBe('6');
                    });

                    page.formFields['upper_limit'].clear().then(() => {
                        el[0].click();
                        page.formFields['upper_limit'].sendKeys('1');

                    });

                    page.updateButtonEl.click();

                    browser.waitForAngular(); 

                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    page.formFields['upper_limit'].getAttribute('value').then((value) => {
                        expect(value).toBe('1');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if a enroute air navigation charge is not selected', () => {

                browser.waitForAngular();

                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if a enroute air navigation charge is selected', () => {
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