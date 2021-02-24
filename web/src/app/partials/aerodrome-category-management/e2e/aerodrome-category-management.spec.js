'use strict';

describe('aerodrome-category-management view', () => {
  let page;
  let helpers;

  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/aerodrome-category');
    page = require('./aerodrome-category-management.po');
    mock(['aerodrome-category-management/e2e/aerodrome-category-management.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      page.formFields['category_name'].sendKeys('Test 2');
      page.formFields['international_passenger_fee_adult'].sendKeys('0.01');
      page.formFields['international_passenger_fee_child'].sendKeys('0.02');
      page.formFields['domestic_passenger_fee_adult'].sendKeys('0.03');
      page.formFields['domestic_passenger_fee_child'].sendKeys('0.04');
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

      page.formFields['category_name'].getAttribute('value').then((value) => {
        expect(value).toBe('Test 2');
      });
      page.formFields['international_passenger_fee_adult'].getAttribute('value').then((value) => {
        expect(value).toBe('0.01');
      });
      page.formFields['international_passenger_fee_child'].getAttribute('value').then((value) => {
        expect(value).toBe('0.02');
      });
      page.formFields['domestic_passenger_fee_adult'].getAttribute('value').then((value) => {
        expect(value).toBe('0.03');
      });
      page.formFields['domestic_passenger_fee_child'].getAttribute('value').then((value) => {
        expect(value).toBe('0.04');
      });

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

      it('should create an aerodrome category when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if an aerodrome category is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if an aerodrome category is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          browser.waitForAngular();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if an aerodrome category is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          browser.waitForAngular();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('shouldn\'t be visible if an aerodrome category is selected and then reset is clicked', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          browser.waitForAngular();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });

          page.resetButtonEl.click();

          browser.waitForAngular();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the aerodrome category is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['category_name'].clear().then(() => {
            page.formFields['category_name'].sendKeys('Test 2');
          });

          page.updateButtonEl.click();

          popup();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['category_name'].getAttribute('value').then((value) => {
            expect(value).toBe('Test 2');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if an aerodrome category is not selected', () => {
        browser.waitForAngular();

        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });

      it('should be visible if an aerodrome category is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          browser.waitForAngular();

          page.deleteButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be removed upon clicking delete', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          browser.waitForAngular();

          page.deleteButtonEl.click().then(() => {
            popup();
            let name = page.formFields['category_name'].getAttribute('value');
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