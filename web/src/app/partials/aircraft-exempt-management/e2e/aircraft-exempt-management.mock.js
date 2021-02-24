let exemptAircraftNoId = {
    'enroute_fees_exempt': 100,
    'late_arrival_fees_exempt': 100,
    'late_departure_fees_exempt': 100,
    'parking_fees_exempt': 100,
    'adult_fees_exempt': 100,
    'child_fees_exempt': 100,
    'approach_fees_exempt': null,
    'aerodrome_fees_exempt': null,
    'flight_notes': 'Test Notes',
    'aircraft_type': 'S123'
};

let exemptAircraftWithId = {
    'id': 2
};

let aircraftType = {
    'aircraft_image': null,
    'aircraft_name': 'sputnik',
    'aircraft_type': 'S123',
    'id': 1,
    'manufacturer': 'test',
    'maximum_takeoff_weight': 2.4,
    'wake_turbulence_category': {
        id: 1,
        name: 'L'
    }
};

Object.assign(exemptAircraftWithId, exemptAircraftNoId);

module.exports = [{
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
            path: '/aircraft-type-exemptions',
            method: 'GET'
        },
        response: {
            data: { content: [exemptAircraftWithId] }
        }
    },
    {
        request: {
            path: '/aircraft-type-exemptions',
            method: 'POST',
            data: exemptAircraftNoId
        },
        response: {
            data: [
                { content: [exemptAircraftWithId] }
            ]
        }
    },
    {
        request: {
            path: '/aircraft-type-exemptions',
            method: 'PUT',
            data: exemptAircraftWithId
        },
        response: {
            data: [
                { content: [exemptAircraftWithId] }
            ]
        }
    },
    {
        request: {
            path: '/aircraft-type-exemptions/2',
            method: 'DELETE'
        },
        response: ''
    }
];