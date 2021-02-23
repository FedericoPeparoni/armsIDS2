-- change default value for separated sequence numbers field 

ALTER TABLE billing_centers
    ALTER COLUMN iata_invoice_sequence_number SET DEFAULT 1;
    
ALTER TABLE billing_centers
    ALTER COLUMN receipt_cheque_sequence_number SET DEFAULT 1;
    
ALTER TABLE billing_centers
    ALTER COLUMN receipt_wire_sequence_number SET DEFAULT 1;
