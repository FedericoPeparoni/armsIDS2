update billing_centers set receipt_sequence_number=1 where receipt_sequence_number is null;
alter table billing_centers alter column receipt_sequence_number set not null;
alter table billing_centers alter column receipt_sequence_number set default(1);
