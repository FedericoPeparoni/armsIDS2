-- Change the length of flight_id from 7 to 10 characters
ALTER TABLE flight_movements ALTER COLUMN flight_id TYPE varchar(10);
