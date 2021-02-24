'use strict';

describe('invoices view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/invoices');
        page = require('./invoices.po');
        mock(['invoices/e2e/invoices.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
