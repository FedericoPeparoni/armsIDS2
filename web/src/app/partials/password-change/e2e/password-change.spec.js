'use strict';

describe('password-change view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/password-change');
        page = require('./password-change.po');
        mock(['password-change/e2e/password-change.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
