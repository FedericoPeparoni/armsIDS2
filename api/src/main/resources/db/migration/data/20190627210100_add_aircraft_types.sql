-- INSERT INTO aircraft_types WHERE NOT EXIST ALREADY
--
-- Modify this file to add additional default aircraft types. It can be executed multiple
-- times and only missing aircraft will be added each time.
do $$
declare
    v_h_wake integer;
    v_j_wake integer;
    v_l_wake integer;
    v_m_wake integer;
begin

    -- define wake turbulence category ids that are necessary to insert aircraft types
    SELECT wtc.id INTO v_h_wake FROM wake_turbulence_categories wtc WHERE name = 'H';
    SELECT wtc.id INTO v_j_wake FROM wake_turbulence_categories wtc WHERE name = 'J';
    SELECT wtc.id INTO v_l_wake FROM wake_turbulence_categories wtc WHERE name = 'L';
    SELECT wtc.id INTO v_m_wake FROM wake_turbulence_categories wtc WHERE name = 'M';

    -- insert aircraft types if they do not exist already
    -- existing are ignored and left as-is
    INSERT INTO aircraft_types
            (aircraft_type, aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight, created_by)
        VALUES
            ('A22', 'A-22 Foxbat', 'Aeroprak', v_l_wake, 0.496039947750459, 'system'),
            ('A350', 'A350 XWB', 'Airbus', v_h_wake, 308.647078600286, 'system'),
            ('A359', 'A350-900', 'Airbus', v_h_wake, 308.647078600286, 'system'),
            ('A360', 'A340-600', 'Airbus', v_h_wake, 420.882179489299, 'system'),
            ('A400', 'A-400', 'Airbus', v_h_wake, 155.425850295144, 'system'),
            ('AS35', 'AS 350/355', 'Aerospatiale', v_l_wake, 2.48019973875229, 'system'),
            ('AT6T', 'Air Tractor 602', 'Air Tractor', v_m_wake, 6.17294157200571, 'system'),
            ('B19', 'B19 Musketeer', 'Beechcraft', v_l_wake, 1.12435721490104, 'system'),
            ('B206', 'Bell 206', 'Bell', v_l_wake, 1.59945325374648, 'system'),
            ('B300', 'Super King Air', 'Beechcraft', v_l_wake, 6.70976702657121, 'system'),
            ('B505', 'Bell 505 Jet Ranger', 'Bell', v_l_wake, 1.8397570506567, 'system'),
            ('B38M', '737 MAX 8', 'Boeing', v_m_wake, 90.6000429901288, 'system'),
            ('B58', 'B-58 Hustler', 'Convair', v_m_wake, 88.4494342388818, 'system'),
            ('B74D', 'Boeing 747-400', 'Boeing', v_h_wake, 416.874176711476, 'system'),
            ('B787', 'Boeing 787', 'Boeing', v_h_wake, 251.249745090582, 'system'),
            ('B90L', 'King Air B-90L', 'Beechcraft', v_l_wake, 13.007269741012, 'system'),
            ('BE12', 'Beriev BE-12', 'Beriev', v_m_wake, 39.6831958200367, 'system'),
            ('C180', 'Cessna 180 Skywagon', 'Cessna', v_l_wake, 1.67551271240155, 'system'),
            ('C185', 'Cessna 185 Skywagon', 'Cessna', v_l_wake, 1.67551271240155, 'system'),
            ('C401', 'Cessna 401', 'Cessna', v_l_wake, 3.42488026146817, 'system'),
            ('C68A', '680A Citation', 'Cessna', v_l_wake, 1.5112683741464, 'system'),
            ('CH70', 'Zenith STOL CH 701', 'Zenith Aircraft Company', v_l_wake, 0.550053186505509, 'system'),
            ('CH80', 'Zenith STOL CH 801', 'Zenith Aircraft Company', v_l_wake, 1.10010637301102, 'system'),
            ('CL35', 'BD-100 Challenger 350', 'Bombardier', v_m_wake, 20.3001592839388, 'system'),
            ('CRJ9', 'CRJ-900', 'Bombardier', v_m_wake, 40.2343513175372, 'system'),
            ('D28T', 'Dornier Do-28D-6 Turbo Skyservant', 'Dornier Flugzeugwerke', v_l_wake, 4.79505282825444, 'system'),
            ('DHC2', 'Beaver Mk III', 'De Havilland Canada', v_l_wake, 2.69074113879749, 'system'),
            ('DHC8', 'DHC-8-400 Dash 8', 'De Havilland Canada', v_m_wake, 30.1261594933779, 'system'),
            ('DHD8', 'DHC-8-400 Dash 8', 'De Havilland Canada', v_m_wake, 30.1261594933779, 'system'),
            ('E35L', 'EMB-135BJ', 'Embraer', v_m_wake, 20.9439089050194, 'system'),
            ('E90', 'EMB-190', 'Embraer', v_m_wake, 57.0997095410528, 'system'),
            ('EDGE', 'EDGE 540 Aerobatic Aircraft', 'Edge', v_l_wake, 0.55115549750051, 'system'),
            ('F156', 'Fieseler Fi 156', 'Fiesler', v_l_wake, 1.32387550499622, 'system'),
            ('F50', 'Fokker 50', 'Fokker', v_m_wake, 22.9501149159212, 'system'),
            ('GR51', 'Cameron P-51G Grand 51', 'Cameron & Sons Aircraft', v_l_wake, 1.39993496365129, 'system'),
            ('A748', 'Hawker Siddeley HS-748', 'Hawker Siddeley', v_m_wake, 23.2499435065615, 'system'),
            ('J32', 'Aerospace Jetstream 31/31', 'British Aerospace', v_l_wake, 7.66106141525709, 'system'),
            ('K8', 'Karakorum K-8', 'Karakorum', v_l_wake, 4.77300660835442, 'system'),
            ('KR2', 'Rand Robinson KR-2', 'Rand Robinson', v_l_wake, 0.396831958200367, 'system'),
            ('L29B', 'Jetstart 2/731', 'Lockheed Martin Corp', v_m_wake, 21.8257577010202, 'system'),
            ('L410', 'L-410', 'LET', v_l_wake, 7.05479036800653, 'system'),
            ('M20P', 'Mooney M-20', 'Mooney', v_l_wake, 1.68433120036156, 'system'),
            ('M24', 'Magni M-24 Orion', 'Magni', v_l_wake, 0.496039947750459, 'system'),
            ('MA60', 'Modern Ark 60', 'XIAN', v_m_wake, 52.977066419749, 'system'),
            ('MI8', 'Mil Mi-8', 'Russian Military', v_m_wake, 14.3300429350133, 'system'),
            ('MI17', 'Mil Mi-17', 'Russian', v_m_wake, 31.5922331167292, 'system'),
            ('MI18', 'Mil Mi-18', 'Russian', v_l_wake, 8.0027778237074, 'system'),
            ('P28B', 'Piper PA-28-235 Cherokee', 'Piper', v_l_wake, 1.49914295320139, 'system'),
            ('P3', 'Orion', 'Lockheed Martin Corp', v_m_wake, 70.9888280780657, 'system'),
            ('P68B', 'P68B', 'Vulcanair', v_l_wake, 2.29721611358213, 'system'),
            ('P68C', 'P68C', 'Vulcanair', v_l_wake, 2.29721611358213, 'system'),
            ('P92', 'P92 Echo', 'Tecnam', v_l_wake, 0.606271047250561, 'system'),
            ('PA14', 'Piper PA-14 Family Cruiser', 'Piper', v_l_wake, 0.924838924805856, 'system'),
            ('PA23', 'Piper PA-23 Apache/Aztec', 'Piper', v_l_wake, 2.60145394820241, 'system'),
            ('PA39', 'Piper PA-39 Twin Comanche', 'Piper', v_l_wake, 1.80007385483667, 'system'),
            ('PA68', 'Vulcanair P68', 'Vulcanair', v_l_wake, 2.19359888005203, 'system'),
            ('PTS2', 'Pitts Special S2', 'Curtis Pitts', v_l_wake, 0.812403203315752, 'system'),
            ('PUMA', '330 Puma', 'Aerospatiale', v_l_wake, 7.71617696500714, 'system'),
            ('PZ04', 'PZL-104 Wilga', 'European Aeronautic Defence and Space Company', v_l_wake, 1.43300429350133, 'system'),
            ('Q400', 'Dash 8 Q400', 'Bombardier', v_m_wake, 31.5260944570292, 'system'),
            ('RH44', 'Robinson Helicopter R44', 'Robinson', v_l_wake, 1.25002066833116, 'system'),
            ('S2R', 'Ayres Thrush SR2', 'Ayres', v_l_wake, 3.45023341435319, 'system'),
            ('SS2T', 'Ayres Thrush SS2T', 'Ayres', v_l_wake, 3.45023341435319, 'system'),
            ('S61N', 'S-61', 'Sikorsky', v_l_wake, 5.8422482735054, 'system'),
            ('SA33', '330 Puma', 'Aerospatiale', v_l_wake, 7.71617696500714, 'system'),
            ('SLG4', 'Sling 4', 'The Airplace Factory', v_l_wake, 1.01412611540094, 'system'),
            ('UH1H', 'UH-1 Iroquois', 'Bell', v_l_wake, 4.74985807745939, 'system'),
            ('VO10', 'Commander 100', 'Rockwell', v_l_wake, 1.32497781599123, 'system'),
            ('Y12', 'Y-12', 'Harbin', v_l_wake, 5.8422482735054, 'system'),
            ('Z9', 'Z-9', 'Harbin', v_l_wake, 4.51947507950418, 'system'),
            ('Z9EH', 'Z-9 EH', 'Harbin', v_l_wake, 4.51947507950418, 'system'),
            ('A220', 'A220-100 Airbus', 'Airbus', v_m_wake, 69.4996059238193, 'system'),
            ('BCS3', 'A220-300 Airbus', 'Airbus', v_m_wake, 76.9997299338062, 'system')
        ON CONFLICT DO NOTHING;

end $$;
