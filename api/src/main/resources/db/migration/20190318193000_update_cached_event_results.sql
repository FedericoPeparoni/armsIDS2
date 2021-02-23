-- set all cached event result sequence number to 0 due to migration script 20190314153500_alter_cached_events.sql
-- even though we only ever have one result, it should still be supported for list order
UPDATE cached_event_results SET sequence = 0;
