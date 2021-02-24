'use strict';

describe('sc-inactivity-expiry-notice view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-inactivity-expiry-notice');
        page = require('./sc-inactivity-expiry-notice.po');
        mock(['sc-inactivity-expiry-notice/e2e/sc-inactivity-expiry-notice.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
