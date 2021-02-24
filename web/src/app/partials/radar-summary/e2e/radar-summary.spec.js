'use strict';

describe('radar-summary view', () => {
  let page;
  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/radar-summary');
    page = require('./radar-summary.po');
    mock(['radar-summary/e2e/radar-summary.mock']);
    browser.waitForAngular();
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      page.formFields['date'].sendKeys('2017-Feb-07');
      page.formFields['flight_identifier'].sendKeys('A67');
      page.formFields['day_of_flight'].sendKeys('2017-Feb-07');
      page.formFields['departure_time'].sendKeys('1400');
      page.formFields['registration'].sendKeys('T56');
      page.formFields['route'].sendKeys('Test');
      page.formFields['fir_entry_point'].sendKeys('Y7');
      page.formFields['fir_entry_time'].sendKeys('1405');
      page.formFields['fir_exit_point'].sendKeys('R2');
      page.formFields['fir_exit_time'].sendKeys('1410');
      page.formFields['flight_rule'].sendKeys('IFR');
      page.formFields['flight_travel_category'].sendKeys('Overflight');

      page.depAerodrome.get(0).click();
      page.destAerodrome.get(0).click();
      page.aircraftType.get(0).click();
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

      page.aircraftType.getAttribute('value').then((value) => {
        expect(value[0]).toEqual('string:S123');
      });
      page.formFields['date'].getAttribute('value').then((value) => {
        expect(value).toBe('2017-Feb-07');
      });
      page.formFields['flight_identifier'].getAttribute('value').then((value) => {
        expect(value).toBe('A67');
      });
      page.formFields['day_of_flight'].getAttribute('value').then((value) => {
        expect(value).toBe('2017-Feb-07');
      });
      page.formFields['departure_time'].getAttribute('value').then((value) => {
        expect(value).toBe('1400');
      });
      page.formFields['registration'].getAttribute('value').then((value) => {
        expect(value).toBe('T56');
      });
      page.depAerodrome.getAttribute('value').then((value) => {
        expect(value[0]).toEqual('string:Ottawa');
      });
      page.destAerodrome.getAttribute('value').then((value) => {
        expect(value[0]).toEqual('string:Ottawa');
      });
      page.formFields['route'].getAttribute('value').then((value) => {
        expect(value).toBe('Test');
      });
      page.formFields['fir_entry_point'].getAttribute('value').then((value) => {
        expect(value).toBe('Y7');
      });
      page.formFields['fir_entry_time'].getAttribute('value').then((value) => {
        expect(value).toBe('1405');
      });
      page.formFields['fir_exit_point'].getAttribute('value').then((value) => {
        expect(value).toBe('R2');
      });
      page.formFields['fir_exit_time'].getAttribute('value').then((value) => {
        expect(value).toBe('1410');
      });
      page.formFields['flight_rule'].getAttribute('value').then((value) => {
        expect(value).toBe('string:IFR');
      });
      page.formFields['flight_travel_category'].getAttribute('value').then((value) => {
        expect(value).toBe('string:Overflight');
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

      it('should create a radar summary when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a radar summary is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a radar summary is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a radar summary is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the radar summary is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['route'].clear().then(() => {
            page.formFields['route'].sendKeys('ABS');
          });

          page.formFields['route'].getAttribute('value').then((value) => {
            expect(value).toBe('ABS');
          });

          page.formFields['route'].clear().then(() => {
            el[0].click();
            page.formFields['route'].sendKeys('test');
          });

          page.updateButtonEl.click();
          popup();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['route'].getAttribute('value').then((value) => {
            expect(value).toBe('test');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a radar summary is not selected', () => {
        browser.waitForAngular();
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      })

      it('should be visible if a radar summary is selected', () => {
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
            let name = page.formFields.route.getAttribute('value');
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
    });
});
