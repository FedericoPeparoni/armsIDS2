-- increase uploaded_files file_type to support xlsx
ALTER TABLE uploaded_files
    ALTER COLUMN file_type TYPE character varying (150)
