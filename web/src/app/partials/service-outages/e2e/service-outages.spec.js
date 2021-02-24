'use strict';

describe('service-outages view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/service-outages');
        page = require('./service-outages.po');
        mock(['service-outages/e2e/service-outages.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
