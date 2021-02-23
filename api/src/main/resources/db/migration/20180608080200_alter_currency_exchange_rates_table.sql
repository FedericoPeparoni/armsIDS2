ALTER TABLE currency_exchange_rates RENAME COLUMN exchange_rate_to_usd TO exchange_rate;
ALTER TABLE currency_exchange_rates ADD COLUMN target_currency int references currencies (id);
UPDATE currency_exchange_rates SET target_currency = currency_id('USD');
ALTER TABLE currency_exchange_rates ALTER COLUMN target_currency SET not null;