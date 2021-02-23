ALTER TABLE uploaded_files
    ADD COLUMN exception_type character varying;

ALTER TABLE uploaded_files
    ADD COLUMN exception_message character varying;

ALTER TABLE uploaded_files
    ADD COLUMN exception_trace character varying;
