'use strict';

describe('login view', function () {
  var page;

  beforeEach(function () {
    browser.get('/?#/login');
    page = require('./login.po.js');
  });

  var submit = () => {
    return page.submitEl
      .click()
      .then(() => {
        return browser.waitForAngular();
      });
  };

  describe('logging in failed', () => {

    beforeEach(() => {
      page.usernameEl.sendKeys('foo');
      page.passwordEl.sendKeys('bar');
    });

    it('should happen with incorrect credentials', () => {
      submit().then(() => {
        expect(page.formEl.evaluate('warning')).toBeTruthy(); // warning should appear
      });
    });

    it('should have an incremental time delay after multiple failed logins', () => {

      expect(page.formEl.evaluate('timeoutDelay')).toBe(0); // starts as zero

      submit()
        .then(submit())
        .then(submit())
        .then(submit())
        .then(() => {
          expect(page.formEl.evaluate('timeoutDelay')).not.toBe(0); // timeout delay will be set
      })
    });
  });

  describe('logging in succesfully', () => {

    beforeEach(() => { // todo, will need a mock db setup at some point
      page.usernameEl.sendKeys('admin');
      page.passwordEl.sendKeys('admin');
    });

    it('should happen with the correct credentials', () => {
      submit().then(() => {
        expect(browser.getCurrentUrl()).toEqual(browser.baseUrl + '/?#/invoices');
      })
    })
  });

});
