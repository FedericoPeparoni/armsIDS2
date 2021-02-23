ALTER TABLE average_mtow_factors DROP CONSTRAINT average_mtow_factors_upper_limit_key;

ALTER TABLE average_mtow_factors ADD CONSTRAINT unique_upper_limit_key_factor_class UNIQUE (upper_limit, factor_class);
