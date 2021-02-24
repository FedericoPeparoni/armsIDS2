'use strict';

describe('sc-transactions view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-transactions');
        page = require('./sc-transactions.po');
        mock(['sc-transactions/e2e/sc-transactions.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
