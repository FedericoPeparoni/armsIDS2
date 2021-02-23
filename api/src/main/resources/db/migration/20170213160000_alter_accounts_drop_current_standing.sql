-- Alter table `accounts`, drop column `current_standing`
-- `current_standing` column is no longer used and not part of the spec from Feb 03/2017
alter table accounts drop column if exists current_standing;
