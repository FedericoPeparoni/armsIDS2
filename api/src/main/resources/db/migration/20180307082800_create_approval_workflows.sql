create table approval_workflows (
    id                  serial primary key,
    level               int not null unique,
    approval_name       text not null unique,
    approval_group      int references roles (id),
    status_type         text not null,
    threshold_amount    double precision,
    threshold_currency  int references currencies(id),
    approval_under      int references approval_workflows(id),
    approval_over       int references approval_workflows(id),
    rejected            int references approval_workflows(id),
    delete              bool not null default false,
    created_at          timestamp    not null default now(),
    created_by          varchar(50)  not null,
    updated_at          timestamp,
    updated_by          varchar(50),
    version bigint NOT NULL DEFAULT 0,
    CONSTRAINT approval_workflows_ck1       CHECK  (status_type IN ('INITIAL', 'INTERMEDIATE', 'FINAL', 'APPROVED', 'DELETED'))
);

create index approval_workflows_level_i1
    on approval_workflows (level);
create index approval_workflows_approval_name_i1
    on approval_workflows (approval_name);
