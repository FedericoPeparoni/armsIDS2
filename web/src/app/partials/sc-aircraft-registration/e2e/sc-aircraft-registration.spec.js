'use strict';

describe('sc-aircraft-registration view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-aircraft-registration');
        page = require('./sc-aircraft-registration.po');
        mock(['sc-aircraft-registration/e2e/sc-aircraft-registration.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
