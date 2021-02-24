import { HelperService } from './helpers.service';

describe('service helpers', () => {

  beforeEach(angular.mock.module('armsWeb'));

  it('should be registered', inject(() => {
    expect(HelperService).not.toEqual(null);
  }));

  describe('getRandomNumber', () => {
    it('random numbers generated should be between min and max or min and max', inject(() => {

      let minStart = HelperService.getRandomNumber(0, 99);
      let maxStart = HelperService.getRandomNumber(minStart + 1, 100); // the max value can be anywhere between the min and 100

      let min = minStart;
      let max = maxStart;
      let loops = 100;

      for (let i = 0; i < loops; i++) {
        let rand = HelperService.getRandomNumber(min, max);
        expect(rand).not.toBeLessThan(min);
        expect(rand).toBeLessThan(max + 1); // this is because it can be the "max" as well, so it can be this or less than
      }

    }));
  });

  describe('dateToISOString', () => {
    it('should strip off the milliseconds for an ISO string', inject(() => {
      let d = new Date();
      let string = d.toISOString();

      expect(string.match(/\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}\:\d{2}\.\d{3}Z/)).toBeTruthy(); // should be valid ISO string

      let newISO = HelperService.dateToISOString(d);

      expect(newISO.match(/\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}\:\d{2}/)).toBeTruthy();

    }));

    it('a forced string should return with the milliseconds removed', inject(() => {
      let string = '2016-05-27T14:09:44.123Z';

      let d = new Date(string);
      let isoString = HelperService.dateToISOString(d);

      expect(isoString).toBe('2016-05-27T14:09:44Z'); // should be ISO string minus the milliseconds

      // need to see if we can go back to a date object with this string and for it to have not changed
      let newDate = new Date(isoString);
      expect(newDate.getUTCFullYear()).toBe(2016);
      expect(newDate.getUTCMonth()).toBe(4);
      expect(newDate.getUTCDate()).toBe(27);
      expect(newDate.getUTCHours()).toBe(14);
      expect(newDate.getUTCMinutes()).toBe(9);
      expect(newDate.getUTCSeconds()).toBe(44);

    }));

  });

  describe('getStartEndStrings', () => {
    it('should return an ISO string with milliseconds', inject(() => {

      let date = new Date();

      date.setFullYear(2025);
      date.setMonth(11);
      date.setDate(25);
      date.setHours(5);
      date.setMinutes(44);
      date.setSeconds(12);

      let d = HelperService.getStartEndStrings(date);

      expect(d).toBe('2025-12-25T05:44:12Z');

    }));
  });

  describe('getRandomChar', () => {
    it('should be one character by default', inject(() => {
      let char = HelperService.getRandomChar();
      expect(char.length).toBe(1);
    }));

    it('setting a random number of characters, will give random same length back', inject(() => {
      let num = HelperService.getRandomNumber(0, 100);
      let char = HelperService.getRandomChar(num);
      expect(char.length).toBe(num);
    }));

    it('test to see if characters match', inject(() => {
      let char = HelperService.getRandomChar(50);
      expect(char.length).toBe(50);
    }));
  });

  describe('generateRandomHexColor', () => {

    const acceptableChars = '0123456789ABCDEF';

    it('should be registered', inject(() => {
      expect(HelperService.generateRandomHexColor).not.toEqual(null);
    }));

    it('should generate 6 digit hex string', inject(() => {
      let hex = HelperService.generateRandomHexColor();
      expect(hex.length).toBe(6);
    }));

    it('should only consist of hex characters', inject(() => {

      let hex = HelperService.generateRandomHexColor();

      for (let i = 0; i < hex.length; i++) {
        expect(acceptableChars.indexOf(hex[i])).toBeGreaterThan(-1);
      }
    }));

    it('should all be uppercase', inject(() => {
      let acceptableLetters = 'ABCDEF';
      let hex = HelperService.generateRandomHexColor();

      for (let i = 0; i < hex.length; i++) {
        if (acceptableLetters.indexOf(hex[i]) !== -1) {
          expect(hex[i] === hex[i].toUpperCase()).toBeTruthy();
        }
      }
    }));

  });

  describe('evenRound', () => {
    it('should round up when odd number', inject(() => {
      expect(HelperService.evenRound(1.5)).toBe(2);
    }));

    it('should round down when even number', inject(() => {
      expect(HelperService.evenRound(2.5)).toBe(2);
    }));

    it('should round to supplied decimal points', inject(() => {
      expect(HelperService.evenRound(1.115, 2)).toBe(1.12);
      expect(HelperService.evenRound(1.125, 2)).toBe(1.12);
    }));
  });


});
