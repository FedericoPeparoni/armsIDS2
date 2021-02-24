'use strict';

describe('aircraft-unspecified-management view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/aircraft-unspecified-management');
        page = require('./aircraft-unspecified-management.po');
        mock(['aircraft-unspecified-management/e2e/aircraft-unspecified-management.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
