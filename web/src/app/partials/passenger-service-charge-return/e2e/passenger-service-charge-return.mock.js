let returnWithNoId = {
    'flight_id': "2",
    'day_of_flight': "2017-Nov-24",
    'departure_time': "00:00",
    'transit_passengers': 1,
    'joining_passengers': 1,
    'children': 1,
    'chargeable_itl_passengers': 1,
    'chargeable_domestic_passengers': 1
};

let returnWithId = {
    "id": 1
};

Object.assign(returnWithId, returnWithNoId);

module.exports = [{
        request: {
            path: '/passenger-service-charge-return',
            method: 'GET'
        },
        response: {
            data: {content: [returnWithId]}
        }
    },
    {
        request: {
            path: '/passenger-service-charge-return',
            method: 'POST',
            data: returnWithNoId
        },
        response: {
            data: {content: [returnWithId]}
        }
    },
    {
        request: {
            path: '/passenger-service-charge-return',
            method: 'PUT',
            data: returnWithId
        },
        response: {
            data: {content: [returnWithId]}
        }
    },
    {
        request: {
            path: '/passenger-service-charge-return/1',
            method: 'DELETE'
        },
        response: ''
    }
];