drop function if exists flight_movements_summary( varchar, int );

create function flight_movements_summary( _movement_type varchar,  _query_type int)
    returns table(
        total     bigint,
        val       bigint,
        percent   numeric
    ) as $$
declare

    _status varchar;
    _query varchar;
    _mov_typ_cond varchar;
    _where_cond varchar;
begin
	
	if (_query_type = 1) then
		_status := 'PENDING';
		
		_mov_typ_cond = 'and movement_type in (''' || _movement_type || ''')';
	    _where_cond = ' where movement_type in (''' || _movement_type || ''')';
		
		_query := 'select count(*) as total, 
	       (select count(*) from flight_movements where status = ''' || _status || '''  ' || _mov_typ_cond ||') as val,
	       ((select count(*) from flight_movements where status = ''' || _status || '''  ' || _mov_typ_cond ||') * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent
	    from flight_movements ' || _where_cond;
	 end if;
	 
	 if (_query_type = 2) then
        _mov_typ_cond = 'and movement_type in (''' || _movement_type || ''')';
        _where_cond = ' where movement_type in (''' || _movement_type || ''')';
        
        _query := 'select count(*) as total, 
           (select count(*) from flight_movements where movement_type in (''' || _movement_type || ''') ) as val, 
           ((select count(*) from flight_movements where movement_type in (''' || _movement_type || ''') ) * 100)::numeric / 
                ((select CASE WHEN count(*)=0 THEN 1 ELSE count(*) END from flight_movements  )) as percent
        from flight_movements ' || _where_cond;
     end if;
     
     if (_query_type = 3) then
        _mov_typ_cond = 'and movement_type in (''' || _movement_type || ''')';
        _where_cond = ' where movement_type in (''' || _movement_type || ''')';
        
        _query := 'select count(*) as total, 
           (select count(*) from flight_movements where movement_type in (''' || _movement_type || ''') and parking_time > 0 ) as val, 
           ((select count(*) from flight_movements where movement_type in (''' || _movement_type || ''') and parking_time > 0 ) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent
        from flight_movements ' || _where_cond;
     end if;
     
     if (_query_type = 4) then
     
        if (_movement_type = 'true') IS TRUE then
	        _query:= 'select count(*) as total,
                (select count(*) from abms.aircraft_types as ac full outer join abms.flight_movements as fm ON fm.aircraft_type = ac.aircraft_type where ac.aircraft_type is not null ) as val, 
                ((select count(*) from abms.aircraft_types as ac full outer join abms.flight_movements as fm ON fm.aircraft_type = ac.aircraft_type where ac.aircraft_type is not null ) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent
                from abms.aircraft_types as ac full outer join abms.flight_movements as fm ON fm.aircraft_type = ac.aircraft_type';
        else 
	        _query:= 'select count(*) as total,0::bigint as val,0::numeric as percent
                from abms.aircraft_types as ac full outer join abms.flight_movements as fm ON fm.aircraft_type = ac.aircraft_type
                where ac.aircraft_type is null';
        end if;
        
     end if;
     
     if (_query_type = 5) then
        _query:= 'select count(*) as total, 
        (select count(*) from accounts a1 where a1.black_listed_indicator is true) as resolved, 
        ((select count(*) from accounts a2 where a2.black_listed_indicator is true) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent
        from accounts a';
     end if;
     
     if (_query_type = 6) then
        _query:= 'select count(*) as total, 
        (select count(*) from flight_movements fm1, accounts a1 where fm1.account = a1.id and a1.black_listed_indicator is true) as resolved, 
        ((select count(*) from flight_movements fm2, accounts a2 where fm2.account = a2.id and a2.black_listed_indicator is true) * 100)::numeric / CASE count(*) WHEN 0 THEN 1 ELSE count(*) END as percent
        from flight_movements fm, accounts a
        where fm.account = a.id';
     end if;
     
     if (_query_type = 7) then
        _query := 'select count(distinct a.id) as total, 
        0::bigint as val, 
        0::numeric as percent
        from flight_movements fm, accounts a
        where fm.account = a.id and fm.movement_type in ('''|| _movement_type ||''') and a.active is true';
     end if; 
	
     if (_query_type = 8) then
        _query := 'select count(*) as total, 
        0::bigint as val, 
        0::numeric as percent
        from rejected_items ri
        where ri.record_type in ('''|| _movement_type ||''')';
     end if;
     
     if (_query_type = 9) then
        _query := 'select count(*) as total, 
        0::bigint as val, 
        0::numeric as percent
        from billing_ledgers bl        
        where bl.amount_owing > 0 AND bl.invoice_state_type_id = 3';
     end if;
     
     if (_query_type = 10) then
        _query := 'select count(*) as total, 
        0::bigint as val, 
        0::numeric as percent
        from billing_ledgers bl        
        where now() > bl.payment_due_date and bl.invoice_state_type_id != 4';
     end if;
     
	return query execute _query;
end;
$$ language 'plpgsql';
