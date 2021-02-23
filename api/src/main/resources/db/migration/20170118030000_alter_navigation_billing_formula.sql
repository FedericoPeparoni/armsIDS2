alter table navigation_billing_formulas alter column domestic_formula set not null;
alter table navigation_billing_formulas alter column regional_departure_formula set not null;
alter table navigation_billing_formulas alter column regional_arrival_formula set not null;
alter table navigation_billing_formulas alter column regional_overflight_formula set not null;
alter table navigation_billing_formulas alter column international_departure_formula set not null;
alter table navigation_billing_formulas alter column international_arrival_formula set not null;
alter table navigation_billing_formulas alter column international_overflight_formula set not null;
