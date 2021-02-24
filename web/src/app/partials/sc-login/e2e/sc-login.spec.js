'use strict';

describe('sc-login view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-login');
        page = require('./sc-login.po');
        mock(['sc-login/e2e/sc-login.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
