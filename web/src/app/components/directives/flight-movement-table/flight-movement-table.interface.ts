export interface IExportTableOptions {
  boxHeader: number;
  endpoint: string;
}

export interface IFlightMovementTableAttributes extends angular.IAttributes {
  exportTableBoxHeader: string;
  exportTableEndpoint: string;
}
