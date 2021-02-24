export interface ICoordinates {
  parseDMS: (dmsString: string) => number;
  toLat: (degrees: number, format: string, decimalPlaces: number) => string;
  toLon: (degrees: number, format: string, decimalPlaces: number) => string;
}