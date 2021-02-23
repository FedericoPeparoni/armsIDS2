alter table airspaces alter column id drop default;
alter sequence airspaces_id_seq owned by none;
drop sequence airspaces_id_seq;