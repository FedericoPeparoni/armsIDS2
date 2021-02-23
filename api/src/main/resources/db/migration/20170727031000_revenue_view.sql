-- drops and creates `revenue_view` to be used with revenue statistics
drop view if exists revenue_view;
create view revenue_view as (
	select bc.name,
	    acc.name account_name,
	    ad.aerodrome_name,
	    inv.invoice_period_or_date,
	    inv.amount_owing,
	    CASE WHEN amount_owing = 0 THEN 'T'
	        ELSE 'F'
	    END "is_paid",
		inv.payment_mode,
		scc.charge_class,
		scc.category,
		scc.type,
		scc.subtype,
		scc.description,
		ili.amount * ili.exchange_rate_to_usd usd,
		ili.amount * ili.exchange_rate_to_ansp ansp
    from abms.billing_centers bc,
		abms.aerodromes ad,
		abms.billing_ledgers inv,
		abms.invoice_line_items ili,
		abms.service_charge_catalogues scc,
		abms.accounts acc
	where bc. id = ad. billing_center_id
		and ad.id = ili.aerodrome_id
		and ili.service_charge_catalogue_id = scc.id
		and inv.account_id = acc.id
    union
	select bc.name,
        acc.name account_name,
        CASE WHEN fm.movement_type in ('INT_DEPARTURE', 'REG_DEPARTURE' ) THEN fm.dep_ad
             WHEN fm.movement_type in ( 'DOMESTIC', 'INT_ARRIVAL', 'REG ARRIVAL' ) THEN fm.dest_ad
             WHEN fm.movement_type in ( 'INT_OVERFLIGHT', 'REG_OVERFLIGHT' ) THEN ''
             ELSE 'ERROR'
        END "aerodrome_name",
        inv.invoice_period_or_date,
        inv.amount_owing,
        CASE WHEN amount_owing = 0 THEN 'T'
             ELSE 'F'
        END "is_paid",
        inv.payment_mode,
        'Aviation',
        'Domestic',
        'PAX',
        '',
        'Domestic PAX',
        fm.passengers_chargeable_domestic,
        fm.passengers_chargeable_domestic
    from abms.billing_centers bc right join abms.flight_movements fm on bc.id = fm.billing_center_id 
        inner join abms.accounts acc on acc.id = fm.account
        inner join abms.billing_ledgers inv on inv.account_id = acc.id
);

