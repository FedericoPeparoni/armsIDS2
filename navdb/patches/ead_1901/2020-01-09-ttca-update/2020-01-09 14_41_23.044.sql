
-- 01 ====  WAYPOINT insertion  ====  Comment: Missing waypoints for Botswana  ======================================================================

-- 02 ====  ADHP insertion  ====  Source: ICAO ICARD 5LNC  ====  Comment: TFS 70290  ================================================================

-- 03 ====  ADHP insertion  ====  Source: WIKIPEDIA  ====  Comment: Observed by k.gubbins while analyzing KCAA data  ================================

-- 04 ====  ADHP insertion  ====  Source: WIKIPEDIA  ====  Comment: Observed by d.panech while analyzing KCAA data  =================================

-- 05 ====  ADHP insertion  ====  Source: w.reiche@idscorporation.com  ====  Comment: TFS 101774  ===================================================

-- 06 ====  ADHP insertion  ====  Source: w.reiche@idscorporation.com  ====  Comment: TFS 105875 South Sudan  =======================================

-- 07 ====  'UL432' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: TFS 107785  ========================================================

-- 08 ====  ADHP insertion  ====  Source: w.reiche@idscorporation.com  ====  Comment: TFS 114451 Namibia  ===========================================

-- 09 ====  'UM731' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Zambia enter/exit problems  ========================================

-- 10 ====  'UM315' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Zambia enter/exit problems  ========================================

-- 11 ====  'UA405' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Zambia enter/exit problems  ========================================

-- 12 ====  UP312 AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Zambia enter/exit problems  ==========================================

-- 13 ====  UR782 AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Zambia enter/exit problems  ==========================================

-- 14 ====  'UL432' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Jul 16, 2019  ======================================================

-- 15 ====  'UA727' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Jul 16, 2019  ======================================================

-- 16 ====  'UT932' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Jul 16, 2019  ======================================================

-- 17 ====  'UL445' AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: Jul 16, 2019  ======================================================
INSERT INTO navdb.airport (airport_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, nam, updateoper, source, codeicao, codedatum, elevation, p_geoiduom) VALUES ((SELECT MAX(airport_pk) + 1 FROM navdb.airport), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('58.112777', '6.809722'), 4326), '58.112777','6.809722','SYGO','OGLE','idsna','http://ourairports.com','SYGO','WGE',NULL,'FT') RETURNING "airport_pk";
INSERT INTO navdb.airport (airport_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, nam, updateoper, source, codeicao, codedatum, elevation, p_geoiduom) VALUES ((SELECT MAX(airport_pk) + 1 FROM navdb.airport), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('61.211111', '13.144444'), 4326), '61.211111','13.144444','TVSV','ST VINCENT','idsna','http://ourairports.com','TVSV','WGE',NULL,'FT') RETURNING "airport_pk";

-- 18 ====  UM791 AIRWAY update  ====  Source: ICAO ICARD 5LNC  ====  Comment: TTCA enter/exit problems  ============================================
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-34.927299166666664 -8.136580555555556,-35.851166666666664 -7.3035))', 4326), airway_pk = '4486', segmnt_pk = '16113', seq = '10', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '312.0505620101971', inmagtrack = NULL, revtruetrack = '132.17467366330808', revmagtrack = NULL, routedis = '74.18434442947084', uomdist = 'NM' WHERE airwayseg_pk = '18386';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-35.851166666666664 -7.3035,-35.939 -7.2283333333333335))', 4326), airway_pk = '4486', segmnt_pk = '40229', seq = '20', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '310.5920589518123', inmagtrack = NULL, revtruetrack = '130.60316763692828', revmagtrack = NULL, routedis = '6.8976135404843415', uomdist = 'NM' WHERE airwayseg_pk = '46659';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-35.939 -7.2283333333333335,-37.4605 -5.985))', 4326), airway_pk = '4486', segmnt_pk = '40230', seq = '30', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.1661055813539', inmagtrack = NULL, revtruetrack = '129.34117888548948', revmagtrack = NULL, routedis = '117.32686213473974', uomdist = 'NM' WHERE airwayseg_pk = '46660';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-37.4605 -5.985,-37.520833333333336 -5.9350000000000005))', 4326), airway_pk = '4486', segmnt_pk = '34460', seq = '40', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.6119310902038', inmagtrack = NULL, revtruetrack = '129.61819575059113', revmagtrack = NULL, routedis = '4.682348338988121', uomdist = 'NM' WHERE airwayseg_pk = '39671';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-37.520833333333336 -5.9350000000000005,-37.64983333333333 -5.829))', 4326), airway_pk = '4486', segmnt_pk = '34461', seq = '50', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.3650712201423', inmagtrack = NULL, revtruetrack = '129.37829115610927', revmagtrack = NULL, routedis = '9.977843356592333', uomdist = 'NM' WHERE airwayseg_pk = '39672';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-37.64983333333333 -5.829,-37.7695 -5.730166666666666))', 4326), airway_pk = '4486', segmnt_pk = '16114', seq = '60', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.5037683257487', inmagtrack = NULL, revtruetrack = '129.51581898058643', revmagtrack = NULL, routedis = '9.27598564786231', uomdist = 'NM' WHERE airwayseg_pk = '18387';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-37.7695 -5.730166666666666,-38.515166666666666 -5.107166666666667))', 4326), airway_pk = '4486', segmnt_pk = '15825', seq = '70', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.7814079966241', inmagtrack = NULL, revtruetrack = '129.8518253182518', revmagtrack = NULL, routedis = '58.09361029571922', uomdist = 'NM' WHERE airwayseg_pk = '18087';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-38.515166666666666 -5.107166666666667,-38.781166666666664 -4.884))', 4326), airway_pk = '4486', segmnt_pk = '16329', seq = '80', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.9033985197092', inmagtrack = NULL, revtruetrack = '129.92656160621283', revmagtrack = NULL, routedis = '20.76712657312473', uomdist = 'NM' WHERE airwayseg_pk = '18617';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-38.781166666666664 -4.884,-38.90683333333333 -4.778333333333333))', 4326), airway_pk = '4486', segmnt_pk = '16115', seq = '90', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.96569695378355', inmagtrack = NULL, revtruetrack = '129.9762805946492', revmagtrack = NULL, routedis = '9.821472244075595', uomdist = 'NM' WHERE airwayseg_pk = '18388';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-38.90683333333333 -4.778333333333333,-39.027833333333334 -4.676333333333333))', 4326), airway_pk = '4486', segmnt_pk = '34462', seq = '100', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '310.03293030946367', inmagtrack = NULL, revtruetrack = '130.04290240530435', revmagtrack = NULL, routedis = '9.467448997105292', uomdist = 'NM' WHERE airwayseg_pk = '39673';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-39.027833333333334 -4.676333333333333,-39.13466666666667 -4.586166666666667))', 4326), airway_pk = '4486', segmnt_pk = '34463', seq = '110', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '310.0637605474709', inmagtrack = NULL, revtruetrack = '130.07238655064538', revmagtrack = NULL, routedis = '8.363840741058315', uomdist = 'NM' WHERE airwayseg_pk = '39674';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-39.13466666666667 -4.586166666666667,-39.667833333333334 -4.121166666666666))', 4326), airway_pk = '4486', segmnt_pk = '16330', seq = '120', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '310.9652109522289', inmagtrack = NULL, revtruetrack = '131.00568570969196', revmagtrack = NULL, routedis = '42.33231966330076', uomdist = 'NM' WHERE airwayseg_pk = '18618';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-39.667833333333334 -4.121166666666666,-40.098333333333336 -3.7398333333333333))', 4326), airway_pk = '4486', segmnt_pk = '16017', seq = '130', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.3961932273573', inmagtrack = NULL, revtruetrack = '131.42570274446183', revmagtrack = NULL, routedis = '34.421985106982184', uomdist = 'NM' WHERE airwayseg_pk = '18283';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-40.098333333333336 -3.7398333333333333,-40.522333333333336 -3.3648333333333333))', 4326), airway_pk = '4486', segmnt_pk = '16331', seq = '140', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.3417924051616', inmagtrack = NULL, revtruetrack = '131.36806379117965', revmagtrack = NULL, routedis = '33.887587652854215', uomdist = 'NM' WHERE airwayseg_pk = '18619';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-40.522333333333336 -3.3648333333333333,-41.48383333333334 -2.506666666666667))', 4326), airway_pk = '4486', segmnt_pk = '33652', seq = '150', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.57102359882856', inmagtrack = NULL, revtruetrack = '131.6202704133555', revmagtrack = NULL, routedis = '77.17974868830724', uomdist = 'NM' WHERE airwayseg_pk = '38772';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-41.48383333333334 -2.506666666666667,-41.8555 -2.1745))', 4326), airway_pk = '4486', segmnt_pk = '33653', seq = '160', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.6130482151036', inmagtrack = NULL, revtruetrack = '131.62822702261795', revmagtrack = NULL, routedis = '29.859255683690066', uomdist = 'NM' WHERE airwayseg_pk = '38773';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-41.8555 -2.1745,-42.77883333333333 -1.3585))', 4326), airway_pk = '4486', segmnt_pk = '25244', seq = '170', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.27650092238434', inmagtrack = NULL, revtruetrack = '131.30496526975946', revmagtrack = NULL, routedis = '73.83001653817225', uomdist = 'NM' WHERE airwayseg_pk = '28778';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-42.77883333333333 -1.3585,-43.588 -0.6386666666666667))', 4326), airway_pk = '4486', segmnt_pk = '50716', seq = '180', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.46186663493256', inmagtrack = NULL, revtruetrack = '131.47596905184687', revmagtrack = NULL, routedis = '64.89945630179697', uomdist = 'NM' WHERE airwayseg_pk = '59028';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-43.588 -0.6386666666666667,-45.08416666666667 0.6928333333333333))', 4326), airway_pk = '4486', segmnt_pk = '50717', seq = '190', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.4742546185292', inmagtrack = NULL, revtruetrack = '131.4735473021156', revmagtrack = NULL, routedis = '120.02971562553185', uomdist = 'NM' WHERE airwayseg_pk = '59029';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-45.08416666666667 0.6928333333333333,-45.38033333333333 0.9563333333333334))', 4326), airway_pk = '4486', segmnt_pk = '33654', seq = '200', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.4735304669564', inmagtrack = NULL, revtruetrack = '131.46926825314424', revmagtrack = NULL, routedis = '23.75605188835637', uomdist = 'NM' WHERE airwayseg_pk = '38774';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-45.38033333333333 0.9563333333333334,-46.61066666666667 2.0501666666666667))', 4326), airway_pk = '4486', segmnt_pk = '33655', seq = '210', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.47234908390044', inmagtrack = NULL, revtruetrack = '131.44007022246277', revmagtrack = NULL, routedis = '98.64191444512906', uomdist = 'NM' WHERE airwayseg_pk = '38775';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-46.61066666666667 2.0501666666666667,-49.634166666666665 4.733333333333333))', 4326), airway_pk = '4486', segmnt_pk = '33656', seq = '220', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '311.5268764294367', inmagtrack = NULL, revtruetrack = '131.34790726639076', revmagtrack = NULL, routedis = '242.0179541596879', uomdist = 'NM' WHERE airwayseg_pk = '38776';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-49.634166666666665 4.733333333333333,-51.70527777777778 6.71))', 4326), airway_pk = '4486', segmnt_pk = '18470', seq = '230', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '313.71476062212014', inmagtrack = NULL, revtruetrack = '133.50822579817', revmagtrack = NULL, routedis = '171.09663331732182', uomdist = 'NM' WHERE airwayseg_pk = '21068';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-51.70527777777778 6.71,-54.0 8.618333333333334))', 4326), airway_pk = '4486', segmnt_pk = '18471', seq = '240', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.9624648859619', inmagtrack = NULL, revtruetrack = '129.65634298313168', revmagtrack = NULL, routedis = '177.96984313279967', uomdist = 'NM' WHERE airwayseg_pk = '21069';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-54.0 8.618333333333334,-54.736666666666665 9.236666666666666))', 4326), airway_pk = '4486', segmnt_pk = '22736', seq = '250', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '310.2246110487711', inmagtrack = NULL, revtruetrack = '130.1102885411798', revmagtrack = NULL, routedis = '57.247840694292655', uomdist = 'NM' WHERE airwayseg_pk = '25902';
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-54.736666666666665 9.236666666666666,-56.52861111111111 10.706944444444444))', 4326), fromfixfea_pk = '9', fromfix_pk = '61486', tofixfea_pk = '9', tofix_pk = '107818', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '20943';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-54.736666666666665 9.236666666666666,-56.52861111111111 10.706944444444444))', 4326), airway_pk = '4486', segmnt_pk = '20943', seq = '260', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '309.76556386128', inmagtrack = NULL, revtruetrack = '129.45521405273922', revmagtrack = NULL, routedis = '137.71580538361394', uomdist = 'NM' WHERE airwayseg_pk = '23900';
INSERT INTO navdb.segmnt (segmnt_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, fromfixfea_pk, fromfix_pk, tofixfea_pk, tofix_pk, updateoper, source, codetype) VALUES ((SELECT MAX(segmnt_pk) + 1 FROM navdb.segmnt), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((-56.52861111111111 10.706944444444444,-59.483827777777776 13.07505))', 4326), '9','107818','3','2563','idsna','icao icard 5lnc','GRC') RETURNING "segmnt_pk";
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((-56.52861111111111 10.706944444444444,-59.483827777777776 13.07505))', 4326), '4486','74213','270','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'309.42999949119707',NULL,'128.82081506201067',NULL,'224.10830344279861','NM') RETURNING "airwayseg_pk";
UPDATE navdb.airway SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((-34.927299166666664 -8.136580555555556,-35.851166666666664 -7.3035,-35.939 -7.2283333333333335,-37.4605 -5.985,-37.520833333333336 -5.9350000000000005,-37.64983333333333 -5.829,-37.7695 -5.730166666666666,-38.515166666666666 -5.107166666666667,-38.781166666666664 -4.884,-38.90683333333333 -4.778333333333333,-39.027833333333334 -4.676333333333333,-39.13466666666667 -4.586166666666667,-39.667833333333334 -4.121166666666666,-40.098333333333336 -3.7398333333333333,-40.522333333333336 -3.3648333333333333,-41.48383333333334 -2.506666666666667,-41.8555 -2.1745,-42.77883333333333 -1.3585,-43.588 -0.6386666666666667,-45.08416666666667 0.6928333333333333,-45.38033333333333 0.9563333333333334,-46.61066666666667 2.0501666666666667,-49.634166666666665 4.733333333333333,-51.70527777777778 6.71,-54.0 8.618333333333334,-54.736666666666665 9.236666666666666,-56.52861111111111 10.706944444444444,-59.483827777777776 13.07505))', 4326), txtdesig = 'UM791', txtlocdesig = 'SB-TT', name = NULL, updateoper = 'idsna', source = 'icao icard 5lnc' WHERE airway_pk = '4486';
