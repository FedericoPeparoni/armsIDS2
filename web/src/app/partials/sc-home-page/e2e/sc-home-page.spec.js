'use strict';

describe('sc-home-page view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-home-page');
        page = require('./sc-home-page.po');
        mock(['sc-home-page/e2e/sc-home-page.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
