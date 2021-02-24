'use strict';

describe('interest-rates view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/interest-rates');
        page = require('./interest-rates.po');
        mock(['interest-rates/e2e/interest-rates.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
