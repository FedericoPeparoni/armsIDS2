'use strict';

describe('flight-movement-management view', () => {
  let page;
  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/flight-movement-management');
    page = require('./flight-movement-management.po');
    mock(['flight-movement-management/e2e/flight-movement-management.mock']);
    browser.waitForAngular();
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillFlightMovements() {
      page.flightFields['flight_id'].sendKeys('AB123');
      page.flightFields['item18_reg_num'].sendKeys('2359');
      page.flightFields['date_of_flight'].sendKeys('2017-Feb-08');
      page.flightFields['dep_time'].sendKeys('2359');
      page.flightFields['dep_ad'].sendKeys('Aerodrome1');
      page.flightFields['dest_ad'].sendKeys('Aerodrome2');
      page.flightFields['flight_type'].sendKeys('S');
      page.flightFields['flight_rules'].sendKeys('I');
      page.flightFields['item18_status'].sendKeys('PENDING');
      page.flightFields['item18_rmk'].sendKeys('Test Remark');
      page.flightFields['item18_dep'].sendKeys('AAAA');
      page.flightFields['item18_dest'].sendKeys('AAAA');
      page.flightFields['fpl_route'].sendKeys('Test');
      page.flightFields['arrival_ad'].sendKeys('Aerodrome');
      page.flightFields['arrival_time'].sendKeys('2359');
      page.flightFields['user_crossing_distance'].sendKeys('Test');
      page.flightFields['parking_time'].sendKeys('1234');
      page.flightFields['passengers_chargeable_intern'].sendKeys('5');
      page.flightFields['passengers_chargeable_domestic'].sendKeys('5');
      page.flightFields['passengers_child'].sendKeys('5');
      page.flightFields['tasp_charge'].sendKeys('1');
      page.flightFields['elapsed_time'].sendKeys('1234');
      page.flightFields['cruising_speed_or_mach_number'].sendKeys('N123');
      element(by.cssContainingText('option', 'Sputnik')).click();
      element(by.cssContainingText('option', 'Air Botswana')).click();
    }

    function popup() {
      let EC = protractor.ExpectedConditions;
      let confirmButton = element(by.buttonText('Confirm'));
      browser.wait(EC.visibilityOf(confirmButton), 5000);
      confirmButton.click();
    }

    // RESET Button and Form Fields
    it('should clear fields in flight movements tab when the reset button is clicked', () => {
      fillFlightMovements();

      page.flightFields['flight_id'].getAttribute('value').then((value) => {
        expect(value).toBe('AB123');
      });
      page.flightFields['item18_reg_num'].getAttribute('value').then((value) => {
        expect(value).toBe('2359');
      });
      page.flightFields['date_of_flight'].getAttribute('value').then((value) => {
        expect(value).toBe('2017-Feb-08');
      });
      page.flightFields['dep_time'].getAttribute('value').then((value) => {
        expect(value).toBe('2359');
      });
      page.flightFields['dep_ad'].getAttribute('value').then((value) => {
        expect(value).toBe('Aerodrome1');
      });
      page.flightFields['dest_ad'].getAttribute('value').then((value) => {
        expect(value).toBe('Aerodrome2');
      });
      page.aircraftType.getAttribute('value').then((value) => {
        expect(value[0]).toBe('string:Sputnik');
      });
      page.account.getAttribute('value').then((value) => {
        expect(value[0]).toBe('number:1');
      });
      page.flightFields['flight_type'].getAttribute('value').then((value) => {
        expect(value).toBe('S');
      });
      page.flightFields['item18_status'].getAttribute('value').then((value) => {
        expect(value).toBe('PENDING');
      });
      page.flightFields['item18_rmk'].getAttribute('value').then((value) => {
        expect(value).toBe('Test Remark');
      });
      page.flightFields['item18_dep'].getAttribute('value').then((value) => {
        expect(value).toBe('AAAA');
      });
      page.flightFields['item18_dest'].getAttribute('value').then((value) => {
        expect(value).toBe('AAAA');
      });
      page.flightFields['fpl_route'].getAttribute('value').then((value) => {
        expect(value).toBe('Test');
      });
      page.flightFields['arrival_ad'].getAttribute('value').then((value) => {
        expect(value).toBe('Aerodrome');
      });
      page.flightFields['arrival_time'].getAttribute('value').then((value) => {
        expect(value).toBe('2359');
      });
      page.flightFields['user_crossing_distance'].getAttribute('value').then((value) => {
        expect(value).toBe('Test');
      });
      page.flightFields['parking_time'].getAttribute('value').then((value) => {
        expect(value).toBe('1234');
      });
      page.flightFields['passengers_chargeable_intern'].getAttribute('value').then((value) => {
        expect(value).toBe('5');
      });
      page.flightFields['passengers_chargeable_domestic'].getAttribute('value').then((value) => {
        expect(value).toBe('5');
      });
      page.flightFields['passengers_child'].getAttribute('value').then((value) => {
        expect(value).toBe('5');
      });
      page.flightFields['tasp_charge'].getAttribute('value').then((value) => {
        expect(value).toBe('1');
      });
      page.flightFields['elapsed_time'].getAttribute('value').then((value) => {
        expect(value).toBe('1234');
      });
      page.flightFields['cruising_speed_or_mach_number'].getAttribute('value').then((value) => {
        expect(value).toBe('N123');
      });

      page.resetButtonEl.click();

      for (let field = 0; field < page.flightFields.length; fields++) {
        page.flightFields[page.flightFields[field]].getAttribute('value').then((value) => {
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

      it('should be possible to click when the form is filled out', () => {
        fillFlightMovements();

        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should create a flight movement when the form is submitted', () => {
        fillFlightMovements();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a flight movement is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a flight movement is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a flight movement is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the flight movement is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.flightFields['flight_type'].getAttribute('value').then((value) => {
            expect(value).toBe('N');
          });

          page.updateButtonEl.click();
          popup();
          browser.waitForAngular();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.flightFields['flight_type'].getAttribute('value').then((value) => {
            expect(value).toBe('N');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a flight movement is not selected', () => {
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      })

      it('should be visible if a flight movement is selected', () => {
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
            let name = page.flightFields['flight_type'].getAttribute('value');
            name.then((val) => {
              expect(val).toBe('');
            });
          });
        });
      });
    });

    // RECONCILE Button
    describe('reconcile button', () => {
      it('shouldn\'t be enabled if a flight movement is not checked', () => {
        page.reconcileButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        })
      })

      it('should be enabled if a flight movement is checked', () => {
        page.checkedFlightMovement.click().then(() => {
          page.reconcileButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });
    });

    // RECALCULATE Button
    describe('recalculate button', () => {
      it('shouldn\'t be enabled if a flight movement is not checked', () => {
        page.recalculateButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        })
      })

      it('should be enabled if a flight movement is checked', () => {
        page.checkedFlightMovement.click().then(() => {
          page.recalculateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });
    });

    // FLIGHT PLAN Button
    describe('flight plan button', () => {
      it('shouldn\'t be enabled if a flight movement is not checked', () => {
        page.fplButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        })
      })

      it('should be enabled if a flight movement is checked', () => {
        page.checkedFlightMovement.click().then(() => {
          page.fplButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });
    });

    // GENERATE INVOICES Button
    describe('generate invoices button', () => {
      it('shouldn\'t be enabled if a flight movement is not checked', () => {
        page.recalculateButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        })
      })

      it('should be enabled if a flight movement is checked', () => {
        page.checkedFlightMovement.click().then(() => {
          page.recalculateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should NOT enable if an incomplete status flight movement is checked', () => {
        page.checkedIncompleteFlightMovement.click().then(() => {
          page.recalculateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should NOT enable if two movements with different accounts selected', () => {
        page.checkedFlightMovement.click();
        page.checkedIncompleteFlightMovement.click().then(() => {
          page.recalculateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });
    });

    // TABLE Filter
    describe('filter', () => {

      it('should filter all from table', () => {

        page.textFilter.sendKeys('####');

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeUndefined();
        });
      });

      it('should filter out flight movement if status "INCOMPLETE" is selected', () => {

        element(by.cssContainingText('option', 'Incomplete Flight Movements')).click();
        browser.waitForAngular();

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeDefined();
        });
      });

      it('should filter out flight movement if status "PENDING" is selected', () => {

        element(by.cssContainingText('option', 'Pending (Complete) Flight Movements')).click();
        browser.waitForAngular();

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeDefined();
        });
      });

      it('should filter out flight movement when flight type changed', () => {

        element(by.cssContainingText('option', 'Regional Overflight')).click();
        browser.waitForAngular();

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeUndefined();
        });
      });

      it('Flight Movement Status filter should be set automatically to Incomplete Flight Movements when Incomplete Flight Reason is selected', () => {

        element(by.cssContainingText('option', 'Missing MTOW')).click();
        browser.waitForAngular();

        expect(element(by.cssContainingText('option', 'Incomplete Flight Movements')).isDisplayed()).toBeTruthy();

      });

      it('should be enabled when incomplete is selected', () => {

        element(by.cssContainingText('option', 'Incomplete Flight Movements')).click();
        browser.waitForAngular();

        page.incompleteReasonFilter.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        })
      });

    });

  }); //End suite

}); //End view
