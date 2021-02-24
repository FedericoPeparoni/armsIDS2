// still need multiselect aerodromes tests
// untested

'use strict';

describe('repositioning-aerodrome-clusters view', () => {
  var page;
  var helpers;

  var mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/repositioning-aerodrome-clusters');
    page = require('./repositioning-aerodrome-clusters.po');
    mock(['repositioning-aerodrome-clusters/e2e/repositioning-aerodrome-clusters.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      page.formFields['repositioning_aerodrome_cluster_name'].sendKeys('Test Cluster');
      page.formFields['enroute_fees_are_exempt'].sendKeys('True');
      page.formFields['approach_fees_are_exempt'].sendKeys('True');
      page.formFields['aerodrome_fees_are_exempt'].sendKeys('True');
      page.formFields['late_arrival_fees_are_exempt'].sendKeys('True');
      page.formFields['late_departure_fees_are_exempt'].sendKeys('True');
      page.formFields['adult_passenger_fees_are_exempt'].sendKeys('True');
      page.formFields['child_passenger_fees_are_exempt'].sendKeys('True');
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

      page.formFields['repositioning_aerodrome_cluster_name'].getAttribute('value').then((value) => {
        expect(value).toBe('Test Cluster');
      });
      page.formFields['enroute_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['approach_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['aerodrome_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['late_arrival_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['late_departure_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['adult_passenger_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['child_passenger_fees_are_exempt'].getAttribute('value').then((value) => {
        expect(value).toBe('boolean:true');
      });
      page.formFields['flight_notes'].getAttribute('value').then((value) => {
        expect(value).toBe('Test Flight Notes');
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

      it('should create a repositioning aerodrome cluster when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a repositioning aerodrome cluster is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a repositioning aerodrome cluster is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a repositioning aerodrome cluster is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the repositioning aerodrome is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['enroute_fees_are_exempt'].sendKeys('True');

          page.formFields['enroute_fees_are_exempt'].getAttribute('value').then((value) => {
            expect(value).toBe('boolean:true');
          });

          el[0].click();

          page.formFields['enroute_fees_are_exempt'].sendKeys('False');

          page.updateButtonEl.click();

          popup();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['enroute_fees_are_exempt'].getAttribute('value').then((value) => {
            expect(value).toBe('boolean:false');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a repositioning aerodrome cluster is not selected', () => {
        browser.waitForAngular();
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });

      it('should be visible if a repositioning aerodrome cluster is selected', () => {
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
