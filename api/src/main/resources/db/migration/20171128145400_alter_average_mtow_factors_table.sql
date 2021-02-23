ALTER TABLE average_mtow_factors ADD factor_class character varying(30);

UPDATE average_mtow_factors SET factor_class = 'DOMESTIC';

ALTER TABLE average_mtow_factors
    ALTER COLUMN  factor_class SET NOT NULL;
