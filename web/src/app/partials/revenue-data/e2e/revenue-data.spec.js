'use strict';

describe('revenue-data view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/revenue-data');
        page = require('./revenue-data.po');
        mock(['revenue-data/e2e/revenue-data.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
