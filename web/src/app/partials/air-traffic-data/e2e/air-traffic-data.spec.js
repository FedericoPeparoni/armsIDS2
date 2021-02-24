'use strict';

describe('air-traffic-data view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/air-traffic-data');
        page = require('./air-traffic-data.po');
        mock(['air-traffic-data/e2e/air-traffic-data.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
