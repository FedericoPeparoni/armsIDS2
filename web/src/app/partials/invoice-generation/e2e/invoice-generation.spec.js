'use strict';

describe('invoice-generation view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/invoice-generation');
        page = require('./invoice-generation.po');
        mock(['invoice-generation/e2e/invoice-generation.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
