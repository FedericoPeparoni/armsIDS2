let atcMovementLogWithNoId = {
    date_of_contact: '2017-Mar-07',
    registration: 'R456',
    operator_identifier: '167',
    route: 'ABCD/DEF',
    flight_id: 'A123',
    aircraft_type: 'A320',
    departure_aerodrome: 'Ottawa',
    destination_aerodrome: 'Ottawa',
    fir_entry_point: 'Z7',
    fir_entry_time: '1010',
    fir_mid_point: 'Z7',
    fir_mid_time: '1010',
    fir_exit_point: 'Z7',
    fir_exit_time: '1010',
    flight_level: '1000',
    wake_turbulence:'L',
    flight_category: 'sched',
    flight_type: 'normal',
    day_of_flight: '2017-Mar-07',
    departure_time: '1010'
};

let atcMovementLogWithId = {
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

Object.assign(atcMovementLogWithId, atcMovementLogWithNoId);

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
            path: '/atc-movement-log',
            method: 'GET'
        },
        response: {
            data: { content: [atcMovementLogWithId] }
        }
    },
    {
        request: {
            path: '/atc-movement-log',
            method: 'POST',
            data: atcMovementLogWithNoId
        },
        response: {
            data: { content: [atcMovementLogWithId] }
        }
    },
    {
        request: {
            path: '/atc-movement-log',
            method: 'PUT',
            data: atcMovementLogWithId
        },
        response: {
            data: { content: [atcMovementLogWithId] }
        }
    },
    {
        request: {
            path: '/atc-movement-log/1',
            method: 'DELETE'
        },
        response: ''
    }
];
