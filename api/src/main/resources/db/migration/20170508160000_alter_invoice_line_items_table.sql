alter table invoice_line_items
    add column user_electricity_charge_type varchar(20),
    add constraint invoice_line_items_user_electricity_charge_type_ck
        check (user_electricity_charge_type in ('COMMERCIAL', 'RESIDENTIAL'))
;