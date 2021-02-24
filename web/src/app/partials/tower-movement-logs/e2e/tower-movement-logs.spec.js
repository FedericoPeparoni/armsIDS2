'use strict';

describe('tower movement log view', () => {
    let page;
    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/tower-movement-logs');
        page = require('./tower-movement-logs.po');
        mock(['tower-movement-logs/e2e/tower-movement-logs.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function fillOutForm() {
            page.formFields['date_of_contact'].sendKeys('2017-Feb-11');
            page.formFields['flight_id'].sendKeys('100');
            page.formFields['registration'].sendKeys('AFMN');
            page.formFields['operator_name'].sendKeys('FXBR');
            page.formFields['departure_contact_time'].sendKeys('1234');
            page.formFields['destination_contact_time'].sendKeys('1234');
            page.formFields['route'].sendKeys('ABDGDGD');
            page.formFields['flight_level'].sendKeys('15000');
            page.formFields['flight_crew'].sendKeys('14');
            page.formFields['passengers'].sendKeys('200');
            page.formFields['flight_category'].sendKeys('Scheduled');
            page.formFields['day_of_flight'].sendKeys('2017-Feb-11');
            page.formFields['departure_time'].sendKeys('1234');
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

            page.formFields['date_of_contact'].getAttribute('value').then((value) => expect(value).toBe('2017-Feb-11'));
            page.formFields['flight_id'].getAttribute('value').then((value) => expect(value).toBe('100'));
            page.formFields['registration'].getAttribute('value').then((value) => expect(value).toBe('AFMN'));
            page.aircraftType.getAttribute('value').then((value) => expect(value[0]).toEqual('string:S123'));
            page.formFields['operator_name'].getAttribute('value').then((value) => expect(value).toBe('FXBR'));
            page.formFields['departure_contact_time'].getAttribute('value').then((value) => expect(value).toBe('1234'));
            page.depAerodrome.getAttribute('value').then((value) => expect(value[0]).toEqual('number:1'));
            page.destAerodrome.getAttribute('value').then((value) => expect(value[0]).toEqual('number:1'));
            page.formFields['destination_contact_time'].getAttribute('value').then((value) => expect(value).toBe('1234'));
            page.formFields['route'].getAttribute('value').then((value) => expect(value).toBe('ABDGDGD'));
            page.formFields['flight_level'].getAttribute('value').then((value) => expect(value).toBe('15000'));
            page.formFields['flight_crew'].getAttribute('value').then((value) => expect(value).toBe('14'));
            page.formFields['passengers'].getAttribute('value').then((value) => expect(value).toBe('200'));
            page.formFields['flight_category'].getAttribute('value').then((value) => expect(value).toBe('string:sch'));
            page.formFields['day_of_flight'].getAttribute('value').then((value) => expect(value).toBe('2017-Feb-11'));
            page.formFields['departure_time'].getAttribute('value').then((value) => expect(value).toBe('1234'));

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

            it('should create a tower movement log when the form is submitted', () => {
                fillOutForm();
                page.createButtonEl.click();
                browser.waitForAngular();
            });
        });

        // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a tower movement log is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a tower movement log is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a tower movement log is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the tower movement log is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['flight_id'].clear().then(() => {
            page.formFields['flight_id'].sendKeys('ABS');
          });

          page.formFields['flight_id'].getAttribute('value').then((value) => {
            expect(value).toBe('ABS');
          });

          page.formFields['flight_id'].clear().then(() => {
            el[0].click();
            page.formFields['flight_id'].sendKeys('100');
          });

          page.updateButtonEl.click();
          popup();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['flight_id'].getAttribute('value').then((value) => {
            expect(value).toBe('100');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a tower movement log is not selected', () => {
        browser.waitForAngular();
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      })

      it('should be visible if a tower movement log is selected', () => {
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
            let flightId = page.formFields.flight_id.getAttribute('value');
            flightId.then((val) => {
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
