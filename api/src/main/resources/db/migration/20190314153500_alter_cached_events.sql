-- Add "retry_count" to cached_events table
alter table cached_events add column retry_count integer not null default 0;

-- Delete "successful" results (we shouldn't have any, but just in case)
delete from cached_event_results where not thrown;

-- Remove the "thrown" column because it's always implicitly true
alter table cached_event_results drop column thrown;

-- Set retry_count to the {count of results} - 1
update cached_events
   set retry_count = (
        select greatest (count(*) - 1, 0)
        from cached_event_results
        where cached_event_id = cached_events.id
   )
;

-- Set "retry" flag to false in cached events with too many results
with config as (
    select current_value::integer as max_retries
    from system_configurations
    where item_name = 'Cached event maximum retries'
    and current_value ~ '^\d{1,6}$'
)
update cached_events
   set retry = false
  from config
where retry_count > config.max_retries;


--
-- Delete all event results except last for each event. The following method is much faster than
-- a "normal" delete with where clauses etc.
--

-- First create a temporary table that is a copy of "cached_event_results",
-- but contains only the records that we want to keep, i.e., the ones with the largest
-- "sequence".
create temp table cached_event_results_temp as
    -- select evrything from subquery where "keep_rec" is true
    select * from (
        -- select everything from cached_event_results, plus an extra boolean column "keep_rec"
        -- that will be set to true only for the records whose <sequence> equals
        -- the max sequence of the same cached_event
        select *, (sequence = max (sequence) over (partition by cached_event_id)) as keep_rec
        from cached_event_results
    ) x
    where keep_rec;
-- drop the extra "keep_rec" column because it doesn't exist in the original table
alter table cached_event_results_temp drop column keep_rec;

-- quickly delete all records from original table
truncate cached_event_results;

-- (re-)insert everything from the temp table to the original table
insert into cached_event_results
select * from cached_event_results_temp;

-- drop the temp table
drop table cached_event_results_temp;
