'use strict';

describe('regional-country-management view', () => {
  var page;
  var mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/regional-country-management');
    page = require('./regional-country-management.po');
    mock(['regional-country-management/e2e/regional-country-management.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    // UPDATE Button
    describe('update', () => {

      it('should be visible', () => {
        page.updateButtonEl.isDisplayed().then((value) => {
          expect(value).toBeTruthy();
        });
      });

      it('should be enabled', () => {
        page.updateButtonEl.getAttribute('disabled').then((value) => {
          expect(value).toBeFalsy();
        });
      });

      it('should update when update button is clicked', () => {

        element(by.css('.dropdown-toggle.ng-binding.btn.btn-default')).getText().then((value) => {
            expect(value).toBe('1 CHECKED ');
        })

        element(by.css('.multiselect-parent')).click().then(() => { // click multiselect
          element(by.cssContainingText('a', 'Afghanistan')).click();
          element(by.css('.multiselect-parent')).click(); // click off multiselect
        });

        element(by.css('.dropdown-toggle.ng-binding.btn.btn-default')).getText().then((value) => {
            expect(value).toBe('2 CHECKED ');
        })

        page.updateButtonEl.click();

        element.all(by.repeater('item in regionalCountryList')).then((el) => {
          expect(el.length).toBe(1);
        });
      });
    });

    // TABLE Filter
    describe('filter', () => {

      it('should filter all from table', () => {

        page.textFilter.sendKeys('zzzz');

        element.all(by.repeater('item in regionalCountryList')).then((el) => {
          expect(el[0]).toBeUndefined();
        });
      });

    });

  }); //End suite

}); //End view