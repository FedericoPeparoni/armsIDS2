'use strict';

describe('account-exempt-management view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/account-exempt-management');
        page = require('./account-exempt-management.po');
        mock(['account-exempt-management/e2e/account-exempt-management.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['enroute'].sendKeys('True');
            page.formFields['landing'].sendKeys('True');
            page.formFields['departure'].sendKeys('True');
            page.formFields['parking'].sendKeys('True');
            page.formFields['adult'].sendKeys('True');
            page.formFields['child'].sendKeys('True');
            page.formFields['approach_fees_exempt'].sendKeys('True');
            page.formFields['aerodrome_fees_exempt'].sendKeys('True');
            page.account.get(0).click();
            page.formFields['flight_notes'].sendKeys('Test Flight Notes')
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

            page.formFields['enroute'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['landing'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['departure'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['parking'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['adult'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['child'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['approach_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['aerodrome_fees_exempt'].getAttribute('value').then((value) => {
                expect(value).toBe('boolean:true');
            });
            page.formFields['account_id'].getText().then((value) => {
                expect(value).toBe('Mock Account');
            });
            page.formFields['flight_notes'].getAttribute('value').then((value) => {
                expect(value).toBe('Test Flight Notes');
            })

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

            it('should create an exempt account when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an exempt account is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an exempt account is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be enabled if an exempt account is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    page.account.get(0).click();

                    page.updateButtonEl.getAttribute('disabled').then((value) => {
                        expect(value).toBeFalsy();
                    });
                });
            });

            it('should update when the exempt account is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();
                    page.account.get(0).click();

                    page.formFields['enroute'].sendKeys('False');

                    page.formFields['enroute'].getAttribute('value').then((value) => {
                        expect(value).toBe('boolean:false');
                    });

                    el[0].click();
                    page.account.get(0).click();

                    page.formFields['enroute'].sendKeys('True');

                    page.updateButtonEl.click();

                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.formFields['enroute'].getAttribute('value').then((value) => {
                        expect(value).toBe('boolean:true');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an exempt account is not selected', () => {
                browser.waitForAngular();
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            });

            it('should be visible if an exempt account is selected', () => {
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
                        let name = page.formFields.flight_notes.getAttribute('value');
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

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });

        });

    }); //End suite

}); //End view
