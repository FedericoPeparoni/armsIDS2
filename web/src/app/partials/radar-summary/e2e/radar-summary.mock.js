let radarSummaryWithNoId = {
    date: '2017-Mar-07',
    flight_identifier: 'A67',
    day_of_flight: '2017-Mar-07',
    departure_time: '1000',
    registration: 'T5',
    aircraft_type: 'S123',
    destination_aero_drome: 'Ottawa',
    departure_aero_drome: 'Ottawa',
    route: 'test',
    fir_entry_point: 'Y6',
    fir_entry_time: '1200',
    fir_exit_point: 'F6',
    fir_exit_time: '1302',
    flight_rule: 'VFR',
    flight_travel_category: 'Departure'
};

let radarSummaryWithId = {
    "id": 1
};

let aerodrome = {
    aerodrome_name: "Ottawa",
    aerodrome_category: "Test",
    aixm_flag: false,
    billing_center: "Test Billing Centre",
    geometry: {
        type: "Point",
        coordinates: [4, 4]
    },
    id: 1,
    is_default_billing_center: false
};

let aircraftType = {
    aircraft_image: null,
    aircraft_name: 'sputnik',
    aircraft_type: 'S123',
    id: 1,
    manufacturer: 'test',
    maximum_takeoff_weight: 2.4,
    wake_turbulence_category: {
        id: 1,
        name: 'L'
    }
};

Object.assign(radarSummaryWithId, radarSummaryWithNoId);

module.exports = [{
        request: {
            path: '/aerodromes',
            method: 'GET'
        },
        response: {
            data: { content: [aerodrome] }
        }
    },
    {
        request: {
            path: '/aircraft-types',
            method: 'GET'
        },
        response: {
            data: { content: [aircraftType] }
        }
    },
    {
        request: {
            path: '/radar-summaries',
            method: 'GET'
        },
        response: {
            data: { content: [radarSummaryWithId] }
        }
    },
    {
        request: {
            path: '/radar-summaries',
            method: 'POST',
            data: radarSummaryWithNoId
        },
        response: {
            data: { content: [radarSummaryWithId] }
        }
    },
    {
        request: {
            path: '/radar-summaries',
            method: 'PUT',
            data: radarSummaryWithId
        },
        response: {
            data: { content: [radarSummaryWithId] }
        }
    },
    {
        request: {
            path: '/radar-summaries/1',
            method: 'DELETE'
        },
        response: ''
    }
];
