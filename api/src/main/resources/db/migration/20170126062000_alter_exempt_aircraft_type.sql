alter table aircraft_type_exemptions rename column enroute to enroute_fees_exempt;
alter table aircraft_type_exemptions rename column approach to approach_fees_exempt;
alter table aircraft_type_exemptions rename column aerodrome to aerodrome_fees_exempt;
alter table aircraft_type_exemptions rename column late_arrival to late_arrival_fees_exempt;
alter table aircraft_type_exemptions rename column late_departure to late_departure_fees_exempt;
alter table aircraft_type_exemptions rename column parking to parking_fees_exempt;
alter table aircraft_type_exemptions rename column adult to adult_fees_exempt;
alter table aircraft_type_exemptions rename column child to child_fees_exempt;