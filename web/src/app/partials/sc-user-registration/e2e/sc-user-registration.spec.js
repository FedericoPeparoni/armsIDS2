'use strict';

describe('sc-user-registration view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-user-registration');
        page = require('./sc-user-registration.po');
        mock(['sc-user-registration/e2e/sc-user-registration.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
