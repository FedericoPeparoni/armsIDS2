'use strict';

describe('transactions view', () => {
  let page;
  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/transactions');
    page = require('./transactions.po');
    mock(['transactions/e2e/transactions.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function fillOutForm() {
      page.formFields['description'].sendKeys('test');
      page.formFields['currency'].sendKeys('AFN');
      page.formFields['amount'].sendKeys('1000.01');

      let selectTransactionType = page.transactionType.get(2);
      selectTransactionType.click();

      let selectAccount = page.account.get(1);
      selectAccount.click();
    }

    // RESET Button and Form Fields
    describe('fields', () => {
      it('should clear fields when the reset button is clicked', () => {
        fillOutForm();

        page.account.getAttribute('value').then((value) => {
          expect(value[0]).toBe('1');
        });
        page.formFields['description'].getAttribute('value').then((value) => {
          expect(value).toBe('test');
        });
        page.transactionType.getAttribute('value').then((value) => {
          expect(value[1]).toBe('2');
        });
        page.formFields['currency'].getAttribute('value').then((value) => {
          expect(value).toBe('AFN');
        });
        page.formFields['amount'].getAttribute('value').then((value) => {
          expect(value).toBe('1000.01');
        });
        page.formFields['payment_mechanism'].getAttribute('value').then((value) => {
          expect(value).toBe('string:adjustment');
        });
        page.formFields['payment_reference_number'].getAttribute('value').then((value) => {
          expect(value).toBe('n/a');
        });

        page.resetButtonEl.click();

        for (let field = 0; field < page.formFields.length; fields++) {
          page.formFields[page.formFields[field]].getAttribute('value').then((value) => {
            expect(value).toBe('');
          });
        }
      });

      it('should automatically fill reference number with n/a when payment mechanism is cash', () => {

        page.formFields['payment_mechanism'].sendKeys('Cash');

        page.formFields['payment_reference_number'].getAttribute('value').then((value) => {
          expect(value).toBe('n/a');
        });
      });

      it('should disable payment reference number when payment mechanism is cash', () => {

        page.formFields['payment_mechanism'].sendKeys('Cash');

        page.formFields['payment_reference_number'].getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        });
      });

      it('should fill payment mechanism and payment reference number when debit is selected', () => {

        let selectTransactionType = page.transactionType.get(2);
        selectTransactionType.click();

        page.formFields['payment_mechanism'].getAttribute('value').then((value) => {
          expect(value).toBe('string:adjustment');
        });

        page.formFields['payment_reference_number'].getAttribute('value').then((value) => {
          expect(value).toBe('n/a');
        });
      });

      it('should disable payment mechanism and payment reference number when debit is selected', () => {

        let selectTransactionType = page.transactionType.get(2);
        selectTransactionType.click();

        page.formFields['payment_mechanism'].getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        });

        page.formFields['payment_reference_number'].getAttribute('disabled').then((value) => {
          expect(value).toBeTruthy();
        });
      });
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

      it('should create a transaction when the form is submitted', () => {
        fillOutForm();

        page.createButtonEl.click();
        browser.waitForAngular();
      });
    });

    // INVOICES List
    describe('invoices', () => {
      it('should show a list of invoices', () => {
        fillOutForm();

        let selectTransactionType = page.transactionType.get(1);
        selectTransactionType.click();

        page.createButtonEl.click();

        element.all(by.repeater('item in invoices')).then((el) => {
          expect(el).toBeDefined();
        });

      });
    });

    // TABLE Filter
    describe('filter', () => {
      it('should filter all from table', () => {

        page.textFilter.sendKeys('zzzzzz');

        element.all(by.repeater('item in list')).then((el) => {
          expect(el[0]).toBeUndefined();
        });
      });
    });

  }); //End suite

}); //End view
