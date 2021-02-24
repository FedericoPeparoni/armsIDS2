 'use strict';

 describe('group-management view', () => {
     var page;
     var helpers;

     var mock = require('protractor-http-mock');

     beforeEach(() => {
         browser.get('/#/group-management');
         page = require('./group-management.po');
         mock(['group-management/e2e/group-management.mock']);
     });

     describe('', () => {
         it('', () => {

         });
     });
 });
