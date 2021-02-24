'use strict';

describe('sc-user-management view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-user-management');
        page = require('./sc-user-management.po');
        mock(['sc-user-management/e2e/sc-user-management.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
