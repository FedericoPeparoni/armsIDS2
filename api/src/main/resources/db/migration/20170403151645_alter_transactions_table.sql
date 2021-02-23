alter table transactions add column receipt_number varchar(256);

create unique index transactions_receipt_number_i1 on transactions (receipt_number);
