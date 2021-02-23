alter table invoice_overdue_penalties alter column penalty_added_to_invoice_id drop not null;
alter table invoice_overdue_penalties alter column penalty_applied_date drop not null;
