'use strict';

describe('aviation-billing-engine view', () => {
  var page;
  var mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/aviation-billing-engine');
    page = require('./aviation-billing-engine.po');
    mock(['aviation-billing-engine/e2e/aviation-billing-engine.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('select dropdowns', () => {

    // check default selections
    describe('defaults', () => {
      it('should set defaults properly', () => {

        expect(element(by.id('account-type')).element(by.css('option:checked')).getText()).toEqual(
          'IATA');

        expect(element(by.id('month')).element(by.css('option:checked')).getText()).toEqual('January');

        element(by.id('year')).getAttribute('value').then((value) => {
          expect(value).toBe('2017');
        })

      });
    });

    // check all drop-down values populate
    describe('number of values', () => {
      it('should count the number of options for invoice type', function () {
        expect(element(by.id('account-type')).all(by.tagName('option')).count()).toBe(2);
      });

      it('should count the number of options for month', function () {
        expect(element(by.id('month')).all(by.tagName('option')).count()).toBe(12);
      });
    });


    // check that models update
    describe('updating models', () => {
      it('should update the invoice-type model upon changing selection', function () {
        element(by.id('account-type')).element(by.css('select option:nth-child(2)')).click();

        page.invoice.getAttribute('value').then((value) => {
          expect(value).toBe('non-iata');
        })
      });

      it('should update the month model upon changing selection', function () {
        element(by.id('month')).element(by.css('select option:nth-child(6)')).click();

        page.month.getAttribute('value').then((value) => {
          expect(value).toBe('6');
        })
      });

      it('should update the year model upon changing selection', function () {
        element(by.id('year')).clear().then(() => {
          element(by.id('year')).sendKeys('2015');
        })

        page.year.getAttribute('value').then((value) => {
          expect(value).toBe('2015');
        })
      });
    });

    // accounts
    describe('accounts', () => {
      it('should be displayed for both IATA and non-IATA', function () {

        element(by.id('accounts')).isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        });

        element(by.id('account-type')).element(by.css('select option:nth-child(2)')).click();

        element(by.id('accounts')).isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        });

      });

      it('should check that no default is selected', function () {

        element(by.id('account-type')).element(by.css('select option:nth-child(2)')).click();

        element(by.css('.dropdown-toggle.ng-binding.btn.btn-default')).getText().then((value) => {
          expect(value).toBe('SELECT ');
        })

      });

      it('should update the multiselect upon selection', function () {

        element(by.id('account-type')).element(by.css('select option:nth-child(2)')).click();

        element(by.css('.multiselect-parent')).click().then(() => { // click multiselect
          element(by.cssContainingText('a', 'Test Account')).click();
          element(by.css('.multiselect-parent')).click(); // click off multiselect
        });

        element(by.css('.dropdown-toggle.ng-binding.btn.btn-default')).getText().then((value) => {
          expect(value).toBe('1 CHECKED ');
        })

      });

    });

    // generate button
    describe('check generate button', () => {

      it('for IATA should be initially displayed', function () {

        element(by.css('.btn-generate-iata')).isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        })

      });

      it('for non-IATA should display after changing account-type', function () {

        element(by.id('account-type')).element(by.css('select option:nth-child(2)')).click();

        element(by.css('.btn-generate-non-iata')).isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        })

      });
    });

    // preview button
    describe('check preview button', () => {

      it('for IATA should be initially displayed', function () {

        element(by.css('.btn-preview-iata')).isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        })

      });

      it('for non-IATA should display after changing account-type', function () {

        element(by.id('account-type')).element(by.css('select option:nth-child(2)')).click();

        element(by.css('.btn-preview-non-iata')).isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        })

      });
    });

  }); //End suite

}); //End view
