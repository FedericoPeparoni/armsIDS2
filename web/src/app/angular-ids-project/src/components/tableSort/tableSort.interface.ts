export interface ISort {
  value: string;
  dir: string;
}

export interface ITableSortDirective extends ng.IScope {
  tableSort: (object: any) => void;
  disableSort: boolean;
  sortQueryString: () => string;
  sortQueryStringSuffix?: string;
  updateSortValue: (newValue: string, oldValue: string) => boolean;
  sortTable: () => void;
}
