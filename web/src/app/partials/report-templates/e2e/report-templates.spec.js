'use strict';

describe('report-templates view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/report-templates');
        page = require('./report-templates.po');
        mock(['report-templates/e2e/report-templates.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
