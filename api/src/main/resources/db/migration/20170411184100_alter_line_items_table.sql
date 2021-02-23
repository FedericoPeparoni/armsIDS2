alter table recurring_charges
    add constraint recurring_charges_pk
        primary key (id)
;
drop index recurring_charges_account_i1;
create index recurring_charges_account_i1 on recurring_charges (account_id);

--

alter table invoice_line_items
    add column recurring_charge_id integer
;

alter table invoice_line_items
    add constraint invoice_line_items_recurring_charge_fk
        foreign key (recurring_charge_id) references recurring_charges (id)
;

create index invoice_line_items_recurring_charge_i1 on invoice_line_items (recurring_charge_id);
