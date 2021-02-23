-- change KCAA AATIS to a site specific plugin
UPDATE plugins SET site = 'KCAA', visible = true
    WHERE key = 'kcaa.aatis';
