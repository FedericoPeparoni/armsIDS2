-- US105875: modify country south sudan prefix from HS to HJ as per ICAO refer DOC 7910/170
UPDATE
	aerodrome_prefixes ap
SET
	aerodrome_prefix = 'HJ',
	updated_by = 'system',
	updated_at = NOW()
FROM
	countries c
WHERE
	c.id = ap.country_code
	AND c.country_code = 'SSD'
	AND ap.aerodrome_prefix = 'HS';
