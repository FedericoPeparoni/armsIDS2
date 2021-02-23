--------------------------------------------------------------------
-- DO NOT ADD TO MIGRATION CHANGELOG, THIS WILL BE RUN MANUALLY!! --
--------------------------------------------------------------------

-- insert bank codes with billing centers and currencies
INSERT INTO bank_codes (code, description, account_number, branch_code, billing_center_id, currency_id) VALUES

    -- SSKIA bank codes
    ('6010', 'Barclays - SSKA Revenue Account', '1002208', '290267', (SELECT id FROM billing_centers WHERE name = 'SSKIA'), (SELECT id FROM currencies WHERE currency_code = 'BWP')),
    ('6016', 'Barclays SSKIA - ZAR Account', '1012478', '290667', (SELECT id FROM billing_centers WHERE name = 'SSKIA'), (SELECT id FROM currencies WHERE currency_code = 'ZAR')),
    ('6018', 'Barclays SSKIA - USD Account', '1014527', '290667', (SELECT id FROM billing_centers WHERE name = 'SSKIA'), (SELECT id FROM currencies WHERE currency_code = 'USD')),

    -- Selebi Phikwi bank codes
    ('6030', 'Barclays - S-Phikwe Revenue Account', '1009792', '290267', (SELECT id FROM billing_centers WHERE name = 'Selebi-Phikwe'), (SELECT id FROM currencies WHERE currency_code = 'BWP')),
    ('6035', 'Barclays Phikwe - USD Account', '1010211', '290667', (SELECT id FROM billing_centers WHERE name = 'Selebi-Phikwe'), (SELECT id FROM currencies WHERE currency_code = 'USD')),
    ('6036', 'Barclays Phikwe - ZAR Account', '1012672', '290667', (SELECT id FROM billing_centers WHERE name = 'Selebi-Phikwe'), (SELECT id FROM currencies WHERE currency_code = 'ZAR')),

    -- Francistown bank codes
    ('6050', 'Barclays - Francistown Revenue Account', '1000493', '290267', (SELECT id FROM billing_centers WHERE name = 'Francistown'), (SELECT id FROM currencies WHERE currency_code = 'BWP')),
    ('6055', 'Barclays FTown - USD Account', '1022139', '290667', (SELECT id FROM billing_centers WHERE name = 'Francistown'), (SELECT id FROM currencies WHERE currency_code = 'USD')),
    ('6056', 'Barclays FTown - ZAR Account', '1019472', '290667', (SELECT id FROM billing_centers WHERE name = 'Francistown'), (SELECT id FROM currencies WHERE currency_code = 'ZAR')),

    -- Maun bank codes
    ('6070', 'Barclays - Maun Revenue Account', '1008184', '290267', (SELECT id FROM billing_centers WHERE name = 'Maun'), (SELECT id FROM currencies WHERE currency_code = 'BWP')),
    ('6071', 'Barclays Maun - USD account', '1023356', '290667', (SELECT id FROM billing_centers WHERE name = 'Maun'), (SELECT id FROM currencies WHERE currency_code = 'USD')),
    -- double entry for Maun ZAR, will use first instead of 6077
    ('6073', 'Barclays Maun - ZAR account', '1020837', '290667', (SELECT id FROM billing_centers WHERE name = 'Maun'), (SELECT id FROM currencies WHERE currency_code = 'ZAR')),

    -- Kasane bank codes
    ('6093', 'Barclays Kasane - USd account', '1016317', '290667', (SELECT id FROM billing_centers WHERE name = 'Kasane'), (SELECT id FROM currencies WHERE currency_code = 'USD')),
    ('6098', 'Barclays Kasane - ZAR Account', '1018034', '290667', (SELECT id FROM billing_centers WHERE name = 'Kasane'), (SELECT id FROM currencies WHERE currency_code = 'ZAR')),
    ('6100', 'Barclays - Kasane Revenue Account', '1013633', '290267', (SELECT id FROM billing_centers WHERE name = 'Kasane'), (SELECT id FROM currencies WHERE currency_code = 'BWP')),

    -- Head Office bank codes
    ('6115', 'Barclays HO Revenue Account', '1002208', '290267', (SELECT id FROM billing_centers WHERE name = 'Head Office'), (SELECT id FROM currencies WHERE currency_code = 'BWP')),
    ('6116', 'Barclays Head office - USD Account', '1009981', '290667', (SELECT id FROM billing_centers WHERE name = 'Head Office'), (SELECT id FROM currencies WHERE currency_code = 'USD')),
    ('6117', 'Barclays Head office - ZAR Account', '1006990', '290667', (SELECT id FROM billing_centers WHERE name = 'Head Office'), (SELECT id FROM currencies WHERE currency_code = 'ZAR')),

    -- Ghanzi bank codes
    -- only uses BWP and not USD or ZAR
    ('6108', 'Barclays - Revenue Chantzi', '1004324', '290267', (SELECT id FROM billing_centers WHERE name = 'Ghanzi'), (SELECT id FROM currencies WHERE currency_code = 'BWP'));
