alter table roles add max_credit_note_amount_approval_limit double precision not null default(0.0);
alter table roles alter column max_credit_note_amount_approval_limit drop default;
alter table roles add max_debit_note_amount_approval_limit double precision not null default(0.0);
alter table roles alter column max_debit_note_amount_approval_limit drop default;
alter table roles add notification_mechanism varchar(50);
alter table roles add created_at timestamp not null default now();
alter table roles add created_by  varchar(50) not null default 'system';
alter table roles alter column created_by drop default;
alter table roles add updated_at timestamp;
alter table roles add updated_by varchar(50);

alter table permissions add created_at timestamp not null default now();
alter table permissions add created_by  varchar(50) not null default 'system';
alter table permissions alter column created_by drop default;
alter table permissions add updated_at timestamp;
alter table permissions add updated_by varchar(50);

alter table role_permission add created_at timestamp not null default now();
alter table role_permission add created_by  varchar(50) not null default 'system';
alter table role_permission alter column created_by drop default;
alter table role_permission add updated_at timestamp;
alter table role_permission add updated_by varchar(50);

alter table user_role add created_at timestamp not null default now();
alter table user_role add created_by  varchar(50) not null default 'system';
alter table user_role alter column created_by drop default;
alter table user_role add updated_at timestamp;
alter table user_role add updated_by varchar(50);

create table role_ownership (
    id                      serial primary key,
    parent_role_id          int       not null references roles (id),
    owned_role_id           int       not null references roles (id),
    created_at timestamp    not null default now(),
    created_by varchar(50)  not null,
    updated_at timestamp,
    updated_by varchar(50),
    unique (parent_role_id, owned_role_id)
);
