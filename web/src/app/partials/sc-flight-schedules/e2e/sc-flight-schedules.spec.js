'use strict';

describe('sc-flight-schedules view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-flight-schedules');
        page = require('./sc-flight-schedules.po');
        mock(['sc-flight-schedules/e2e/sc-flight-schedules.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
