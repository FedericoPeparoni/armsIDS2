'use strict';

describe('flight-route-exemptions view', () => {
  let page;
  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/flight-route-exemptions');
    page = require('./flight-route-exemptions.po');
    mock(['flight-route-exemptions/e2e/flight-route-exemptions.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      element(by.model('departure_aerodrome_id')).sendKeys('1');
      element(by.model('destination_aerodrome_id')).sendKeys('2');
      element(by.model('exemption_in_either_direction')).sendKeys('true');
      element(by.model('enroute_fees_are_exempt')).sendKeys('true');
      element(by.model('approach_fees_are_exempt')).sendKeys('true');
      element(by.model('aerodrome_fees_are_exempt')).sendKeys('true');
      element(by.model('late_arrival_fees_are_exempt')).sendKeys('true');
      element(by.model('late_departure_fees_are_exempt')).sendKeys('true');
      element(by.model('parking_fees_are_exempt')).sendKeys('true');
      element(by.model('adult_passenger_fees_are_exempt')).sendKeys('true');
      element(by.model('child_passenger_fees_are_exempt')).sendKeys('true');
      page.formFields['flight_notes'].sendKeys('notes');
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

      page.formFields['departure_aerodrome_id'].getAttribute('value').then((value) => expect(value).toBe('1'));
      page.formFields['destination_aerodrome_id'].getAttribute('value').then((value) => expect(value).toBe('2'));
      page.formFields['exemption_in_either_direction'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['enroute_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['approach_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['aerodrome_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['late_arrival_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['late_departure_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['parking_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['adult_passenger_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['child_passenger_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
      page.formFields['flight_notes'].getAttribute('value').then((value) => expect(value).toBe('notes'));

      page.resetButtonEl.click();

      for (let field = 0; field < page.formFields.length; fields++) {
        page.formFields[page.formFields[field]].getAttribute('value').then((value) => expect(value).toBe(''));
      };
    })

    // CREATE Button
    describe('create', () => {
      it('shouldn\'t be possible to click when the form is empty', () => {
        page.createButtonEl.getAttribute('disabled').then((value) => expect(value).toBeTruthy());
      });

      it('should be enabled when all fields are filled out', () => {
        fillOutForm();
        page.createButtonEl.getAttribute('disabled').then((value) => expect(value).toBeFalsy());
      });

      it('should create a flight route exemption when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a flight route exemption is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a flight route exemption is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a flight route exemption is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();
          fillOutForm();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the flight route exemption is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['departure_aerodrome_id'].clear().then(() => page.formFields['departure_aerodrome_id'].sendKeys('1'));
          page.formFields['departure_aerodrome_id'].getAttribute('value').then((value) => expect(value).toBe('1'));
          page.formFields['destination_aerodrome_id'].clear().then(() => page.formFields['destination_aerodrome_id'].sendKeys('2'));
          page.formFields['destination_aerodrome_id'].getAttribute('value').then((value) => expect(value).toBe('2'));
          page.formFields['exemption_in_either_direction'].clear().then(() => page.formFields['exemption_in_either_direction'].sendKeys('true'));
          page.formFields['exemption_in_either_direction'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['enroute_fees_are_exempt'].clear().then(() => page.formFields['enroute_fees_are_exempt'].sendKeys('true'));
          page.formFields['enroute_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['approach_fees_are_exempt'].clear().then(() => page.formFields['approach_fees_are_exempt'].sendKeys('true'));
          page.formFields['approach_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['aerodrome_fees_are_exempt'].clear().then(() => page.formFields['aerodrome_fees_are_exempt'].sendKeys('true'));
          page.formFields['aerodrome_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['late_arrival_fees_are_exempt'].clear().then(() => page.formFields['late_arrival_fees_are_exempt'].sendKeys('true'));
          page.formFields['late_arrival_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['late_departure_fees_are_exempt'].clear().then(() => page.formFields['late_departure_fees_are_exempt'].sendKeys('true'));
          page.formFields['late_departure_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['parking_fees_are_exempt'].clear().then(() => page.formFields['parking_fees_are_exempt'].sendKeys('true'));
          page.formFields['parking_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['adult_passenger_fees_are_exempt'].clear().then(() => page.formFields['adult_passenger_fees_are_exempt'].sendKeys('true'));
          page.formFields['adult_passenger_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['child_passenger_fees_are_exempt'].clear().then(() => page.formFields['xxxxxxxxxxxxxx'].sendKeys('true'));
          page.formFields['child_passenger_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['flight_notes'].clear().then(() => page.formFields['flight_notes'].sendKeys('notes'));
          page.formFields['flight_notes'].getAttribute('value').then((value) => expect(value).toBe('notes'));
          page.formFields['flight_notes'].clear().then(() => {
            el[0].click();
            page.formFields['flight_notes'].sendKeys('edited notes');
          });

          page.updateButtonEl.click();

          popup();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['departure_aerodrome_id'].getAttribute('value').then((value) => expect(value).toBe('1'));
          page.formFields['destination_aerodrome_id'].getAttribute('value').then((value) => expect(value).toBe('2'));
          page.formFields['exemption_in_either_direction'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['enroute_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['approach_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['aerodrome_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['late_arrival_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['late_departure_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['parking_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['adult_passenger_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['child_passenger_fees_are_exempt'].getAttribute('value').then((value) => expect(value).toBe('true'));
          page.formFields['flight_notes'].getAttribute('value').then((value) => expect(value).toBe('edited notes'));
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if aflight route exemption is not selected', () => {
        page.deleteButtonEl.isDisplayed().then((value) => expect(value).toBeFalsy())
      })

      it('should be visible if a flight route exemption is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();
          page.deleteButtonEl.isDisplayed().then((value) => expect(value).toBeTruthy());
        });
      });

      it('should be removed upon clicking delete', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.deleteButtonEl.click().then((value) => {
            popup();
            let name = page.formFields.departure_aerodrome_id.getAttribute('value');
            name.then((val) => { expect(val).toBe(''); });
          });
        });
      });
    });

    // TABLE Filter
    describe('filter', () => {
      it('should filter all from table if a letter is an input', () => {
        page.textFilter.sendKeys('zzzz');
        element.all(by.repeater('item in list')).then((el) => expect(el[0]).toBeUndefined());
      });
    });
  })
});
