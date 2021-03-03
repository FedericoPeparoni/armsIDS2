
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
INSERT INTO navdb.vor (vor_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, longitude, latitude, ident, txtname, updateoper, source) VALUES ((SELECT MAX(vor_pk) + 1 FROM navdb.vor), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_SetSRID(ST_MakePoint('37.62916666666667', '6.866111111111111'), 4326), '37.62916666666667','6.866111111111111','ETGOM ','ETGOM','idsna','icao icard 5lnc') RETURNING "vor_pk";
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((34.97540277777778 2.447927777777778,35.61273888888889 3.1075))', 4326), airway_pk = '7537', segmnt_pk = '49736', seq = '180', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '44.16095661591669', inmagtrack = NULL, revtruetrack = '224.19184356353347', revmagtrack = NULL, routedis = '54.90885186606371', uomdist = 'NM' WHERE airwayseg_pk = '89048';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((35.61273888888889 3.1075,36.084186111111116 3.9969722222222224))', 4326), airway_pk = '7537', segmnt_pk = '74178', seq = '190', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '28.02389948088524', inmagtrack = NULL, revtruetrack = '208.05311069545624', revmagtrack = NULL, routedis = '60.17003355311609', uomdist = 'NM' WHERE airwayseg_pk = '89049';
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((36.084186111111116 3.9969722222222224,37.62916666666667 6.866111111111111))', 4326), fromfixfea_pk = '9', fromfix_pk = '41416', tofixfea_pk = '3', tofix_pk = '3843', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '74204';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((36.084186111111116 3.9969722222222224,37.62916666666667 6.866111111111111))', 4326), airway_pk = '7537', segmnt_pk = '74204', seq = '200', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '28.284041615883524', inmagtrack = NULL, revtruetrack = '208.43033875245735', revmagtrack = NULL, routedis = '194.66754624985313', uomdist = 'NM' WHERE airwayseg_pk = '89057';
INSERT INTO navdb.segmnt (segmnt_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, fromfixfea_pk, fromfix_pk, tofixfea_pk, tofix_pk, updateoper, source, codetype) VALUES ((SELECT MAX(segmnt_pk) + 1 FROM navdb.segmnt), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((37.62916666666667 6.866111111111111,38.00833333333333 7.602222222222222))', 4326), '3','3843','9','121942','idsna','icao icard 5lnc','GRC') RETURNING "segmnt_pk";
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((37.62916666666667 6.866111111111111,38.00833333333333 7.602222222222222))', 4326), '7537','74210','210','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'27.196901414743515',NULL,'207.24464907485165',NULL,'49.43106272472462','NM') RETURNING "airwayseg_pk";
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((38.00833333333333 7.602222222222222,38.76991666666667 9.106194444444444))', 4326), fromfixfea_pk = '9', fromfix_pk = '121942', tofixfea_pk = '3', tofix_pk = '158', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '2600';
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((38.00833333333333 7.602222222222222,38.76991666666667 9.106194444444444))', 4326), '7537','2600','220','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'26.7080648201641',NULL,'206.8187282230858',NULL,'100.58858959729535','NM') RETURNING "airwayseg_pk";
INSERT INTO navdb.segmnt (segmnt_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, fromfixfea_pk, fromfix_pk, tofixfea_pk, tofix_pk, updateoper, source, codetype) VALUES ((SELECT MAX(segmnt_pk) + 1 FROM navdb.segmnt), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((38.76991666666667 9.106194444444444,38.76991666666667 9.106194444444444))', 4326), '3','158','3','158','idsna','icao icard 5lnc','GRC') RETURNING "segmnt_pk";
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((38.76991666666667 9.106194444444444,38.76991666666667 9.106194444444444))', 4326), airway_pk = '7537', segmnt_pk = '74211', seq = '230', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '0.0', inmagtrack = NULL, revtruetrack = '0.0', revmagtrack = NULL, routedis = '0.0', uomdist = 'NM' WHERE airwayseg_pk = '89058';
UPDATE navdb.airway SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((30.823055555555555 -15.98361111111111,30.735 -15.47,30.65 -14.833333333333334,29.661666666666665 -8.58,29.593333333333334 -8.13,29.25 -5.866666666666667,29.270866666666667 -5.1224388888888885,29.3 -3.985,29.322025 -3.346338888888889,29.649241666666665 -2.800680555555555,30.147558333333333 -1.968563888888889,30.74451388888889 -1.4512277777777778,31.235833333333336 -1.0,31.633333333333333 -0.6666666666666666,33.21350555555556 0.8123694444444445,32.43333333333333 0.05277777777777778,34.25 1.8263888888888888,34.97540277777778 2.447927777777778,35.61273888888889 3.1075,36.084186111111116 3.9969722222222224,37.62916666666667 6.866111111111111,38.00833333333333 7.602222222222222,38.76991666666667 9.106194444444444,38.76991666666667 9.106194444444444))', 4326), txtdesig = 'UL432', txtlocdesig = 'FQ-HK', name = NULL, updateoper = 'idsna', source = 'icao icard 5lnc' WHERE airway_pk = '7537';
INSERT INTO navdb.airway (airway_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, txtdesig, txtlocdesig, name, updateoper, source) VALUES ((SELECT MAX(airway_pk) + 1 FROM navdb.airway), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((40.082425 1.7468138888888889,39.53333333333333 0.4494444444444445,37.666666666666664 -0.8833333333333333,36.95422777777778 -1.2999027777777776))', 4326), 'UT932','UT932','UT932','idsna','icao icard 5lnc') RETURNING "airway_pk";
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((40.082425 1.7468138888888889,39.53333333333333 0.4494444444444445))', 4326), fromfixfea_pk = '3', fromfix_pk = '1403', tofixfea_pk = '9', tofix_pk = '139576', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '74206';
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((40.082425 1.7468138888888889,39.53333333333333 0.4494444444444445))', 4326), '13375','74206','10','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'203.0802290524737',NULL,'23.069705064043585',NULL,'84.19586974359234','NM') RETURNING "airwayseg_pk";
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((39.53333333333333 0.4494444444444445,37.666666666666664 -0.8833333333333333))', 4326), fromfixfea_pk = '9', fromfix_pk = '139576', tofixfea_pk = '9', tofix_pk = '139577', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '74207';
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((39.53333333333333 0.4494444444444445,37.666666666666664 -0.8833333333333333))', 4326), '13375','74207','20','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'234.65468428719015',NULL,'54.66175331704756',NULL,'137.5512872204174','NM') RETURNING "airwayseg_pk";
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((37.666666666666664 -0.8833333333333333,36.95422777777778 -1.2999027777777776))', 4326), fromfixfea_pk = '9', fromfix_pk = '139577', tofixfea_pk = '3', tofix_pk = '1402', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '74208';
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((37.666666666666664 -0.8833333333333333,36.95422777777778 -1.2999027777777776))', 4326), '13375','74208','30','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'239.84121731649486',NULL,'59.85479038168333',NULL,'49.51503327070033','NM') RETURNING "airwayseg_pk";
UPDATE navdb.airway SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((40.082425 1.7468138888888889,39.53333333333333 0.4494444444444445,37.666666666666664 -0.8833333333333333,36.95422777777778 -1.2999027777777776))', 4326), txtdesig = 'UT932', txtlocdesig = 'UT932', name = 'UT932', updateoper = 'idsna', source = 'icao icard 5lnc' WHERE airway_pk = '13375';
UPDATE navdb.segmnt SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((36.82696944444445 -1.5171666666666666,36.82696944444445 -1.5171666666666666))', 4326), fromfixfea_pk = '9', fromfix_pk = '107987', tofixfea_pk = '9', tofix_pk = '107987', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = 'GRC' WHERE segmnt_pk = '74209';
UPDATE navdb.airwayseg SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((36.82696944444445 -1.5171666666666666,36.82696944444445 -1.5171666666666666))', 4326), airway_pk = '9757', segmnt_pk = '74209', seq = '80', updateoper = 'idsna', source = 'icao icard 5lnc', codetype = NULL, levl = 'UPPER', upperlimit = NULL, uomupperlimit = NULL, codedistverupper = NULL, lowerlimit = NULL, uomlowerlimit = NULL, codedistverlower = NULL, intruetrack = '0.0', inmagtrack = NULL, revtruetrack = '0.0', revmagtrack = NULL, routedis = '0.0', uomdist = 'NM' WHERE airwayseg_pk = '89066';
INSERT INTO navdb.segmnt (segmnt_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, fromfixfea_pk, fromfix_pk, tofixfea_pk, tofix_pk, updateoper, source, codetype) VALUES ((SELECT MAX(segmnt_pk) + 1 FROM navdb.segmnt), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((36.82696944444445 -1.5171666666666666,36.95422777777778 -1.2999027777777776))', 4326), '9','107987','3','1402','idsna','icao icard 5lnc','GRC') RETURNING "segmnt_pk";
INSERT INTO navdb.airwayseg (airwayseg_pk, effectivedt, updatedt, validfrom, validto, bltype, geom, airway_pk, segmnt_pk, seq, updateoper, source, codetype, levl, upperlimit, uomupperlimit, codedistverupper, lowerlimit, uomlowerlimit, codedistverlower, intruetrack, inmagtrack, revtruetrack, revmagtrack, routedis, uomdist) VALUES ((SELECT MAX(airwayseg_pk) + 1 FROM navdb.airwayseg), current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, current_timestamp::timestamp without time zone, 'infinity'::timestamp without time zone, 'A', ST_MLineFromText('MULTILINESTRING((36.82696944444445 -1.5171666666666666,36.95422777777778 -1.2999027777777776))', 4326), '9757','74212','90','idsna','icao icard 5lnc','RNAV','UPPER',NULL,NULL,NULL,NULL,NULL,NULL,'30.520879649116306',NULL,'210.51775149332215',NULL,'15.058059790141469','NM') RETURNING "airwayseg_pk";
UPDATE navdb.airway SET effectivedt = current_timestamp::timestamp without time zone, updatedt = current_timestamp::timestamp without time zone, validfrom = current_timestamp::timestamp without time zone, validto = 'infinity'::timestamp without time zone, bltype = 'A', geom = ST_MLineFromText('MULTILINESTRING((39.24780555555556 -11.168149999999999,39.21400555555556 -8.5555,39.19271388888889 -6.888005555555555,38.46109722222222 -5.064797222222222,37.96136111111112 -3.816019444444444,37.900841666666665 -3.5857388888888893,37.56751666666667 -2.8524805555555557,36.82696944444445 -1.5171666666666666,36.82696944444445 -1.5171666666666666,36.95422777777778 -1.2999027777777776))', 4326), txtdesig = 'UL445', txtlocdesig = 'HT-HK', name = NULL, updateoper = 'idsna', source = 'icao icard 5lnc' WHERE airway_pk = '9757';
