'use strict';

describe('sc-report-generation view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-report-generation');
        page = require('./sc-report-generation.po');
        mock(['sc-report-generation/e2e/sc-report-generation.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
