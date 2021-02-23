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
            ('A21N', 'A-321neo', 'AIRBUS', v_m_wake, 103.066078032595, 'system'),
            ('AC14', 'AC-14 Aerocommander', 'Rockwell', v_l_wake, 1.630317962, 'system'),
            ('AT47', 'ATR-42-600', 'ATR', v_m_wake, 20.50298451, 'system'),
            ('AT4T', 'Air Tractor AT-400', 'Air Tractor', v_l_wake, 2.067935427, 'system'),
            ('AT5', 'Air Tractor AT-500', 'Air Tractor', v_l_wake, 5.24038647, 'system'),
            ('AT66', 'Air Tractor AT-600', 'Air Tractor', v_l_wake, 6.172941572, 'system'),
            ('AT67', 'Air Tractor AT-600', 'Air Tractor', v_l_wake, 6.172941572, 'system'),
            ('AT73', 'ALENIA ATR-73', 'ATR', v_m_wake, 23.69968639, 'system'),
            ('B10', 'Martin B-10', 'Martin', v_l_wake, 8.201193803, 'system'),
            ('B358', 'Beechcraft Baron 58', 'Beechcraft', v_l_wake, 2.755777488, 'system'),
            ('B50', 'B-50 Superfortress', 'Boeing', v_m_wake, 86.49944609, 'system'),
            ('B754', 'Boeing 757-400', 'Boeing', v_m_wake, 136.4661012, 'system'),
            ('C520', 'Cessna Citation Mustang 510', 'Cessna', v_l_wake, 4.33208221, 'system'),
            ('CA8', 'Comp Air 8', 'Comp Air', v_l_wake, 2.799869927, 'system'),
            ('CE55', 'Cessna 550 Citation II', 'Cessna', v_l_wake, 7.549728005, 'system'),
            ('D95A', 'Beechcraft 95-D95A', 'Beechcraft', v_l_wake, 2.099902445, 'system'),
            ('DCH6', 'DHC-6 Twin Otter', 'De Havilland', v_l_wake, 6.250103342, 'system'),
            ('E295', 'Embraer E195-E2', 'Embraer', v_m_wake, 67.64331421, 'system'),
            ('HDJT', 'Honda HA-420 HondaJet', 'Honda Aircraft Company', v_l_wake, 5.35061757, 'system'),
            ('LYNX', 'Westland Lynx', 'Westland Helicopters', v_l_wake, 5.875317603, 'system'),
            ('MH60', 'MH-60 Jayhawk', 'Sikorsky', v_l_wake, 10.94153894, 'system'),
            ('MK11', 'Supermarine Spitfire MK-11', 'Supermarine', v_l_wake, 3.349923114, 'system'),
            ('P28', 'PA28 Cherokee', 'Piper', v_l_wake, 1.267657644, 'system'),
            ('P28S', 'PA28 Cherokee', 'Piper', v_l_wake, 1.267657644, 'system'),
            ('PC24', 'Pilatus PC-24', 'Pilatus Aircraft', v_l_wake, 9.149181259, 'system'),
            ('R100', 'Robin R1180', 'Robin', v_l_wake, 1.267657644, 'system'),
            ('T207', 'Cessna T207A Soloy Turbine 207', 'Cessna', v_l_wake, 1.900384155, 'system'),
            ('T328', 'Fairchild Dornier 328JET', 'Fairchild Dornier', v_m_wake, 17.26219018, 'system'),
            ('ULM', 'Ultralight - M', 'Unknown', v_l_wake, 0.551155498, 'system')
        ON CONFLICT DO NOTHING;


    -- update existing aircraft types
    UPDATE aircraft_types
            SET (aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight)
                        = ('A300B4-203', 'AIRBUS', v_h_wake, 181.8813142)
                        WHERE aircraft_type = 'A308';

    UPDATE aircraft_types
            SET (aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight)
                        = ('Beechcraft Skipper 77', 'Beechcraft', v_l_wake, 0.837756356)
                        WHERE aircraft_type = 'BE77';

    UPDATE aircraft_types
            SET (aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight)
                        = ('Aviat Husky A-1C', 'Aviat Aircraft', v_l_wake, 1.100106373)
                        WHERE aircraft_type = 'HUSK';

    UPDATE aircraft_types
            SET (aircraft_name, manufacturer, wake_turbulence_category, maximum_takeoff_weight)
                        = ('Vulcanair P68', 'Vulcanair', v_l_wake, 2.16052955)
                        WHERE aircraft_type = 'P68';
end $$;
