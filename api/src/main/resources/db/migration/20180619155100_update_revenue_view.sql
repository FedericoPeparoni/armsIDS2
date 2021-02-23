drop view if exists revenue_view;

CREATE OR REPLACE VIEW revenue_view AS
 SELECT bc.name,
    acc.name AS account_name,
    ad.aerodrome_name,
    inv.invoice_period_or_date,
    inv.amount_owing,
        CASE
            WHEN inv.amount_owing = 0::double precision THEN 'T'::text
            ELSE 'F'::text
        END AS is_paid,
    inv.payment_mode,
    scc.charge_class,
    scc.category,
    scc.type,
    scc.subtype,
    scc.description,
    ili.amount * ili.exchange_rate_to_usd AS usd,
    ili.amount * ili.exchange_rate_to_ansp AS ansp
   FROM billing_centers bc,
    aerodromes ad,
    billing_ledgers inv,
    invoice_line_items ili,
    service_charge_catalogues scc,
    accounts acc
  WHERE bc.id = ad.billing_center_id AND ad.id = ili.aerodrome_id AND ili.service_charge_catalogue_id = scc.id AND inv.account_id = acc.id
UNION
 SELECT bc.name,
    acc.name AS account_name,
        CASE
            WHEN fm.movement_type::text = ANY (ARRAY['INT_DEPARTURE'::character varying::text, 'REG_DEPARTURE'::character varying::text]) THEN fm.dep_ad
            WHEN fm.movement_type::text = ANY (ARRAY['DOMESTIC'::character varying::text, 'INT_ARRIVAL'::character varying::text, 'REG_ARRIVAL'::character varying::text]) THEN fm.dest_ad
            WHEN fm.movement_type::text = ANY (ARRAY['INT_OVERFLIGHT'::character varying::text, 'REG_OVERFLIGHT'::character varying::text]) THEN ''::character varying
            ELSE 'ERROR'::character varying
        END AS aerodrome_name,
    inv.invoice_period_or_date,
    inv.amount_owing,
        CASE
            WHEN inv.amount_owing = 0::double precision THEN 'T'::text
            ELSE 'F'::text
        END AS is_paid,
    inv.payment_mode,
    'Aviation'::character varying AS charge_class,
    'Domestic'::character varying AS category,
    'PAX'::character varying AS type,
    ''::character varying AS subtype,
    'Domestic PAX'::character varying AS description,
    fm.passengers_chargeable_domestic AS usd,
    fm.passengers_chargeable_domestic AS ansp
   FROM billing_centers bc
     RIGHT JOIN flight_movements fm ON bc.id = fm.billing_center_id
     JOIN accounts acc ON acc.id = fm.account
     JOIN billing_ledgers inv ON inv.account_id = acc.id;
