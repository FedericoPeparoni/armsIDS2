update currency_exchange_rates as cer
set exchange_rate_valid_to_date=('2100-1-1'::timestamp)
from currencies as c
where cer.currency = c.id and c.currency_code = 'USD';

insert into currency_exchange_rates (
            currency, exchange_rate_to_usd, exchange_rate_valid_from_date, 
            exchange_rate_valid_to_date, created_by)
select id, 1.0, now(), ('2100-1-1'::timestamp), 'system'
from currencies
where currency_code = 'USD' and not exists (select 1 from currency_exchange_rates as cer, currencies as c where cer.currency = c.id and c.currency_code = 'USD');