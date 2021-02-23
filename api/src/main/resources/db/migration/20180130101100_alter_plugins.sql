-- alter plugins table to add visibility column
ALTER TABLE plugins ADD COLUMN visible boolean NOT NULL DEFAULT true;

-- update plugins to hide prototype plugin by default
UPDATE plugins SET visible = false WHERE id = plugin_id('prototype');
