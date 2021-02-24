'use strict';

describe('catalogue-service-charge view', () => {
    let page;

    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/catalogue-service-charge');
        page = require('./catalogue-service-charge.po');
        mock(['catalogue-service-charge/e2e/catalogue-service-charge.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
      mock.teardown();
    });

    describe('form', () => {

    function fillOutForm() {
      page.formFields['charge_class'].sendKeys('10');
      page.formFields['category'].sendKeys('1');
      page.formFields['type'].sendKeys('20');
      page.formFields['subtype'].sendKeys('5');
      page.formFields['description'].sendKeys('Test');
      page.formFields['minimum_amount'].sendKeys('1');
      page.formFields['maximum_amount'].sendKeys('85');
      page.formFields['amount'].sendKeys('30');
      page.formFields['charge_basis'].sendKeys('Fixed Price');
      page.formFields['invoice_category'].sendKeys('Lease');

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

      page.formFields['charge_class'].getAttribute('value').then((value) => {
        expect(value).toBe('10');
      });
      page.formFields['category'].getAttribute('value').then((value) => {
        expect(value).toBe('1');
      });
      page.formFields['type'].getAttribute('value').then((value) => {
        expect(value).toBe('20');
      });
      page.formFields['subtype'].getAttribute('value').then((value) => {
        expect(value).toBe('5');
      });
      page.formFields['description'].getAttribute('value').then((value) => {
        expect(value).toBe('Test');
      });
      page.formFields['minimum_amount'].getAttribute('value').then((value) => {
        expect(value).toBe('1');
      });
      page.formFields['maximum_amount'].getAttribute('value').then((value) => {
        expect(value).toBe('85');
      });
      page.formFields['amount'].getAttribute('value').then((value) => {
        expect(value).toBe('30');
      });
      page.formFields['charge_basis'].getAttribute('value').then((value) => {
        expect(value).toBe('string:fixed');
      });
      page.formFields['invoice_category'].getAttribute('value').then((value) => {
        expect(value).toBe('string:lease');
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

      it('should create a service charge catalogue when the form is submitted', () => {
        fillOutForm();
        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // UPDATE Button
    describe('update', () => {
      it('shouldn\'t be visible if a service charge catalogue is not selected', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should be visible if a service charge catalogue is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.updateButtonEl.isDisplayed().then((value) => {
            expect(value).toBeTruthy();
          });
        });
      });

      it('should be enabled if a service charge catalogue is selected', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();
          fillOutForm();

          page.updateButtonEl.getAttribute('disabled').then((value) => {
            expect(value).toBeFalsy();
          });
        });
      });

      it('should update when the service charge catalogue is updated', () => {
        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['type'].clear().then(() => {
            page.formFields['type'].sendKeys('TRTRT');
          });

          page.formFields['type'].getAttribute('value').then((value) => {
            expect(value).toBe('TRTRT');
          });

          page.formFields['type'].clear().then(() => {
            el[0].click();
            page.formFields['type'].sendKeys('Test');

          });

          page.updateButtonEl.click();

          popup();
          browser.waitForAngular();
        });

        element.all(by.repeater('item in list')).then((el) => {
          el[0].click();

          page.formFields['type'].getAttribute('value').then((value) => {
            expect(value).toBe('Test');
          });
        });
      });
    });

    // DELETE Button
    describe('delete', () => {
      it('shouldn\'t be visible if a service charge catalogue is not selected', () => {
        browser.waitForAngular();
        page.deleteButtonEl.isDisplayed().then((value) => {
          expect(value).toBeFalsy();
        })
      })

      it('should be visible if a service charge catalogue is selected', () => {
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
            let name = page.formFields.charge_class.getAttribute('value');
            name.then((val) => { expect(val).toBe(''); });
          });
        });
      });
    });

    // TABLE Filter
    describe('filter', () => {
      it('should filter all from table if a letter is an input', () => {

        page.textFilter.sendKeys('zzzz');

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeUndefined();
        });
      });
    });

  }); //End suite

}); //End view
