export interface IMtowType {
  id?: number;
  upper_limit: number;
  average_mtow_factor: number;
  factor_class: string;
}

export interface IMtowTypeSpring {
  content: Array<IMtowType>;
}
