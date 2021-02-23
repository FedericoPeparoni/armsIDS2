-- TFS 90189: add support for FIR_P airspace type

-- This is re-created down below
alter table airspaces
    drop constraint airspaces_airspace_type_check;

-- Change table definition
alter table airspaces
    -- remove length restriction from airspace_type
    alter column airspace_type type varchar,
    -- add "FIR_P" as a possible value
    add constraint airspaces_airspace_type_check
        check (airspace_type in ('TMA', 'FIR', 'FIR_P'))
;
