DO $$
BEGIN
IF NOT EXISTS (SELECT * FROM currencies WHERE currency_code = 'VES')
THEN
	UPDATE currencies SET currency_name = format ('%s (%s)', currencies.currency_name, currencies.currency_code)
	WHERE currency_name = 'Venezuelan Bolivar Soberano';

    -- insert new main currency of Venezuela since 20 August 2018
	INSERT INTO currencies (currency_code, currency_name, country_code, decimal_places, symbol, active, created_by, allow_updated_from_web)
	VALUES (
	   'VES',                    		    -- currency_code
	   'Venezuelan Bolivar Soberano',    	-- currency_name
	   country_id('Venezuela'),  		    -- country_code
	   2,                        		    -- decimal_places
	   'Bs.S.',                    		    -- symbol
	   false,                     		    -- active
	   'system',                 		    -- created_by
	   false                     		    -- allow_updated_from_web
    );
END IF;
END
$$ LANGUAGE plpgsql;
