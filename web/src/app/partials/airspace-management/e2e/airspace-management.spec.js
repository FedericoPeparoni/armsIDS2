'use strict';

describe('airspace-management view', () => {
    let page;
    let mock = require('protractor-http-mock');

    beforeEach(() => {
        browser.get('/#/airspace-management');
        page = require('./airspace-management.po');
        mock(['airspace-management/e2e/airspace-management.mock']);
    });

    afterEach(() => {
        mock.teardown();
    });

    describe('form', () => {

        // RESET Button and Form Fields
        it('should clear field in "Select an Airspace" when the reset button is clicked', () => {

            element(by.cssContainingText('option', 'WBFC')).click();

            element(by.id('select-airspace')).getAttribute('value').then((value) => {
                expect(value).toBe('2');
            });

            page.resetButtonEl.click();

            element(by.id('select-airspace')).getAttribute('value').then((value) => {
                expect(value).toBe('?');
            });

        });


        // ADD Button
        describe('add', () => {
            it('shouldn\'t be possible to click when the airspace is not selected', () => {

                page.addButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeTruthy();
                });
            });

            it('should be enabled when airspace is selected', () => {
                element(by.cssContainingText('option', 'WBFC')).click();

                page.addButtonEl.getAttribute('disabled').then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should add an airspace when the add button is pressed', () => {
                element(by.cssContainingText('option', 'WBFC')).click();
                page.addButtonEl.click();

                browser.waitForAngular();
            });
        });

        // REMOVE Button
        describe('delete', () => {
            it('shouldn\'t be visible if an airspace is not selected', () => {
                page.removeButtonEl.isDisplayed().then((value) => {
                    expect(value).toBeFalsy();
                });
            });

            it('should be visible if a airspace is selected', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    page.removeButtonEl.isDisplayed().then((value) => {
                        expect(value).toBeTruthy();
                    });
                });
            });

            it('should be removed upon clicking remove', () => {
                element.all(by.repeater('item in list')).then((el) => {
                    el[0].click();

                    element.all(by.repeater('item in list')).getText().then(function(text) {
                        expect(text).toEqual(['WBFC FIR']);
                    });

                    page.removeButtonEl.click();

                    let name = element.all(by.repeater('item in list')).getAttribute('value');
                    name.then((val) => { expect(val).toEqual([null]); });
                });
            });
        });


        // TABLE Filter
        describe('filter', () => {
            it('should filter all from table if a letter is an input', () => {

                page.textFilter.sendKeys('zzzz');

                element.all(by.repeater('item in list')).then((el) => {
                    expect(el[0]).toBeUndefined();
                });
            });
        });

    }); //End suite

}); //End view
