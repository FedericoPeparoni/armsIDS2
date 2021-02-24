'use strict';

describe('mtow view', () => {
    let page;
    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/mtow');
        page = require('./mtow.po');
        mock(['mtow/e2e/mtow.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['upper_limit'].sendKeys('6');
            page.formFields['average_mtow_factor'].sendKeys('1');
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
            page.formFields['average_mtow_factor'].getAttribute('value').then((value) => {
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

            it('should create a mtow factor when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if a mtow factor is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if a mtow factor is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if a mtow factor is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    fillOutForm();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the mtow factor is updated', () => {
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
            it('shouldn\'t be visible if a mtow factor is not selected', () => {
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if a mtow factor is selected', () => {
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