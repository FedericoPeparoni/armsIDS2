-----------------------------------------------------
-- X.400 gateway accounts (mts_users.conf)
-----------------------------------------------------
drop table if exists amhs_accounts;
create table amhs_accounts (
    id                    serial not null,                -- PK
    version               bigint not null default 0,      -- used by Hibernate
    active                boolean not null default true,
    addr                  varchar(200) not null,
    descr                 text not null,
    passwd                varchar(200) not null default 'default', -- P3 password
    allow_mta_conn        boolean not null default true,
    svc_hold_for_delivery boolean not null default true,
    created_at            timestamp with time zone NOT NULL DEFAULT now(),
    created_by            character varying(50) NOT NULL DEFAULT 'system',
    updated_at            timestamp with time zone,
    updated_by            character varying(50)
);

alter table amhs_accounts add
    constraint amhs_accounts_pk
        primary key (id)
;
alter table amhs_accounts add
    constraint amhs_accounts_ck_addr
        check (addr ~ '^[A-Z0-9/,=]{20,}$')
;
alter table amhs_accounts
    add constraint amhs_accounts_unique_addr unique (addr)
;

----------------------------------------------------
-- config data for x.400 agent
----------------------------------------------------
drop table if exists amhs_agent_config;
create table amhs_agent_config (
    json_data text not null             -- all stored as a single JSON object
);
-- unique: only one record can be active
create unique index amhs_agent_config_idx_unique
    on amhs_agent_config ((json_data is not null))
;

----------------------------------------------------
-- amhs_confiugurations
----------------------------------------------------
drop table if exists amhs_configurations;
create table amhs_configurations (
    id                              serial not null,
    
    version                         bigint not null default 0,
    created_at                      timestamp with time zone not null default now(),
    created_by                      character varying(50) not null default 'system',
    updated_at                      timestamp with time zone,
    updated_by                      character varying(50),
    
    active                          boolean not null default true,
    descr                           text,
    protocol                        varchar not null,
    rtse_checkpoint_size            integer not null,
    rtse_window_size                integer not null,
    max_conn                        integer not null,
    
    ping_enabled                    boolean not null default true,
    ping_delay                      integer,
    
    network_device                  varchar,
    
    local_bind_authenticated        boolean not null default false,
    local_passwd                    varchar not null,
    local_hostname                  varchar,
    local_ipaddr                    varchar,
    local_port                      integer not null,
    local_tsap_addr                 varchar not null,
    local_tsap_addr_is_hex          boolean not null default false,
    
    remote_hostname                 varchar not null,
    remote_ipaddr                   varchar,
    remote_port                     integer not null,
    remote_passwd                   varchar not null,
    remote_bind_authenticated       boolean not null default false,
    remote_class_extended           boolean not null default true,
    remote_content_corr             boolean not null default true,
    remote_dl_exp_prohibit          boolean not null default true,
    remote_rcpt_reass_prohibit      boolean not null default true,
    remote_idle_time                integer not null default 120,
    remote_internal_trace           boolean not null default true,
    remote_latest_delivery_time     boolean not null default true,
    remote_tsap_addr                varchar not null,
    remote_tsap_addr_is_hex         boolean not null default false
    
);

-- pk
alter table amhs_configurations add
    constraint amhs_configurations_pk
        primary key (id)
;
-- unique: only one record can be active
create unique index amhs_configurations_idx_active
    on amhs_configurations (active)
    where active
;
-- check
alter table amhs_configurations add
    constraint amhs_configurations_ck_protocol
        check (protocol in ('P1', 'P3'))
;
-- check: local_*
alter table amhs_configurations add
    constraint amhs_configurations_ck_local_port
        check (local_port between 1 and 65535)
;

-- check: remote_*
alter table amhs_configurations add
    constraint amhs_configurations_ck_remote_port
        check (remote_port between 1 and 65535)
;

