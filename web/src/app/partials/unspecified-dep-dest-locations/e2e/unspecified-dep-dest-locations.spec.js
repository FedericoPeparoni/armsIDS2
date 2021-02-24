'use strict';

describe('unspecified-dep-dest-locations view', () => {
    var page;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/unspecified-dep-dest-locations');
        page = require('./unspecified-dep-dest-locations.po');
        mock(['unspecified-dep-dest-locations/e2e/unspecified-dep-dest-locations.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
