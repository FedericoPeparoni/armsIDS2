UPDATE TABLE currency_exchange_rates set target_currency = currency_id('USD') where currency <> currency_id('VTU');
UPDATE TABLE currency_exchange_rates set target_currency = currency_id('VEF') where currency = currency_id('VTU');

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 107.00, '2013-02-06 00:00:00-04', '2014-02-18 00:00:00-04', currency_id('VEF'));

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 127.00, '2014-02-19 00:00:00-04', '2015-02-24 00:00:00-04', currency_id('VEF'));

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 150.00, '2015-02-25 00:00:00-04', '2016-02-10 00:00:00-04', currency_id('VEF'));

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 177.00, '2016-02-11 00:00:00-04', '2017-02-29 00:00:00-04', currency_id('VEF'));

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 300.00, '2017-03-01 00:00:00-04', '2018-02-28 00:00:00-04', currency_id('VEF'));

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 500.00, '2018-03-01 00:00:00-04', '2018-05-03 00:00:00-04', currency_id('VEF'));

INSERT INTO currency_exchange_rates (currency, exchange_rate, exchange_rate_valid_from_date, exchange_rate_valid_to_date, target_currency)
    values (currency_id('VTU'), 850.00, '2018-05-04 00:00:00-04', '', currency_id('VEF'));
