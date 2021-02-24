// a generic interface for use with predetermined list of data from the spec (to fill in selects on CRUD forms)
export interface IStaticType {
  id?: number;
  value?: any;
  name: string;
}
