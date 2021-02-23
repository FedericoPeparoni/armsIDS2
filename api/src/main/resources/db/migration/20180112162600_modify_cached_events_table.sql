-- alter cached events table with new retry column
ALTER TABLE cached_events
ADD COLUMN retry boolean NOT NULL DEFAULT true;
