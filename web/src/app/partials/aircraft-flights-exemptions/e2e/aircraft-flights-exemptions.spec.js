'use strict';

describe('aircraft-flights-exemptions view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aircraft-flights-exemptions');
        page = require('./aircraft-flights-exemptions.po');
        mock(['aircraft-flights-exemptions/e2e/aircraft-flights-exemptions.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
