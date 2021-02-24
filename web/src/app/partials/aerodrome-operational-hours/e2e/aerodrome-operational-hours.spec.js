'use strict';

describe('aerodrome-operational-hours view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aerodrome-operational-hours');
        page = require('./aerodrome-operational-hours.po');
        mock(['aerodrome-operational-hours/e2e/aerodrome-operational-hours.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
