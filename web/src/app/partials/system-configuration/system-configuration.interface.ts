export interface ISystemConfiguration {
  id: number;
  item_name: string;
  item_class: {
    id: number;
    name: string;
  };
  data_type: {
    id: number;
    name: string;
  };
  units: number;
  range: string;
  default_value: string;
  current_value: string | number;
  display_name?: string;
  client_storage_forbidden: boolean;
  system_validation_type: string;
  display_units: string;
}

export interface ISystemConfigurationSpring {
  content: Array<ISystemConfiguration>;
}

export interface ISystemConfigurationScope extends ng.IScope {
  list: Array<ISystemConfiguration>;
  textFilter: string;
  shouldBeFilteredCategory: Function;
  shouldBeFilteredItem: Function;
  current_cache: number;
  clearCache: Function;
  getCache: Function;
  crossing: ISystemConfiguration;
}
