'use strict';

describe('utilities-towns view', () => {
  let page;
  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/utilities-towns');
    page = require('./utilities-towns.po');
    mock(['utilities-towns/e2e/utilities-towns.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      page.formFields['town_or_village_name'].sendKeys('Hill Valley');
      page.formFields['water_utility_schedule'].sendKeys('Schedule: 1 / Charge: 5');
      page.formFields['electricity_utility_schedule'].sendKeys('Schedule: 2 / Charge: 777');
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

      page.formFields['town_or_village_name'].getAttribute('value').then((value) => {
        expect(value).toBe('Hill Valley');
      });
      page.formFields['water_utility_schedule'].getAttribute('value').then((value) => {
        expect(value).toBe('Schedule: 1 / Charge: 5');
      });
      page.formFields['electricity_utility_schedule'].getAttribute('value').then((value) => {
        expect(value).toBe('Schedule: 2 / Charge: 777');
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

      it('should create a utilities town when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });

      it('should no longer be enabled after reset is clicked', () => {
        fillOutForm();
        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });
        page.resetButtonEl.click();

        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        });
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a utilities town is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a utilities town is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a utilities town is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the utilities town is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['town_or_village_name'].clear().then(() => {
            page.formFields['town_or_village_name'].sendKeys('Tinseltown');
          });

          page.formFields['town_or_village_name'].sendKeys('Tinseltown');

          page.updateButtonEl.click();
          popup();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['town_or_village_name'].getAttribute('value').then((value) => {
            expect(value).toBe('Tinseltown');
          });
        });
      });

      it('should be disappear if the reset button is clicked', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });

          page.resetButtonEl.click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a utilities town is not selected', () => {
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });

      it('should be visible if a utilities town is selected', () => {
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
            let name = page.formFields.town_or_village_name.getAttribute('value');
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
