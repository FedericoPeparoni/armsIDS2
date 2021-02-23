-- TFS 102146

alter table currencies
    add column exchange_rate_target_currency integer;

alter table currencies
    add constraint currencies_target_fk
        foreign key (exchange_rate_target_currency) references currencies (id);
    
update currencies set exchange_rate_target_currency = currency_id ('USD');
