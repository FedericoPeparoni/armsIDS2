/*
 * This script provides the missing range values for several numeric configuration items.
 * See also TFS 96576
 */

-- All of these can't be negative
update system_configurations
    set range = '0,maxint',
        -- correct current_value if necessary
        current_value = case
            -- if it's a number or null, keep it
            when current_value is null or current_value ~ '^\d+$' then
                current_value
            -- otherwise, if default_value is sane, use that
            when default_value ~ '^\d+$' then
                default_value
            -- otherwise, set it to null
            else
                null
            end
 where
    item_name in (
		'Training cost per day',
		'Exempt flights shorter than distance',
		'Maximum domestic crossing distance',
		'Maximum international crossing distance',
		'Maximum regional crossing distance',
		'Minimum domestic crossing distance',
		'Minimum international crossing distance',
		'Minimum regional crossing distance',
		'Default account credit limit',
		'Default account maximum credit note amount',
		'Default account minimum credit note amount',
		'Default account monthly penalty rate',
		'Default account parking exemption',
		'Default account payment terms'
	)
;

-- SMS server port
update system_configurations
    set range = '1,65535',
        default_value = null,
        current_value = case
            when current_value ~ '^\d{1,5}$' and current_value::integer between 1 and 65535 then
                current_value
            else
                null
            end
where
    item_name = 'SMS server port';
;

-- Email server port
update system_configurations
    set range = '1,65535',
        default_value = '587',
        current_value = case
            when current_value ~ '^\d{1,5}$' and current_value::integer between 1 and 65535 then
                current_value
            else
                null
            end
where
    item_name = 'Email server port';
;

-- Radar summary server port
update system_configurations
    set range = '1024,65535',
        default_value = null,
        current_value = case
            when current_value ~ '^\d{1,5}$' and current_value::integer between 1024 and 65535 then
                current_value
            else
                null
            end
where
    item_name = 'Radar summary port number';
;
