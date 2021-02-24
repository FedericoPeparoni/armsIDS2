'use strict';

describe('cached-events view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/cached-events');
        page = require('./cached-events.po');
        mock(['cached-events/e2e/cached-events.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
