alter table navigation_billing_formulas RENAME COLUMN d_factor_formula TO domestic_d_factor_formula;
alter table navigation_billing_formulas add reg_dep_d_factor_formula varchar(255) not null default 'round(([CrossDist] / 1.852) / 100, 2)';
alter table navigation_billing_formulas add reg_arr_d_factor_formula varchar(255) not null default 'round(([CrossDist] / 1.852) / 100, 2)';
alter table navigation_billing_formulas add reg_ovr_d_factor_formula varchar(255) not null default 'round(([CrossDist] / 1.852) / 100, 2)';
alter table navigation_billing_formulas add int_dep_d_factor_formula varchar(255) not null default 'round(([CrossDist] / 1.852) / 100, 2)';
alter table navigation_billing_formulas add int_arr_d_factor_formula varchar(255) not null default 'round(([CrossDist] / 1.852) / 100, 2)';
alter table navigation_billing_formulas add int_ovr_d_factor_formula varchar(255) not null default 'round(([CrossDist] / 1.852) / 100, 2)';