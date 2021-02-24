'use strict';

describe('rejected-items view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/rejected-items');
        page = require('./rejected-items.po');
        mock(['rejected-items/e2e/rejected-items.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

      function popup() {
            let EC = protractor.ExpectedConditions;
            let confirmButton = element(by.buttonText('Confirm'));
            browser.wait(EC.visibilityOf(confirmButton), 5000);
            confirmButton.click();
        }


        // FORM Fields
        it('form fields should be equal to the record in header', () => {
          element.all(by.repeater('item in list')).then((el) => {
              el[0].click();
              browser.waitForAngular();

              element.all(by.model('array[$index]')).get(0).getAttribute('value').then((value) => expect(value).toBe('16-Feb-17'));
              element.all(by.model('array[$index]')).get(1).getAttribute('value').then((value) => expect(value).toBe('AAAA'));
              element.all(by.model('array[$index]')).get(2).getAttribute('value').then((value) => expect(value).toBe('BRITISH AIRWAYS'));
              element.all(by.model('array[$index]')).get(3).getAttribute('value').then((value) => expect(value).toBe('UG863/UM002'));
              element.all(by.model('array[$index]')).get(4).getAttribute('value').then((value) => expect(value).toBe(''));
              element.all(by.model('array[$index]')).get(5).getAttribute('value').then((value) => expect(value).toBe('B744'));
              element.all(by.model('array[$index]')).get(6).getAttribute('value').then((value) => expect(value).toBe('BAW'));
              element.all(by.model('array[$index]')).get(7).getAttribute('value').then((value) => expect(value).toBe('FAOR'));
              element.all(by.model('array[$index]')).get(8).getAttribute('value').then((value) => expect(value).toBe('EGLL'));
              element.all(by.model('array[$index]')).get(9).getAttribute('value').then((value) => expect(value).toBe('RUDAS'));
              element.all(by.model('array[$index]')).get(10).getAttribute('value').then((value) => expect(value).toBe('0105'));
              element.all(by.model('array[$index]')).get(11).getAttribute('value').then((value) => expect(value).toBe('320'));
              element.all(by.model('array[$index]')).get(12).getAttribute('value').then((value) => expect(value).toBe('MNV'));
              element.all(by.model('array[$index]')).get(13).getAttribute('value').then((value) => expect(value).toBe('1255'));
              element.all(by.model('array[$index]')).get(14).getAttribute('value').then((value) => expect(value).toBe('320'));
              element.all(by.model('array[$index]')).get(15).getAttribute('value').then((value) => expect(value).toBe('BUGRO'));
              element.all(by.model('array[$index]')).get(16).getAttribute('value').then((value) => expect(value).toBe('1615'));
              element.all(by.model('array[$index]')).get(17).getAttribute('value').then((value) => expect(value).toBe('320'));
              element.all(by.model('array[$index]')).get(18).getAttribute('value').then((value) => expect(value).toBe('H'));
              element.all(by.model('array[$index]')).get(19).getAttribute('value').then((value) => expect(value).toBe('sch'));
          });

        });

        // UPDATE Button
        describe('update', () => {
            it('shouldn\'t be visible if an rejected item is not selected', () => {
                page.updateButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if an rejected item is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.updateButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });


            it('should update when the rejected item is updated', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    element.all(by.model('array[$index]')).get(1).clear().then(() => {
                        element.all(by.model('array[$index]')).get(1).sendKeys('ABS');
                    });

                    page.updateButtonEl.click();
                    popup();
                });

                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    element.all(by.model('array[$index]')).get(1).getAttribute('value').then((value) => {
                        expect(value).toBe('AAAA');
                    });
                });
            });
        });

        // DELETE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an rejected item is not selected', () => {
                browser.waitForAngular();
                page.deleteButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                })
            })

            it('should be visible if an rejected item is selected', () => {
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

                        element(by.model('array[$index]')).isPresent().then((value) => {
                            expect(value).toBeFalsy();
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
