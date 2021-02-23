ALTER TABLE system_item_types
    ADD COLUMN plugin_id integer;

ALTER TABLE system_item_types
    ADD CONSTRAINT system_item_types_plugins_fkey FOREIGN KEY (plugin_id)
        REFERENCES plugins (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE;
