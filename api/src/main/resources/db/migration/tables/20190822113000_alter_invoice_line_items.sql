-- add user_description to invoice_line_items
ALTER TABLE invoice_line_items
    ADD COLUMN user_description CHARACTER VARYING(100);