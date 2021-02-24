'use strict';

describe('system-configuration view', () => {
  let page;
  let mock = require('protractor-http-mock');

  beforeEach(() => {
    browser.get('/#/system-configuration');
    page = require('./system-configuration.po');
    mock(['system-configuration/e2e/system-configuration.mock']);
  });

  afterEach(() => {
    mock.teardown();
  });

  describe('form', () => {

    function popup() {
      let EC = protractor.ExpectedConditions;
      let confirmButton = element(by.buttonText('Confirm'));
      browser.wait(EC.visibilityOf(confirmButton), 5000);
      confirmButton.click();
    }

    // UPDATE Button and Form Fields
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

      it('should update field when button is clicked', () => {

        page.currentValue.clear().then(() => {
            page.currentValue.sendKeys('test');
        })  

        page.currentValue.getAttribute('value').then((value) => {
          expect(value).toBe('test');
        });

        page.currentValue.clear().then(() => {
            page.currentValue.sendKeys('test2');
        });

        page.updateButtonEl.click();

        popup();

        browser.waitForAngular();

        page.currentValue.getAttribute('value').then((value) => {
          expect(value).toBe('test2');
        });

      });
    });

  }); //End suite
}); //End view