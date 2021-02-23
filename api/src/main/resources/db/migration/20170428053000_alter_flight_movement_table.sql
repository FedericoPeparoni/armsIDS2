delete from flight_movements
where id in (select id
              from (select id,
                             ROW_NUMBER() OVER (partition by flight_id, date_of_flight, dep_time, dep_ad) as rnum
                     from flight_movements) t
              where t.rnum > 1);

alter table flight_movements add constraint unique_fields_fm unique (flight_id, date_of_flight, dep_time, dep_ad);