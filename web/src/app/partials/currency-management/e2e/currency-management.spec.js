'use strict';

describe('currency-management view', () => {
  let page;
  let helpers;

  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/?#/currency-management');
    page = require('./currency-management.po');
    mock(['currency-management/e2e/currency-management.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      page.formFields['currency_code'].sendKeys('CAD');
      page.formFields['currency_name'].sendKeys('Canadian Dollar');

      page.countries.get(0).click();
      browser.actions().sendKeys("TTT").perform();

      page.formFields['decimal_places'].sendKeys('1');
      page.formFields['symbol'].sendKeys('$');
      page.formFields['active'].sendKeys('True');

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

      page.formFields['currency_code'].getAttribute('value').then((value) => {
        expect(value).toBe('CAD');
      });
      page.formFields['country_code'].getText().then((value) => {
        expect(value).toBe('TTT');
      });
      page.formFields['active'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['currency_name'].getAttribute('value').then((value) => {
        expect(value).toBe('Canadian Dollar');
      });
      page.formFields['decimal_places'].getAttribute('value').then((value) => {
        expect(value).toBe('1');
      });
      page.formFields['symbol'].getAttribute('value').then((value) => {
        expect(value).toBe('$');
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

      it('should create a currency when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a currency is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a currency is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a currency is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the currency is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['decimal_places'].clear().then(() => {
            page.formFields['decimal_places'].sendKeys('1');
          });

          page.formFields['decimal_places'].getAttribute('value').then((value) => {
            expect(value).toBe('1');
          });

          page.formFields['decimal_places'].clear().then(() => {
            el[0].click();
            page.formFields['decimal_places'].sendKeys('4');
          });

          page.updateButtonEl.click();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['decimal_places'].getAttribute('value').then((value) => {
            expect(value).toBe('4');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a currency is not selected', () => {
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      })

      it('should be visible if a currency is selected', () => {
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
            let decimalPlaces = page.formFields.decimal_places.getAttribute('value');
            decimalPlaces.then((val) => {
              expect(val).toBe('');
            });
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

      it('should filter to respective 3 characters', () => {

        page.textFilter.sendKeys('TTT');

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0].getText()).toContain('TTT');
        });
      });
    });

  }); //End Form Suite

  describe('exchange form', () => {

    it('should not show by default', () => {
      element.all(by.repeater('rate in exchangeRates')).then((el) => {
        expect(el[0]).toBeUndefined();
      });
    });

    it('should show after an exchange rate is clicked', () => {
      element.all(by.repeater('item in list')).then((el) => {
        el[0].click().then(() => {
          element.all(by.repeater('rate in exchangeRates')).then((rate) => {
            expect(rate[0]).toBeDefined();
          });
        });
      });
    });

  }); //End Exchange Rate Form

}); //End view
