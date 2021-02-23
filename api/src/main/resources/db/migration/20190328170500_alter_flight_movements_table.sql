-- add thru flight boolean flag and leave existing records as null
-- US 104328: BUG - CAAB - better detection of duplicate tower movement when THRU PLAN is A-B-C-A and tower log is segment C-A or D-A
ALTER TABLE flight_movements
    ADD COLUMN thru_flight boolean;
