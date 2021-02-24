'use strict';

describe('utilities-schedules view', () => {
  var page;
  var helpers;

  var mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/utilities-schedules');
    page = require('./utilities-schedules.po');
    mock(['utilities-schedules/e2e/utilities-schedules.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutSchedule() {
      page.formFields['schedule_type'].sendKeys('Water');
      page.formFields['minimum_charge'].sendKeys('5000');
    }

    function popup() {
      let EC = protractor.ExpectedConditions;
      let confirmButton = element(by.buttonText('Confirm'));
      browser.wait(EC.visibilityOf(confirmButton), 5000);
      confirmButton.click();
    }

    function selectARange() {
      element.all(by.repeater('item in list')).then((items) => {
        items[0].click();
      });
      browser.waitForAngular();

      element.all(by.repeater('range in ranges')).then((elements) => {
        elements[0].click();
      });
      browser.waitForAngular();
    }

    // RESET Button
    it('should clear fields when the reset button is clicked', () => {
      fillOutSchedule();

      page.formFields['schedule_type'].getAttribute('value').then((value) => {
        expect(value).toBe('string:WATER');
      });

      page.formFields['minimum_charge'].getAttribute('value').then((value) => {
        expect(value).toBe('5000');
      });

      page.resetButtonEl.click();

      page.formFields['schedule_type'].getAttribute('value').then((value) => {
        expect(value).toBe('');
      });

      page.formFields['minimum_charge'].getAttribute('value').then((value) => {
        expect(value).toBe('');
      });

    });

    // CREATE Button
    describe('create', () => {
      it('shouldn\'t be possible to click when schedule type and minimum charge are empty', () => {
        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        });
      });

      it('should be enabled when schedule type and minimum charge are filled', () => {
        fillOutSchedule();
        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should disable if a reset is clicked', () => {
        fillOutSchedule();

        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });

        page.resetButtonEl.click();

        page.createButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        });

      });

      it('should create a schedule when the schedule fields are filled and the button is clicked', () => {
        fillOutSchedule();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a schedule is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a schedule is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a schedule is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should disappear if a reset is clicked', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });

          page.resetButtonEl.click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeFalsy();
          });

        });
      });

      it('should update when the schedule is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['minimum_charge'].clear().then(() => {
            page.formFields['minimum_charge'].sendKeys('700');
          })

          page.formFields['minimum_charge'].getAttribute('value').then((value) => {
            expect(value).toBe('700');
          });

          page.updateButtonEl.click();

          popup();

          browser.waitForAngular();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['minimum_charge'].getAttribute('value').then((value) => {
            expect(value).toBe('700');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a schedule is not selected', () => {
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });

      it('should be visible if a schedule is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.deleteButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should disappear if a reset is clicked', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.deleteButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });

          page.resetButtonEl.click();

          page.deleteButtonEl.isDisplayed().then((value) => {
            expect(value).toBeFalsy();
          });

        });
      });

      it('should be removed upon clicking delete', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.deleteButtonEl.click().then(() => {
            popup();
            let name = page.formFields.minimum_charge.getAttribute('value');
            name.then((val) => {
              expect(val).toBe('');
            });
          });
        });
      });
    });

    // Fields
    describe('field visibility', () => {
      it('should show all 4 fields when a schedule is selected', () => {

        page.formFields['range_top_end'].isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })

        page.formFields['unit_price'].isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['range_top_end'].isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          })

          page.formFields['unit_price'].isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          })

          page.formFields['minimum_charge'].isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          })

          page.formFields['schedule_type'].isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          })
        });
      });

      it('should show only range fields when range selected', () => {
        selectARange();

        page.formFields['range_top_end'].isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        })

        page.formFields['unit_price'].isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        })

        page.formFields['minimum_charge'].isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })

        page.formFields['schedule_type'].isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });

      it('should hide range fields when reset button clicked', () => {
        selectARange();

        page.resetButtonEl.click();

        page.formFields['range_top_end'].isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })

        page.formFields['unit_price'].isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });
    });

    // CREATE Range Brackets button
    describe('create range brackets button', () => {
      it('should not be visible until a schedule is selected', () => {
        page.createBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.createBracketsButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should not be enabled until range fields are filled', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.createBracketsButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeTruthy();
          });

          page.formFields['range_top_end'].sendKeys('5');
          page.formFields['unit_price'].sendKeys('5');

          page.createBracketsButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should create a range bracket when the button is clicked', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['range_top_end'].sendKeys('5');
          page.formFields['unit_price'].sendKeys('5');

          page.createBracketsButtonEl.click();
          browser.waitForAngular();
        });
      });
    });

    // UPDATE Range Brackets Button
    describe('update range brackets', () => {
      it('shouldn\'t be visible if a range is not selected', () => {
        page.updateBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a range is selected', () => {
        selectARange();
        page.updateBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        });
      });

      it('should be enabled if a range is selected', () => {
        selectARange();
        page.updateBracketsButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should disappear if a reset is clicked', () => {
        selectARange();

        page.updateBracketsButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });

        page.resetButtonEl.click();

        page.updateBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should update when the range is updated', () => {

        selectARange();

        page.formFields['range_top_end'].clear().then(() => {
          page.formFields['range_top_end'].sendKeys('500');
        });

        page.formFields['unit_price'].clear().then(() => {
          page.formFields['unit_price'].sendKeys('500');
        });

        page.updateBracketsButtonEl.click();

        popup();

        browser.waitForAngular();

        selectARange();

        page.formFields['range_top_end'].getAttribute('value').then((value) => {
          expect(value).toBe('500');
        });

        page.formFields['unit_price'].getAttribute('value').then((value) => {
          expect(value).toBe('500');
        });
      });
    });

    // DELETE Range Brackets Button
    describe('delete range brackets', () => {
      it('shouldn\'t be visible if a range is not selected', () => {
        page.deleteBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      });

      it('should be visible if a range is selected', () => {
        selectARange();

        page.deleteBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        });
      });

      it('should disappear if the reset button is clicked', () => {
        selectARange();

        page.deleteBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        });

        page.resetButtonEl.click();

        page.deleteBracketsButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should remove range upon clicking', () => {
        selectARange();

        page.deleteBracketsButtonEl.click().then(() => {
          popup();
          let name = page.formFields.unit_price.getAttribute('value');
          name.then((val) => {
            expect(val).toBe('');
          });
        });
      });
    });

    // TABLES
    describe('tables', () => {
      it('should filter should filter all from table', () => {

        page.textFilter.sendKeys('zzzz');

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeUndefined();
        });
      });

      it('should not show the brackets or towns table on load', () => {

        element(by.css('.brackets-table')).isDisplayed().then((value) => {
          expect(value).toBeFalsy;
        });

        element(by.css('.towns-table')).isDisplayed().then((value) => {
          expect(value).toBeFalsy;
        });
      });

      it('should show the brackets table when a schedule is selected', () => {

        element(by.css('.brackets-table')).isDisplayed().then((value) => {
          expect(value).toBeFalsy;
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();
        });

        element(by.css('.brackets-table')).isDisplayed().then((value) => {
          expect(value).toBeTruthy;
        });

      });

      it('should show the towns table when a schedule is selected', () => {

        element(by.css('.towns-table')).isDisplayed().then((value) => {
          expect(value).toBeFalsy;
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();
        });

        element(by.css('.towns-table')).isDisplayed().then((value) => {
          expect(value).toBeTruthy;
        });

      });

      it('should hide the brackets and towns table when the reset button is clicked', () => {

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();
        });

        element(by.css('.brackets-table')).isDisplayed().then((value) => {
          expect(value).toBeTruthy;
        });

        element(by.css('.towns-table')).isDisplayed().then((value) => {
          expect(value).toBeTruthy;
        });

        page.resetButtonEl.click();

        element(by.css('.brackets-table')).isDisplayed().then((value) => {
          expect(value).toBeFalsy;
        });

        element(by.css('.towns-table')).isDisplayed().then((value) => {
          expect(value).toBeFalsy;
        });

      });
    });

  }); //End suite

}); //End view
