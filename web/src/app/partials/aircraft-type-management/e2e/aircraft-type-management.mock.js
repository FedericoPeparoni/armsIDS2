let aircraftTypeNoId = {
    'aircraft_type': 'T123',
    'aircraft_name': 'Test-123',
    'manufacturer': 'AIRBUS Test',
    'wake_turbulence_category': 'T',
    'maximum_takeoff_weight': 250.50
};

let aircraftTypeWithId = {
    'id': 1
};

let wakeTurbulence = {
    'id': 1,
    'name': 'T'
}

Object.assign(aircraftTypeWithId, aircraftTypeNoId);

module.exports = [{
        request: {
            path: '/aircraft-types',
            method: 'GET'
        },
        response: {
            data: {content: [aircraftTypeWithId]}
        }
    },
    {
        request: {
            path: '/wake-turbulence-categories',
            method: 'GET'
        },
        response: {
            data: {content: [wakeTurbulence]}
        }
    },
    {
        request: {
            path: '/aircraft-types',
            method: 'POST',
            data: aircraftTypeNoId
        },
        response: {
            data: {content: [aircraftTypeWithId]}
        }
    },
    {
        request: {
            path: '/aircraft-types',
            method: 'PUT',
            data: aircraftTypeWithId
        },
        response: {
            data: {content: [aircraftTypeWithId]}
        }
    },
    {
        request: {
            path: '/aircraft-types/1',
            method: 'DELETE'
        },
        response: ''
    }
];