-- View: abms.revenue_view_aviation

-- DROP VIEW abms.revenue_view_aviation;

drop view if exists abms.revenue_view_aviation;

create view abms.revenue_view_aviation
 as
SELECT bc.name,
    acc.name AS account_name,
	''::character varying AS aerodrome_name, 
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
    ROUND(cast(inv.invoice_amount * inv.invoice_exchange as numeric), 2) AS usd,
    ROUND(cast(inv.invoice_amount * inv.invoice_exchange_to_ansp as numeric), 2) AS ansp
FROM abms.billing_ledgers inv
    JOIN abms.accounts acc ON acc.id = inv.account_id
    JOIN abms.billing_centers bc ON bc.id = inv.billing_center_id
    WHERE inv.invoice_type LIKE 'aviation%'
;

