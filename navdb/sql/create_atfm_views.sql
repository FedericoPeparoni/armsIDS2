-- vim:ts=2:sts=2:sw=2:et

----------------------------------------------------------
-- v_fpl_object
----------------------------------------------------------

create or replace view atfm.v_fpl_object as
select t1.*
from atfm.fpl_object as t1
left join atfm.fpl_object as t2
  on t1.spatia_catalogue_fpl_object_id = t2.spatia_catalogue_fpl_object_id 
        and t1.catalogue_date < t2.catalogue_date
where t2.spatia_catalogue_fpl_object_id is null and t1.catalogue_date is not null
order by t1.catalogue_date;
