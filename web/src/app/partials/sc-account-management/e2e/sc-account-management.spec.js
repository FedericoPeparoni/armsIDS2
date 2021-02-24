'use strict';

describe('sc-account-management view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-account-management');
        page = require('./sc-account-management.po');
        mock(['sc-account-management/e2e/sc-account-management.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
