ALTER TABLE transactions ADD COLUMN target_currency_id integer;
ALTER TABLE transaction_payments ADD COLUMN target_currency_id integer;
ALTER TABLE billing_ledgers ADD COLUMN target_currency integer;

ALTER TABLE transactions  ADD  CONSTRAINT target_currency_id_fk FOREIGN KEY(target_currency_id)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE transaction_payments  ADD  CONSTRAINT target_currency_id_fk FOREIGN KEY(target_currency_id)
          REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE billing_ledgers  ADD  CONSTRAINT target_currency_fk FOREIGN KEY(target_currency)
	      REFERENCES currencies (id) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION;
	      
UPDATE transactions SET target_currency_id = currency_id('USD');
UPDATE transaction_payments SET target_currency_id = currency_id('USD');
UPDATE billing_ledgers SET target_currency = currency_id('USD');

ALTER TABLE transactions ALTER COLUMN target_currency_id SET not null;
ALTER TABLE transaction_payments ALTER COLUMN target_currency_id SET not null;
ALTER TABLE billing_ledgers ALTER COLUMN target_currency SET not null;

ALTER TABLE transactions RENAME COLUMN exchange_rate_to_usd TO exchange_rate;
ALTER TABLE transaction_payments RENAME COLUMN exchange_rate_to_usd TO exchange_rate;
ALTER TABLE billing_ledgers RENAME COLUMN invoice_exchange_to_usd TO invoice_exchange;