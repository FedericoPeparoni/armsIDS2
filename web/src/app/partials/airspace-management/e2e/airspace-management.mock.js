let airspaceNoId = {
    airspace_name: "WBFC",
    airspace_type: "FIR"
};

let airspaceWithId = {
    id: 2
};

Object.assign(airspaceWithId, airspaceNoId);

module.exports = [{
        request: {
            path: '/airspaces',
            method: 'GET'
        },
        response: {
            data: { content: [airspaceWithId]}
        }
    },
    {
        request: {
            path: '/airspaces',
            method: 'POST',
            data: airspaceNoId
        },
        response: {
            data: [
                { content: [airspaceWithId]}
            ]
        }
    },
    {
        request: {
            path: '/airspaces',
            method: 'PUT',
            data: airspaceWithId
        },
        response: {
            data: [
                { content: [airspaceWithId]}
            ]
        }
    },
    {
        request: {
            path: '/airspaces/2',
            method: 'DELETE'
        },
        response: ''
    }
];
