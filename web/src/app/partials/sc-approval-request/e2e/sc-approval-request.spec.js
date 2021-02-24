'use strict';

describe('sc-approval-request view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/sc-approval-request');
        page = require('./sc-approval-request.po');
        mock(['sc-approval-request/e2e/sc-approval-request.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
