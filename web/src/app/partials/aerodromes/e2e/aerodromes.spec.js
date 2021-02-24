'use strict';

describe('aerodromes view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aerodromes');
        page = require('./aerodromes.po');
        mock(['aerodromes/e2e/aerodromes.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['aerodrome_name'].sendKeys('TEST');
            page.formFields['geometry.coordinates[0]'].sendKeys('9');
            page.formFields['geometry.coordinates[1]'].sendKeys('0');

            element(by.id("billing-center")).click();
            browser.actions().sendKeys("Test Billing Centre").perform();

            element(by.id("aerodrome-category")).click();
            browser.actions().sendKeys("Test AC").perform();

            page.formFields['is_default_billing_center'].sendKeys('false');
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

            page.formFields['aerodrome_name'].getAttribute('value').then((value) => {
                expect(value).toBe('TEST');
            });
            page.formFields['geometry.coordinates[0]'].getAttribute('value').then((value) => {
                expect(value).toBe('9');
            });
            page.formFields['geometry.coordinates[1]'].getAttribute('value').then((value) => {
                expect(value).toBe('0');
            });
            page.formFields['billing_center'].getText().then((value) => {
                expect(value).toBe('Test Billing Centre');
            });

            page.formFields['is_default_billing_center'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:false');
            });
            page.formFields['aerodrome_category'].getText().then((value) => {
                expect(value).toBe('Test AC');
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

            it('shouldn\'t be possible to click when longitude is above 180', () => {
                fillOutForm();

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeFalsy();
                });

                page.formFields['geometry.coordinates[1]'].clear().then(() => {
                      page.formFields['geometry.coordinates[1]'].sendKeys('181');
                });

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeTruthy();
                });
            });

            it('shouldn\'t be possible to click when latitude is above 90', () => {
                fillOutForm();

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeFalsy();
                });

                page.formFields['geometry.coordinates[0]'].clear().then(() => {
                      page.formFields['geometry.coordinates[0]'].sendKeys('91');
                });

                page.createButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeTruthy();
                });
            });

            it('should create an aerodrome when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an aerodrome is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an aerodrome is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {

                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an aerodrome is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    page.formFields['geometry.coordinates[0]'].sendKeys('9');
                    page.formFields['geometry.coordinates[1]'].sendKeys('0');

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the aerodrome is updated', () => {
               element.all(by.repeater('item in list')).then((el) => {
                   el[0].click();

                   page.formFields['geometry.coordinates[0]'].clear().then(() => {
                       page.formFields['geometry.coordinates[0]'].sendKeys('9');
                   });

                   page.formFields['geometry.coordinates[0]'].getAttribute('value').then((value) => {
                       expect(value).toBe('9');
                   });

                   page.updateButtonEl.click();

                   popup();
               });

               element.all(by.repeater('item in list')).then((el) => {
                   el[0].click();

                   page.formFields['geometry.coordinates[0]'].getAttribute('value').then((value) => {
                       expect(value).toBe('3');
                   });
               });
           });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an aerodrome is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect( value ).toBeFalsy();
                })
            })

            it('should be visible if an aerodrome is selected', () => {
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
                        let name = page.formFields.aerodrome_name.getAttribute('value');
                        name.then((val) => {expect(val).toBe('');});
                    });
                });
            });
        });

        // TABLE Filter
        describe('filter', () => {
            it('should filter all from table if input more than 4 chars', () => {

                page.textFilter.sendKeys('zzzz');

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });

        });

    }); //End suite

}); //End view
