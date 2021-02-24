'use strict';

describe('report-generation view', () => {
    let page;
    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/report-generation');
        page = require('./report-generation.po');
        mock(['report-generation/e2e/report-generation.mock']);
        browser.waitForAngular();
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        function selectAccount() {
          element(by.css('.multiselect-parent')).click().then(() => { // click multiselect
            element(by.cssContainingText('a', 'Mock Account')).click();
            element(by.css('.multiselect-parent')).click(); // click off multiselect
          });
        }

        function ifEmpty() {
          for (let field = 0; field < page.formFields.length; fields++) {
            page.formFields[page.formFields[field]].getAttribute('value').then((value) => {
                expect(value).toBe('');
            });
           }
        }

        function fillAccountStatementReport() {
          page.formFields['report'].sendKeys('Account statement report');
          selectAccount();
          // page.formFields['account_new_page'].click();
        }

        function fillDebtorReport() {
          page.formFields['report'].sendKeys('Debtor report');
          page.formFields['overdue_interval'].sendKeys('60-90');
          page.formFields['invoices'].sendKeys('Overdue');
        }

        function fillRevenueLostToExemptionsReport() {
          page.formFields['report'].sendKeys('Revenue lost to exemptions report');
          selectAccount();
        }

        function fillSummarisedInvoiceTotalsReport() {
          page.formFields['report'].sendKeys('Summarised invoice totals report');
          selectAccount();
          page.formFields['summary_interval'].sendKeys('Monthly');
          // page.formFields['account_new_page'].click();
          // page.formFields['financial_year'].click();
        }

        function defaultDates() {
          let date = new Date();
          let StartDate = new Date(date.getUTCFullYear(), date.getUTCMonth() - 1, 1).toISOString().substring(0, 10);

          let y = StartDate.substring(0,4);
          let m = parseInt(StartDate.substring(5,7));

          let months=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
          let mm = months[m-1];
          //
          return mm + " " + y
        }

    // Account status report
    describe('Account status report', () => {
      let generateButton = element.all(by.css('.btn-download')).first();

      it('GENERATE and PREVIEW buttons should be present and enable', () => {
        page.formFields['report'].sendKeys('Account status report');

        expect(generateButton.isPresent()).toBeTruthy();
        expect(generateButton.getAttribute('disabled')).toBeFalsy();

        expect(page.previewButtonEl.isPresent()).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeFalsy();
      });

      it('correct fields should be visible when report is selected', () => {
        page.formFields['report'].sendKeys('Account status report');

        expect(page.formFields['report'].getAttribute('value')).toBe('accountStatusReport');
        expect(page.page.element(by.id('accounts')).isPresent()).toBeFalsy();
        expect(page.formFields['used_defined'].isPresent()).toBeFalsy();
        expect(page.formFields['used_undefined'].isPresent()).toBeFalsy();
        expect(page.formFields['overdue_interval'].isPresent()).toBeFalsy();
        expect(page.formFields['invoices'].isPresent()).toBeFalsy();
        expect(page.formFields['credit_type'].isPresent()).toBeTruthy();
        expect(page.formFields['summary_interval'].isPresent()).toBeFalsy();
        // expect(page.formFields['financial_year'].isPresent()).toBeFalsy();
        // expect(page.formFields['account_new_page'].isPresent()).toBeFalsy();
        expect(page.page.element(by.model('start.date')).isDisplayed()).toBeFalsy();
        expect(page.page.element(by.model('end.date')).isDisplayed()).toBeFalsy();
      });

      it('should clear fields when the clear button is clicked', () => {

        page.cleanButtonEl.click();
        ifEmpty();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });
    });

    // Account statement report
    describe('Account statement report', () => {
      let generateButton = element.all(by.css('.btn-download')).get(1);

      it('GENERATE and PREVIEW buttons should be present and disabled', () => {
        page.formFields['report'].sendKeys('Account statement report');

        expect(generateButton.isPresent()).toBeTruthy();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();

        expect(page.previewButtonEl.isPresent()).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });

      it('correct fields should be visible when report is selected', () => {
        page.formFields['report'].sendKeys('Account statement report');

        expect(page.formFields['report'].getAttribute('value')).toBe('accountStatementReport');
        expect(page.page.element(by.id('accounts')).isPresent()).toBeTruthy();
        expect(page.formFields['used_defined'].isPresent()).toBeFalsy();
        expect(page.formFields['used_undefined'].isPresent()).toBeFalsy();
        expect(page.formFields['overdue_interval'].isPresent()).toBeFalsy();
        expect(page.formFields['invoices'].isPresent()).toBeFalsy();
        expect(page.formFields['credit_type'].isPresent()).toBeFalsy();
        expect(page.formFields['summary_interval'].isPresent()).toBeFalsy();
        // expect(page.formFields['financial_year'].isPresent()).toBeFalsy();
        // expect(page.formFields['account_new_page'].isPresent()).toBeTruthy();
        expect(page.page.element(by.model('start.date')).isDisplayed()).toBeTruthy();
        expect(page.page.element(by.model('end.date')).isDisplayed()).toBeTruthy();
      });

      it('GENERATE and PREVIEW buttons should be enable when the form is filled', () => {
        fillAccountStatementReport();
        expect(generateButton.getAttribute('disabled')).toBeFalsy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeFalsy();
      });

      it('should clear fields when the clear button is clicked', () => {
        fillAccountStatementReport();
        expect(page.formFields['report'].getAttribute('value')).toBe('accountStatementReport');
        expect(page.page.element(by.id('accounts')).getText()).toBe('1 CHECKED ');
        // expect(page.formFields['account_new_page'].isSelected()).toBeTruthy();
        expect(page.page.element(by.model('start.date')).getAttribute('value')).toBe(defaultDates());
        expect(page.page.element(by.model('end.date')).getAttribute('value')).toBe(defaultDates());

        page.cleanButtonEl.click();
        ifEmpty();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });
    });

    // Debtor report
    describe('Debtor report', () => {
      let generateButton = element.all(by.css('.btn-download')).get(2);

      it('GENERATE and PREVIEW buttons should be present and disabled', () => {
        page.formFields['report'].sendKeys('Debtor report');

        expect(generateButton.isPresent()).toBeTruthy();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();

        expect(page.previewButtonEl.isPresent()).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });

      it('correct fields should be visible when report is selected', () => {
        page.formFields['report'].sendKeys('Debtor report');

        expect(page.formFields['report'].getAttribute('value')).toBe('debtorReport');
        expect(page.page.element(by.id('accounts')).isPresent()).toBeFalsy();
        expect(page.formFields['used_defined'].isPresent()).toBeFalsy();
        expect(page.formFields['used_undefined'].isPresent()).toBeFalsy();
        expect(page.formFields['overdue_interval'].isPresent()).toBeTruthy();
        expect(page.formFields['invoices'].isPresent()).toBeTruthy();
        expect(page.formFields['credit_type'].isPresent()).toBeFalsy();
        expect(page.formFields['summary_interval'].isPresent()).toBeFalsy();
        // expect(page.formFields['financial_year'].isPresent()).toBeFalsy();
        // expect(page.formFields['account_new_page'].isPresent()).toBeFalsy();
        expect(page.page.element(by.model('start.date')).isDisplayed()).toBeFalsy();
        expect(page.page.element(by.model('end.date')).isDisplayed()).toBeFalsy();
      });

      it('GENERATE and PREVIEW buttons should be enable when the form is filled', () => {
        fillDebtorReport();
        expect(generateButton.getAttribute('disabled')).toBeFalsy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeFalsy();
      });

      it('should clear fields when the clear button is clicked', () => {
        fillDebtorReport();
        expect(page.formFields['report'].getAttribute('value')).toBe('debtorReport');
        expect(page.formFields['overdue_interval'].getAttribute('value')).toBe('3');
        expect(page.formFields['invoices'].getAttribute('value')).toBe('true');

        page.cleanButtonEl.click();
        ifEmpty();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });
    });

     // Revenue lost to exemptions report
    describe('Revenue lost to exemptions report', () => {
      let generateButton = element.all(by.css('.btn-download')).get(3);

      it('GENERATE and PREVIEW buttons should be present and disabled', () => {
        page.formFields['report'].sendKeys('Revenue lost to exemptions report');

        expect(generateButton.isPresent()).toBeTruthy();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();

        expect(page.previewButtonEl.isPresent()).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });

      it('correct fields should be visible when report is selected', () => {
        page.formFields['report'].sendKeys('Revenue lost to exemptions report');

        expect(page.formFields['report'].getAttribute('value')).toBe('revenueLostToExemptionsReport');
        expect(page.page.element(by.id('accounts')).isPresent()).toBeTruthy();
        expect(page.formFields['used_defined'].isPresent()).toBeFalsy();
        expect(page.formFields['used_undefined'].isPresent()).toBeFalsy();
        expect(page.formFields['overdue_interval'].isPresent()).toBeFalsy();
        expect(page.formFields['invoices'].isPresent()).toBeFalsy();
        expect(page.formFields['credit_type'].isPresent()).toBeFalsy();
        expect(page.formFields['summary_interval'].isPresent()).toBeFalsy();
        // expect(page.formFields['financial_year'].isPresent()).toBeFalsy();
        // expect(page.formFields['account_new_page'].isPresent()).toBeFalsy();
        expect(page.page.element(by.model('start.date')).isDisplayed()).toBeTruthy();
        expect(page.page.element(by.model('end.date')).isDisplayed()).toBeTruthy();
      });

      it('GENERATE and PREVIEW buttons should be enable when the form is filled', () => {
        fillRevenueLostToExemptionsReport();
        expect(generateButton.getAttribute('disabled')).toBeFalsy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeFalsy();
      });

      it('should clear fields when the clear button is clicked', () => {
        fillRevenueLostToExemptionsReport();
        expect(page.formFields['report'].getAttribute('value')).toBe('revenueLostToExemptionsReport');
        expect(page.page.element(by.id('accounts')).getText()).toBe('1 CHECKED ');
        expect(page.page.element(by.model('start.date')).getAttribute('value')).toBe(defaultDates());
        expect(page.page.element(by.model('end.date')).getAttribute('value')).toBe(defaultDates());

        page.cleanButtonEl.click();
        ifEmpty();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });
    });

    // Summarised invoice totals report
    describe('Summarised invoice totals report', () => {
      let generateButton = element.all(by.css('.btn-download')).get(4);

      it('GENERATE and PREVIEW buttons should be present and disabled', () => {
        page.formFields['report'].sendKeys('Summarised invoice totals report');

        expect(generateButton.isPresent()).toBeTruthy();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();

        expect(page.previewButtonEl.isPresent()).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });

      it('correct fields should be visible when report is selected', () => {
        page.formFields['report'].sendKeys('Summarised invoice totals report');

        expect(page.formFields['report'].getAttribute('value')).toBe('summarisedInvoiceTotalsReport');
        expect(page.page.element(by.id('accounts')).isPresent()).toBeTruthy();
        expect(page.formFields['used_defined'].isPresent()).toBeFalsy();
        expect(page.formFields['used_undefined'].isPresent()).toBeFalsy();
        expect(page.formFields['overdue_interval'].isPresent()).toBeFalsy();
        expect(page.formFields['invoices'].isPresent()).toBeFalsy();
        expect(page.formFields['credit_type'].isPresent()).toBeFalsy();
        expect(page.formFields['summary_interval'].isPresent()).toBeTruthy();
        // expect(page.formFields['financial_year'].isPresent()).toBeTruthy();
        // expect(page.formFields['account_new_page'].isPresent()).toBeTruthy();
        expect(page.page.element(by.model('start.date')).isDisplayed()).toBeTruthy();
        expect(page.page.element(by.model('end.date')).isDisplayed()).toBeTruthy();
      });

      it('GENERATE and PREVIEW buttons should be enable when the form is filled', () => {
        fillSummarisedInvoiceTotalsReport();
        expect(generateButton.getAttribute('disabled')).toBeFalsy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeFalsy();
      });

      it('should clear fields when the clear button is clicked', () => {
        fillSummarisedInvoiceTotalsReport();
        expect(page.formFields['report'].getAttribute('value')).toBe('summarisedInvoiceTotalsReport');
        expect(page.page.element(by.id('accounts')).getText()).toBe('1 CHECKED ');
        expect(page.page.element(by.model('start.date')).getAttribute('value')).toBe(defaultDates());
        expect(page.page.element(by.model('end.date')).getAttribute('value')).toBe(defaultDates());
        // expect(page.formFields['account_new_page'].isSelected()).toBeTruthy();
        // expect(page.formFields['financial_year'].isSelected()).toBeTruthy();
        expect(page.formFields['summary_interval'].getAttribute('value')).toBe('2');

        page.cleanButtonEl.click();
        ifEmpty();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });
    });

    // Aircraft types report
    describe('Aircraft types report', () => {
      let generateButton = element.all(by.css('.btn-download')).get(5);

      it('GENERATE and PREVIEW buttons should be present and enable', () => {
        page.formFields['report'].sendKeys('Aircraft types report');
        expect(generateButton.isPresent()).toBeTruthy();
        expect(generateButton.getAttribute('disabled')).toBeFalsy();

        expect(page.previewButtonEl.isPresent()).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeFalsy();
      });

      it('correct fields should be visible when report is selected', () => {
        page.formFields['report'].sendKeys('Aircraft types report');

        expect(page.formFields['report'].getAttribute('value')).toBe('aircraftTypesReport');
        expect(page.page.element(by.id('accounts')).isPresent()).toBeFalsy();
        expect(page.formFields['used_defined'].isPresent()).toBeTruthy();
        expect(page.formFields['used_undefined'].isPresent()).toBeTruthy();
        expect(page.formFields['overdue_interval'].isPresent()).toBeFalsy();
        expect(page.formFields['invoices'].isPresent()).toBeFalsy();
        expect(page.formFields['credit_type'].isPresent()).toBeFalsy();
        expect(page.formFields['summary_interval'].isPresent()).toBeFalsy();
        // expect(page.formFields['financial_year'].isPresent()).toBeFalsy();
        // expect(page.formFields['account_new_page'].isPresent()).toBeFalsy();
        expect(page.page.element(by.model('start.date')).isDisplayed()).toBeTruthy();
        expect(page.page.element(by.model('end.date')).isDisplayed()).toBeTruthy();
      });

      it('should clear fields when the clear button is clicked', () => {

        page.cleanButtonEl.click();
        ifEmpty();
        expect(generateButton.getAttribute('disabled')).toBeTruthy();
        expect(page.previewButtonEl.getAttribute('disabled')).toBeTruthy();
      });
    });
 });
});
