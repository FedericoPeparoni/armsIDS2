'use strict';

describe('sc-flight-search view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-flight-search');
        page = require('./sc-flight-search.po');
        mock(['sc-flight-search/e2e/sc-flight-search.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
