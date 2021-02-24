'use strict';

describe('sc-invoices view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-invoices');
        page = require('./sc-invoices.po');
        mock(['sc-invoices/e2e/sc-invoices.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
