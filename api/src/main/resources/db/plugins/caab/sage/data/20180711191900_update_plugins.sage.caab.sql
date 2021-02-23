-- change CAAB Sage to a site specific plugin
UPDATE plugins SET site = 'CAAB', visible = true
    WHERE key = 'caab.sage';
