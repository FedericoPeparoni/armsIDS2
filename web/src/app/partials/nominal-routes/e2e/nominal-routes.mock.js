let nominalRoutesNoId = {
    'type': 'FIR / FIR',
    'pointa': 'PointA',
    'pointb': 'PointB',
    'nominal_distance': 155
};

let nominalRoutesWithId = {
    'id': 1
};

Object.assign(nominalRoutesWithId, nominalRoutesNoId);

module.exports = [{
        request: {
            path: '/nominal-routes',
            method: 'GET'
        },
        response: {
            data: { content: [nominalRoutesWithId] }
        }
    },
    {
        request: {
            path: '/nominal-routes',
            method: 'POST',
            data: nominalRoutesNoId
        },
        response: {
            data: [
                nominalRoutesWithId
            ]
        }
    },
    {
        request: {
            path: '/nominal-routes',
            method: 'PUT',
            data: nominalRoutesWithId
        },
        response: {
            data: [
                nominalRoutesWithId
            ]
        }
    },
    {
        request: {
            path: '/nominal-routes/1',
            method: 'DELETE'
        },
        response: ''
    }
];