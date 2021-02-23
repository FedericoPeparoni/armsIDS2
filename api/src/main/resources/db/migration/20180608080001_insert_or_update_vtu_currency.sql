-- Refactored/renamed from file 20180608080000_insert_vtu_currency.sql
do $$
begin
	if (select count(*) from currencies where currency_code = 'VTU') = 0 then
	   insert into currencies (currency_code, currency_name, country_code, decimal_places, symbol, active, created_by, allow_updated_from_web)
	       values (
	           'VTU',                    -- currency_code
	           'Venezuelan Tax Unit',    -- currency_name
	           country_id('Venezuela'),  -- country_code
	           2,                        -- decimal_places
	           'VTU',                    -- symbol
	           false,                    -- active
	           'system',                 -- created_by
	           false                     -- allow_updated_from_web
	       )
	   ;
	end if;
end $$;
