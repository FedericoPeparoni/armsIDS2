update billing_ledgers set invoice_type = 'aviation-iata'    where invoice_type <> 'non-aviation' and invoice_document_type like '%spreadsheet%';
update billing_ledgers set invoice_type = 'aviation-noniata' where invoice_type <> 'non-aviation' and invoice_document_type like '%pdf%';
