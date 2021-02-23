-- remove circular route caches where route is 'DCT'
DELETE FROM route_cache
    WHERE departure_aerodrome = destination_aerodrome
    AND route_text = 'DCT';
