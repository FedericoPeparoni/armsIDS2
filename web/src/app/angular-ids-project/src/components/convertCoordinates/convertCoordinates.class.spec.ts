import { Coordinates } from './convertCoordinates.class';

describe('class Coordinates', () => {

  it('should be registered', () => {
    const coordinates = new Coordinates();
    expect(coordinates).not.toEqual(null);
  });

  it('should convert a decimal latitude to DMS', () => {
    const coordinates = new Coordinates();
    const latitude = -12.3456;
    const latitudeDMS = coordinates.toLat(latitude, 'dms', 0);
    const result = '12 20 44S';
    expect(latitudeDMS).toEqual(result);
  });

  it('should convert a decimal longitude to DMS', () => {
    const coordinates = new Coordinates();
    const longitude = 12.3456;
    const longitudeDMS = coordinates.toLon(longitude, 'dms', 0);
    const result = '012 20 44E';
    expect(longitudeDMS).toEqual(result);
  });

  it('should convert DMS to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '012°20′44″E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '12.345555555555556';
    expect(longitudeInDecimals).toEqual(result);
  });

  it('should convert DMS to a decimal latitude', () => {
    const coordinates = new Coordinates();
    const latitudeDMS = '12°20′44″S';
    const latitudeInDecimals = coordinates.parseDMS(latitudeDMS);
    const result = '-12.345555555555556';
    expect(latitudeInDecimals).toEqual(result);
  });

  it('should convert DMS with spaces to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '012 20 44 E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '12.345555555555556';
    expect(longitudeInDecimals).toEqual(result);
  });

  it('should convert DMS with spaces to a decimal latitude', () => {
    const coordinates = new Coordinates();
    const latitudeDMS = '12 20 44 S';
    const latitudeInDecimals = coordinates.parseDMS(latitudeDMS);
    const result = '-12.345555555555556';
    expect(latitudeInDecimals).toEqual(result);
  });

  it('should convert DMS using quotation marks to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '012°20\'44\"E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '12.345555555555556';
    expect(longitudeInDecimals).toEqual(result);
  });

  it('should convert DMS using quotation marks to a decimal latitude', () => {
    const coordinates = new Coordinates();
    const latitudeDMS = '12°20\'44\"S';
    const latitudeInDecimals = coordinates.parseDMS(latitudeDMS);
    const result = '-12.345555555555556';
    expect(latitudeInDecimals).toEqual(result);
  });

  it('should convert DMS without spaces to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '1232044E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '123.34555555555555';
    expect(longitudeInDecimals).toEqual(result);
  });

  it('should convert DMS without spaces and with decimals, to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '1232044.55E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '123.34570833333333';
    expect(longitudeInDecimals).toEqual(result);
  });

  it('should convert DMS with one space to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '123 2044.55E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '123.34570833333333';
    expect(longitudeInDecimals).toEqual(result);
  });

  it('should convert DMS with two spaces to a decimal longitude', () => {
    const coordinates = new Coordinates();
    const longitudeDMS = '123 20 44.55E';
    const longitudeInDecimals = coordinates.parseDMS(longitudeDMS);
    const result = '123.34570833333333';
    expect(longitudeInDecimals).toEqual(result);
  });
});
