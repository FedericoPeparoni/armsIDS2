create table account_types
(
    id   serial primary key,
    name varchar(25) not null unique
);

insert into account_types (name) values ('Airline');
insert into account_types (name) values ('GeneralAviation');
insert into account_types (name) values ('Nonaviation');

create table types
(
    id   serial primary key,
    name varchar(25) not null unique
);

insert into types (name) values ('aviation');
insert into types (name) values ('non-aviation');
insert into types (name) values ('tenant');
insert into types (name) values ('commercial');
insert into types (name) values ('other');

create table accounts (
    id                                       serial primary key,
    name                                     varchar(100) not null unique,
    alias                                    varchar(100),
    aviation_billing_contact_person_name     varchar(100),
    aviation_billing_phone_number            varchar(100),
    aviation_billing_mailing_address         varchar(255),
    aviation_billing_email_address           varchar(255),
    aviation_billing_sms_number              varchar(100),
    non_aviation_billing_contact_person_name varchar(100),
    non_aviation_billing_phone_number        varchar(100),
    non_aviation_billing_mailing_address     varchar(255),
    non_aviation_billing_email_address       varchar(255),
    non_aviation_billing_sms_number          varchar(100),
    self_care_portal_user_name               varchar(100),
    self_care_portal_user_password           varchar(60),
    iata_code                                varchar(2) unique,
    icao_code                                varchar(3) unique,
    opr_identifier                           varchar(100),
    payment_terms                            int not null,
    discount_structure                       double precision not null,
    account_type                             int not null references account_types (id),
    tax_profile                              varchar(60),
    percentage_of_passenger_fee_payable      double precision,
    invoice_delivery_format                  varchar(100) not null,
    invoice_delivery_method                  varchar(60) not null,
    invoice_currency                         int not null references currencies(id),
    monthly_overdue_penalty_rate             double precision not null,
    notes                                    varchar(255),
    current_standing                         bool not null,
    black_listed_indicator                   bool not null,
    black_listed_override                    bool not null,
    credit_limit                             double precision not null,
    aircraft_parking_exemption               int not null,
    type                                     int not null references types (id),
    iata_member                              bool,
    separate_pax_invoice                     bool not null,
    external_accounting_system_identifier    varchar(25),
    active                                   bool not null,
    created_at timestamp    not null default now(),
    created_by varchar(50)  not null,
    updated_at timestamp,
    updated_by varchar(50)
);
