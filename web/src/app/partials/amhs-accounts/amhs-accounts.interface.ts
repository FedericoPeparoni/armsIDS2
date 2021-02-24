export interface IAMHSAccount {
  id: number,
  active: boolean,
  descr: string,
  addr: string,
  passwd: string,
  allow_mta_conn: boolean,
  svc_hold_for_delivery: boolean
}
