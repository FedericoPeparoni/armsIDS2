'use strict';

describe('sc-flight-cost-calculation view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-flight-cost-calculation');
        page = require('./sc-flight-cost-calculation.po');
        mock(['sc-flight-cost-calculation/e2e/sc-flight-cost-calculation.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
