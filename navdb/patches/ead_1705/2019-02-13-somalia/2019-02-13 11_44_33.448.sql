
-- 01 ====  WAYPOINT insertion  =====================================================================================================================

-- 02 ====  'UL776' AIRWAY update  ====  Source: ATFM SYSTEM CUSTOMIZATIONS AF.pdf  =================================================================

-- 03 ====  'UL452' AIRWAY insertion  ====  Source: ATFM SYSTEM CUSTOMIZATIONS AF.pdf  ==============================================================

-- 04 ====  'UL462' AIRWAY update  ====  Source: ATFM SYSTEM CUSTOMIZATIONS AF.pdf  =================================================================

-- 05 ====  'UL576' AIRWAY insertion  ====  Source: ATFM SYSTEM CUSTOMIZATIONS AF.pdf  ==============================================================

-- 06 ====  'UB540' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 75781  =======================================================

-- 07 ====  'G653' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 75860  ========================================================

-- 08 ====  'UA409' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 75861  =======================================================

-- 09 ====  'UM214' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 75844  =======================================================

-- 10 ====  'UM315' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 91182  =======================================================

-- 11 ====  'UP312' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 91182  =======================================================

-- 12 ====  ADHP insertion  ====  Source: ICAO ICARD 5LNC  ====  Comment: Story 70290  ==============================================================

-- 13 ====  'UT916' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Nov. 09, 2017  =====================================================

-- 14 ====  ADHP insertion  ====  Source: WIKIPEDIA  ====  Comment: TFS 94885  ======================================================================

-- 15 ====  ADHP insertion  ====  Source: WIKIPEDIA  ====  Comment: TFS 94884  ======================================================================

-- 16 ====  ADHP insertion  ====  Source: WIKIPEDIA  ====  Comment: Observed by k.gubbins while analyzing KCAA data  ================================
update airport set validto = '2018-10-24T00:00:00'::timestamp without time zone where ident = 'HC0006';

-- 17 ====  ADHP insertion  ====  Source: WIKIPEDIA  ====  Comment: Observed by d.panech while analyzing KCAA data  =================================

-- 18 ====  WAYPOINT insertion  ====  Source: j.nicolaas@dc-ansp.org  ====  Comment: see also misc_docs/2018-09-26--curacao-waypoint/  ==============

-- 19 ====  ADHP insertion  ====  Source: w.reiche@idscorporation.com  ====  Comment: see also TFS 101774  ==========================================
INSERT INTO navdb.airport (airport_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, nam, updateoper, source, codeicao, codedatum, elevation, p_geoiduom) VALUES ((SELECT MAX(airport_pk) + 1 FROM navdb.airport), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('37.28416667', '0.02641667'), 4326), '37.28416667','0.02641667','HKBK','BORANA','idsna','w.reiche@idscorporation.com','HKBK','WGE',NULL,'FT') RETURNING "airport_pk";
INSERT INTO navdb.airport (airport_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, nam, updateoper, source, codeicao, codedatum, elevation, p_geoiduom) VALUES ((SELECT MAX(airport_pk) + 1 FROM navdb.airport), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('37.150861', '0.347278'), 4326), '37.150861','0.347278','HKSC','MT.KENYA SAFARI CAMP','idsna','w.reiche@idscorporation.com','HKSC','WGE',NULL,'FT') RETURNING "airport_pk";
INSERT INTO navdb.airport (airport_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, nam, updateoper, source, codeicao, codedatum, elevation, p_geoiduom) VALUES ((SELECT MAX(airport_pk) + 1 FROM navdb.airport), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('34.784139', '1.030556'), 4326), '34.784139','1.030556','HKWQ','MOUNT RIDGE','idsna','w.reiche@idscorporation.com','HKWQ','WGE',NULL,'FT') RETURNING "airport_pk";
INSERT INTO navdb.airport (airport_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, nam, updateoper, source, codeicao, codedatum, elevation, p_geoiduom) VALUES ((SELECT MAX(airport_pk) + 1 FROM navdb.airport), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('38.117528', '2.585833'), 4326), '38.117528','2.585833','HKYT','KAMBOYO (TSAVO WEST)','idsna','w.reiche@idscorporation.com','HKYT','WGE',NULL,'FT') RETURNING "airport_pk";
