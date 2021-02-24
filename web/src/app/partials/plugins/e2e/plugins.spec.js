'use strict';

describe('plugins view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/plugins');
        page = require('./plugins.po');
        mock(['plugins/e2e/plugins.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
