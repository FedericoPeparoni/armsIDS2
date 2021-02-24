'use strict';

describe('non-aviation-billing-engine view', () => {
    var page;
    var helpers;

    var mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/non-aviation-billing-engine');
        page = require('./non-aviation-billing-engine.po');
        mock(['non-aviation-billing-engine/e2e/non-aviation-billing-engine.mock']);
    });

    describe('', () => {
        it('', () => {

        });
    });
});
