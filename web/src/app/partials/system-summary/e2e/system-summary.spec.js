'use strict';

describe('system-summary view', () => {
  var page;
  var helpers;

  var mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/system-summary');
    page = require('./system-summary.po');
    mock(['system-summary/e2e/system-summary.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  // REFRESH Button
  describe('refresh', () => {

    it('should be visible', () => {
      page.refreshButtonEl.isDisplayed().then((value) => {
        expect(value).toBeTruthy();
      });
    });

    it('should be enabled', () => {
      page.refreshButtonEl.getAttribute('disabled').then((value) => {
        expect(value).toBeFalsy();
      });
    });

    it('should refresh on click', () => {
      page.refreshButtonEl.click();
      element.all(by.repeater('item in list')).then((el) => {
        expect(el[0]).toBeDefined();
      });
    });

  }); //End suite
}); //End view