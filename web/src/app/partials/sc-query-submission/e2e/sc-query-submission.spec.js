'use strict';

describe('sc-query-submission view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-query-submission');
        page = require('./sc-query-submission.po');
        mock(['sc-query-submission/e2e/sc-query-submission.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
