export interface IAMHSConnection {
  id: number,
  active: boolean,
  descr: string,
  protocol: string,
  rtse_checkpoint_size: number,
  rtse_window_size: number,
  max_conn: number,
  ping_enabled: boolean,
  ping_delay: number,
  network_device: string,
  local_bind_authenticated: boolean,
  local_passwd: string,
  local_hostname: string,
  local_ipaddr: string,
  local_port: number,
  local_tsap_addr: string,
  local_tsap_addr_is_hex: boolean,
  remote_hostname: string,
  remote_ipaddr: string,
  remote_port: number,
  remote_passwd: string,
  remote_bind_authenticated: boolean,
  remote_class_extended: boolean,
  remote_content_corr: boolean,
  remote_dl_exp_prohibit: boolean,
  remote_rcpt_reass_prohibit: boolean,
  remote_idle_time: number,
  remote_internal_trace: boolean,
  remote_latest_delivery_time: boolean,
  remote_tsap_addr: string,
  remote_tsap_addr_is_hex: boolean
}

export interface IAmhsAgentStatus {
  installed: boolean,
  started: boolean
}
