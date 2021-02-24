'use strict';

describe('application-management view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/application-management');
        page = require('./application-management.po');
        mock(['application-management/e2e/application-management.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
