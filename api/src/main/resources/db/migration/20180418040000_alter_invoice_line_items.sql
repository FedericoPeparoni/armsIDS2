DO $$
BEGIN
IF NOT EXISTS (SELECT constraint_name FROM information_schema.table_constraints WHERE table_name = 'invoice_line_items' AND constraint_type = 'PRIMARY KEY')
THEN
    -- set primary key so it can be used as a foreign key
    ALTER TABLE invoice_line_items
        ADD PRIMARY KEY (id);
END IF;
END
$$ LANGUAGE plpgsql;
