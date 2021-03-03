-- vim:ts=2:sts=2:sw=2:et

\i config.sql

create    database :"navdb_name"
             owner :"navdb_owner"
          encoding :'navdb_encoding'
        lc_collate :'navdb_locale'
        lc_ctype   :'navdb_locale'
;

alter database :"navdb_name" set timezone to 'UTC';

